package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class Server {

    public static File users;

    public static void main(String[] args) throws Exception {

        try (var listener = new ServerSocket(51730)) {
            System.out.println("The game server is running...");
            users = new File("users.csv");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }

    }

    private static class Handler implements Runnable {
        private Socket socket;
        private String[] listOfUsers;
        private HashSet<String> h;
        private List<String[]> records;


        Handler(Socket socket) throws FileNotFoundException {
            this.socket = socket;
            this.h = new HashSet<String>();

        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try (
                    PrintWriter out =
                            new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

                    // create CSVWriter object filewriter object as parameter
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));
                    CSVReader reader = new CSVReader(new FileReader(users));
            ) {
                String readUsername;
                String readPassword;
                String message;
                boolean userExists = false;
                while (!userExists){

                    while (((readUsername = in.readLine()) != null) &&
                            ((readPassword = in.readLine()) != null)) {
                        String[] nextRecord;
                        userExists = false;

                        while ((((nextRecord = reader.readNext())) != null) && !userExists) {
                            if (nextRecord[0].equals(readUsername)) {
                                System.out.println("a client entered a already taken username");
                                out.println("Username Already Taken");
                                userExists = true;
                            }
                        }
                            System.out.println("Username accepted");
                            String[] data = {readUsername, readPassword};
                            writer.writeNext(data);
                            writer.close();
                    }

                }
            } catch (IOException | CsvValidationException e) {
                System.out.println("Exception caught when trying to listen on port " + socket
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }


        }
    }
}


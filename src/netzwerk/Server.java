package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();

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
        private String username;


        Handler(Socket socket) throws IOException {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                registerUser();
                chat();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //continue run logic

        }

        private void chat() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));


                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.readLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + username + ": " + input);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void registerUser() throws IOException {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    // create CSVWriter object filewriter object as parameter
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));

            ) {
                String readUsername;
                String readPassword;
                String userChoice = in.readLine();
                if (userChoice.equals("/register")) {
                    boolean userExists = false;
                    while (!userExists) {

                        while (((readUsername = in.readLine()) != null) &&
                                ((readPassword = in.readLine()) != null)) {

                            String[] nextRecord;
                            userExists = false;
                            CSVReader reader = new CSVReader(new FileReader(users));
                            while ((((nextRecord = reader.readNext())) != null) && userExists == false) {
                                if (nextRecord[0].equals(readUsername)) {
                                    System.out.println("a client entered an already taken username");
                                    out.println("Username Already Taken");
                                    userExists = true;
                                }
                            }
                            if (userExists == false) {

                                String[] data = {readUsername, readPassword};
                                System.out.println("-----REGISTRATION SUCCESSFUL----");
                                out.println("-----REGISTRATION SUCCESSFUL----");
                                username = readUsername;
                                writer.writeNext(data);
                                writer.flush();
                                userExists = true;
                                //break;
                            }
                        }
                    }
                }
                else {
                    boolean loginCheck = false;
                    while (((readUsername = in.readLine()) != null) &&
                            ((readPassword = in.readLine()) != null)) {
                        //System.gc();
                        String[] nextRecord;
                        CSVReader reader = new CSVReader(new FileReader(users));

                        while ((((nextRecord = reader.readNext())) != null) && loginCheck == false) {
                            if (nextRecord[0].equals(readUsername)) {
                                if (nextRecord[1].equals(readPassword))
                                    loginCheck = true;
                            }
                        }
                        if (loginCheck == true) {
                            out.println(readUsername + " Login Accepted!!");
                            username = readUsername;
                            writers.add(out);
                            for (PrintWriter printWriter : writers) {
                                printWriter.println(readUsername + " has joined");
                            }
                            //names
                            synchronized (names) {
                                if (!readUsername.isBlank() && !names.contains(readUsername)) {
                                    names.add(readUsername);
                                    System.out.println(names);

                                }
                            }

                            System.out.println("Client: "+socket +" logged in with username " +readUsername);

                        }
                        else
                            out.println("Login failed. Please try again.");
                    }
                }

            } catch (
                    CsvValidationException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}


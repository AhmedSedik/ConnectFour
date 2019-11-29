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
    private static PrintWriter out; private static BufferedReader in;
    public static File users;

    public static void main(String[] args) throws Exception {

        try (var listener = new ServerSocket(51730)) {
            System.out.println("The game server is running...");
            users = new File("users.csv");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                pool.execute(new Handler(listener.accept(), out,in));
            }
        }

    }

    private static class Handler implements Runnable {
        private Socket socket;
        private String[] listOfUsers;
        private HashSet<String> h;
        private List<String[]> records;
        private String username;
        private PrintWriter out;
        private BufferedReader in;

        public Handler(Socket socket, PrintWriter out, BufferedReader in) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(
                          new InputStreamReader(socket.getInputStream()));
        }

        /*Handler(Socket socket) throws IOException {
            this.socket = socket;
        }*/

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                registerUser();
                chat();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void chat() throws IOException {
            System.out.println(socket + "has joined the chat");
            writers.add(out);
            for (PrintWriter printWriter : writers) {
                printWriter.println(username + " has joined");
                printWriter.flush();
            }
            //names
            synchronized (names) {
                if (!username.isBlank() && !names.contains(username)) {
                    names.add(username);
                    out.println(names);
                }
            }
            // Accept messages from this client and broadcast them.
            while (true) {
                String input = in.readLine();
                if (input.toLowerCase().startsWith("/quit"))
                    break;

                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + username + ": " + input);
                    writer.flush();
                }
            }
            System.out.println(socket + "has left the chat");
            names.remove(username);
            for (PrintWriter writer : writers) {
                writer.println(username + "has left the chat");
                writer.flush();
            }
            writers.remove(out);
        }

        public void registerUser() throws IOException {
            try (
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
                                    out.println("false");
                                    out.println("Username Already Taken. \n Please enter Username and Password");
                                    userExists = true;
                                }
                            }
                            if (userExists == false) {

                                String[] data = {readUsername, readPassword};
                                System.out.println(socket +"Registered New User");
                                out.println("true");
                                out.println("-----REGISTRATION SUCCESSFUL----");
                                username = readUsername;
                                writer.writeNext(data);
                                userExists = true;
                                break;
                            }
                        }
                    }
                } else {
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
                            out.println("true");
                            out.println(readUsername + " Login Accepted!");
                            username = readUsername;
                            System.out.println("Client: " + socket + " logged in with username " + readUsername);
                            break;
                        } else
                            out.println("Login failed. Please try again.");
                    }
                }

            } catch (
                    CsvValidationException e) {
                e.printStackTrace();
            }/*finally {
                try {

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }*/

        }
    }
}


package netzwerk;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    final static int ServerPort = 51730;

    public static void main(String args[]) throws UnknownHostException, IOException {
        Scanner scn = new Scanner(System.in);
        // establish the connection
        Socket s = new Socket("127.0.0.1", ServerPort);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        String username;
        String password;
        String login;

        //Ask User to Register or Login and write choice to output stream
        System.out.println("1. Register " + "\n" + "2. Login");
        String userChoice = scn.nextLine();
        if (userChoice.equals("/login"))
            System.out.println("Please Enter Username and Password");

        out.println(userChoice);

        while ((username = scn.nextLine()) != null
                && (password = scn.nextLine()) != null) {
            out.println(username);
            out.println(password);
            login = in.readLine();

            if (login.equals("true")) {
                System.out.println(in.readLine());
                System.out.println(in.readLine());
                break;
            }
        }

            // sendMessage thread
            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        // read the message to deliver.
                        String msg = scn.nextLine();
                        // write on the output stream
                        out.println(msg);
                    }
                }
            });

            // readMessage thread
            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                            String msg = in.readLine();
                            System.out.println(msg);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });

            sendMessage.start();
            readMessage.start();

        }
    }

    /*public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("127.0.0.1", 51730);) {
            System.out.println("\n" +
                    "\n" +
                    " _       __________    __________  __  _________   __________     ________  ________   _________    __  _________   _____ __________ _    ____________ \n" +
                    "| |     / / ____/ /   / ____/ __ \\/  |/  / ____/  /_  __/ __ \\   /_  __/ / / / ____/  / ____/   |  /  |/  / ____/  / ___// ____/ __ \\ |  / / ____/ __ \\\n" +
                    "| | /| / / __/ / /   / /   / / / / /|_/ / __/      / / / / / /    / / / /_/ / __/    / / __/ /| | / /|_/ / __/     \\__ \\/ __/ / /_/ / | / / __/ / /_/ /\n" +
                    "| |/ |/ / /___/ /___/ /___/ /_/ / /  / / /___     / / / /_/ /    / / / __  / /___   / /_/ / ___ |/ /  / / /___    ___/ / /___/ _, _/| |/ / /___/ _, _/ \n" +
                    "|__/|__/_____/_____/\\____/\\____/_/  /_/_____/    /_/  \\____/    /_/ /_/ /_/_____/   \\____/_/  |_/_/  /_/_____/   /____/_____/_/ |_| |___/_____/_/ |_|  \n" +
                    "                                                                                                                                                       \n" +
                    "\n");
            System.out.println("1. Register " + "\n" + "2. Login");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in)); // reading data from user
            String username; String password;
            String userChoice = userIn.readLine();
            out.println(userChoice);
            String login;

            while ((username = userIn.readLine()) != null
                    && (password = userIn.readLine()) != null) {
                out.println(username);
                out.println(password);
                login = in.readLine();
//
                if (login.equals("true")) {
                    System.out.println(in.readLine());
                    System.out.println(in.readLine());
                    break;
                }
            }
            while (true) {
                String message = userIn.readLine();
                out.println(message);
                System.out.println(in.readLine());
            }
            Thread sendMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    while (true) {

                        try {
                            String msg = userIn.readLine();
                            // write on the output stream
                            dos.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });     }
                }

            // readMessage thread
            Thread readMessage = new Thread(new Runnable());
            {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                            String msg = dis.readUTF();
                            System.out.println(msg);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });

            sendMessage.start();
            readMessage.start();

        }
    }*/







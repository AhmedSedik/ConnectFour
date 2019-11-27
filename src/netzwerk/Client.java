package netzwerk;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("127.0.0.1", 51730);){
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

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // send data through the socket to the server, the Client needs to write to the PrintWriter

            BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));

            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in)); // reading data from user

            String username;
            String password;
            String userChoice = userIn.readLine();
            out.println(userChoice);
            while ((username = userIn.readLine())  != null
                    && (password = userIn.readLine()) != null) {
                out.println(username);
                out.println(password);
                System.out.println(in.readLine());
            }

            }
        }

    }




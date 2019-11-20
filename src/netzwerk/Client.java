package netzwerk;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("127.0.0.1", 51730);){
            System.out.println("Welcome to the game server!");
            System.out.println("Please enter your Username");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // send data through the socket to the server, the Client needs to write to the PrintWriter

            BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); // reading data from user

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
        }

    }
}


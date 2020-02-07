package netzwerk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author zozzy on 31.12.19
 */
/*
 * The Client that can be run both as a console or a GUI
 */
public class Client {

    private ObjectInputStream sInput;

    private ObjectOutputStream sOutput;

    private Socket socket;

    private ClientGUI clientGUI;

    private String server, username, password;

    private int port;

    Client(String server, int port, String username, String password) {
        this(server, port, username, password, null);
    }

    /*
     * Constructor call when used from a GUI
     * in console mode the ClientGUI parameter is null
     */
    Client(String server, int port, String username, String password, ClientGUI clientGUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        // save if we are in GUI mode or not
        this.clientGUI = clientGUI;
    }

    /*
     * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        // if it failed not much I can so
        catch (Exception ec) {
            clientGUI.connectionFailed();
            display("Error connecting to server:" + ec);
            return false;
        }

        String msg = "Attempting Connecting to:  " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }
        sendChoice(clientGUI.userChoices);
        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects

        try {
            sOutput.writeObject(username);
            sOutput.writeObject(password);
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }

        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console or the GUI
     */
    private void display(String msg) {
        if (clientGUI == null)
            System.out.println(msg);      // println in console mode
        else
            clientGUI.append(msg + "\n");        // append to the ClientGUI JTextArea (or whatever)
    }

    /*
     * To send a message to the server
     */
    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    private void sendChoice(String msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        } // not much else I can do

        // inform the GUI
        if (clientGUI != null)
            clientGUI.connectionFailed();

    }

    public static void main(String[] args) {
        // default values
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";
        String password = "";

        // depending of the number of arguments provided we fall through
        switch (args.length) {
            // > javac Client username portNumber serverAddr
            case 3:
                serverAddress = args[2];
                // > javac Client username portNumber
            case 2:
                try {
                    portNumber = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
                // > javac Client username
            case 1:
                userName = args[0];
                // > java Client
            case 0:
                break;
            // invalid number of arguments
            default:
                System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
                return;
        }
        // create the Client object
        Client client = new Client(serverAddress, portNumber, userName, password);
        // test if we can start the connection to the Server
        // if it failed nothing we can do
        if (!client.start())
            return;

        // wait for messages from user
        Scanner scan = new Scanner(System.in);
        // loop forever for message from the user
        while (true) {
            System.out.print("> ");
            // read message from user
            String msg = scan.nextLine();
            // logout if message is LOGOUT
            if (msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, "", ""));
                // break to do the disconnect
                break;
            }
            // message WhoIsIn
            else if (msg.equalsIgnoreCase("WHOISIN")) {
                client.sendMessage(new ChatMessage(ChatMessage.ONLINE_USERS, "", ""));
            } else {                // default to ordinary message
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg, ""));
            }
        }
        // done disconnect
        client.disconnect();
    }

    /*
     * a class that waits for the message from the server and append them to the JTextArea
     */
    class ListenFromServer extends Thread {

        public void startGame() {
            // Create a frame
            JFrame frame = new JFrame("Connect Four" + " - " + username);
            // Create an instance of the applet
            ConnectFourcClient applet = new ConnectFourcClient();

            // Add the applet instance to the frame
            frame.getContentPane().add(applet, BorderLayout.CENTER);

            // Invoke init() and start()
            applet.init();
            applet.start();


            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    //clientGUI.sendUserName(username);
                }
            });
            // Display the frame
            frame.setSize(640, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            frame.setResizable(false);


        }

        public void run() {
            while (true) {
                try {
                    String msg = (String) sInput.readObject();

                    // if console mode print the message and add back the prompt
                    if (clientGUI == null) {
                        System.out.println(msg);
                        System.out.print("> ");
                    } else if (msg.equalsIgnoreCase("trueLogin")) {
                        clientGUI.loginAccepted();
                        display("Login Accepted!");
                    } else if (msg.equalsIgnoreCase("falseLogin")) {
                        clientGUI.loginFailed();
                        display("Login Failed!");
                        socket.close();

                    } else if (msg.equalsIgnoreCase("trueRegister")) {
                        clientGUI.registerSucceed();
                        display("Registration Successful!");
                    } else if (msg.equalsIgnoreCase("falseRegister")) {
                        clientGUI.registerFailed();
                        display("Registration Failed!");
                        socket.close();
                    } else if (msg.equalsIgnoreCase("kicked")) {
                        clientGUI.kicked();
                        socket.close();
                    } else if (msg.equalsIgnoreCase("userlogged")) {
                        clientGUI.userLoggedIn();
                        socket.close();
                    } else if (msg.startsWith("online")) {
                        clientGUI.appendClients(msg.substring(6));
                        sOutput.flush();


                    } else if (msg.startsWith("play")) {
                        clientGUI.playRequest(msg);
                        sOutput.flush();
                    } else if (msg.startsWith("userbusy")) {

                        clientGUI.requestUserBusy(msg);

                    } else if (msg.startsWith("true")) {
                        clientGUI.playResponseAccepted(msg);
                        sOutput.flush();

                    } else if (msg.startsWith("false")) {
                        clientGUI.playResponseRejected(msg);

                    } else if (msg.startsWith("connect4")) {
                        startGame();

                    }else if (msg.startsWith("rejected")) {
                        clientGUI.requestRejected(msg);
                    }else if (msg.startsWith("Failure")) {
                        clientGUI.FailureRequest(msg);

                    } else {
                        clientGUI.append(msg);
                    }
                } catch (IOException e) {
                    //display("false Login or Register");
                    display("Connection Interrupted \n Cause: server could have closed the Connection\n or couldn't connect to the server \n" + e);
                    if (clientGUI != null)
                        clientGUI.connectionFailed();
                    break;
                }
                // can't happen with a String object but need the catch anyhow
                catch (ClassNotFoundException e2) {
                }
            }
        }
    }
}
//TODO player username has won Dialogue then vote to play again or exit / chat box at the end with text field / menu for exit or so
//TODO login refactoring and styling
// Chomp integration
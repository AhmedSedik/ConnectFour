package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author zozzy on 31.12.19
 */
/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {

    private int uniqueId;

    private ArrayList<ClientThread> clients;

    private ArrayList<String> onlineUsers;

    private ArrayList<String> gameClients;

    private ServerGUI serverGUI;

    private SimpleDateFormat simpleDateFormat;

    private int port;

    private String intiator = "";

    private boolean keepGoing;

    private static File users;

    boolean connected;

    boolean isBusy = true;

    int sessionCounter = 0;

    private ServerSocket gameSessionSocket = new ServerSocket(5555);

    private Server(int port) throws IOException {
        this(port, null);
    }

    public Server(int port, ServerGUI serverGUI) throws IOException {

        this.serverGUI = serverGUI;

        this.port = port;

        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        clients = new ArrayList<>();

        onlineUsers = new ArrayList<>();

        gameClients = new ArrayList<>();
    }

    public void start() {
        keepGoing = true;

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {

                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();    // accept connection

                if (!keepGoing)
                    break;

                ClientThread thread = new ClientThread(socket);
                clients.add(thread);

                thread.start();
            }
            try {
                serverSocket.close();
                for (int i = 0; i < clients.size(); ++i) {
                    ClientThread clientThread = clients.get(i);
                    try {
                        clientThread.sInput.close();
                        clientThread.sOutput.close();
                        clientThread.socket.close();
                    } catch (IOException ioE) {

                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            String msg = simpleDateFormat.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }


    protected void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception e) {
        }
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        String time = simpleDateFormat.format(new Date()) + " " + msg;
        if (serverGUI == null)
            System.out.println(time);
        else
            serverGUI.appendEvent(time + "\n");
    }

    /*
     *  to broadcast a message to all Clients
     */
    public synchronized void broadcast(String message) {

        String time = simpleDateFormat.format(new Date());
        String fullMessage = time + " " + message + "\n";
        // display message on console or GUI
        if (serverGUI == null)
            System.out.print(fullMessage);
        else
            serverGUI.appendRoom(fullMessage);     // append in the room window

        for (int i = clients.size(); --i >= 0; ) {
            ClientThread clientThread = clients.get(i);
            // try to write to the Client if it fails remove it from the list
            if (!clientThread.writeMsg(fullMessage)) {
                clients.remove(i);
                display("Disconnected Client " + clientThread.username + " removed from list.");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for (int i = 0; i < clients.size(); ++i) {
            ClientThread clientThread = clients.get(i);
            // found it
            if (clientThread.id == id) {
                clients.remove(i);
                return;
            }
        }
    }

    synchronized void kickClient() {

        for (int i = clients.size(); --i >= 0; ) {
            ClientThread clientThread = clients.get(i);

            if (clientThread.id == serverGUI.userIndex) {
                //clients.remove(i);//ArrayList size not correct
                //TODO fix user ID incrementation Done
                clientThread.id = --uniqueId;
                //TODO fix user Index (index is at some point wrong)

                if (serverGUI.userIndex >= 1) {
                    serverGUI.userIndex = -serverGUI.userIndex;
                }
                clientThread.writeMsg("kicked");
                display("Disconnected Client " + clientThread.username + " removed from list.");
                onlineUsers.remove(clientThread.username);
            }
        }
    }

    public static void main(String[] args) throws IOException {

        // start server on port 1500 unless a PortNumber is specified
        int portNumber = 1500;

        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;

        }
        // create a server object and start it
        Server server = new Server(portNumber);
        server.start();
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        String intiator = "";
        // my unique id (used in disconnecting)
        int id;
        // the Username of the Client
        String username = "";
        //instance of the helper Class
        ChatMessage message;
        //login status
        boolean loggedIn;
        String date;

        // Constructor
        ClientThread(Socket socket) {
            users = new File("users.csv");
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            /* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // read the username
                //TODO check user Choice  Done


                String userChoice = (String) sInput.readObject();

                if (userChoice.equalsIgnoreCase("/register")) {
                    registerUser();
                } else if (userChoice.equalsIgnoreCase("/Login")) {
                    userLogin();
                } else if (userChoice.equalsIgnoreCase("playGame")) {
                    //PLAY GAME
                } else {
                    display("Failed to Login or Register");
                }

                broadcast(username + " just connected.");
                display(username + " just connected.");
                connected = true;

            } catch (IOException | ClassNotFoundException e) {
                display("Exception creating new Input/output Streams: " + e);
                connected = false;
                return;
            }

            date = new Date().toString() + "\n";
        }

        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
                // read a String (which is an object)


                try {

                    if (!intiator.equals("")) {
                        findByUsername(intiator).sOutput.flush();
                        findByUsername(intiator).sOutput.reset();
                        System.out.println(findByUsername(intiator).socket.isConnected());
                    }
                    message = (ChatMessage) sInput.readObject();


                    sOutput.flush();

                } catch (IOException e) {
                    // in case client quit while server running reading stream
                    display(username + " Exception reading Streams: " + e);
                    //user disconnected log him off


                    onlineUsers.remove(username);
                    id--;//TODO Fix
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // the message part of the ChatMessage
                String message = this.message.getMessage();

                // Switch on the type of message receive
                switch (this.message.getType()) {
                    case ChatMessage.MESSAGE:
                        if (message.isEmpty()) {
                            break;
                        } else
                            broadcast(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        broadcast(username + " disconnected");
                        display(username + " disconnected");
                        onlineUsers.remove(username);
                        keepGoing = false;
                        break;
                    case ChatMessage.ONLINE_USERS:
                        // scan al the users connected
                        if (clients != null && clients.size() >= 1) {
                            for (int i = 0; i < clients.size(); i++) {
                                ClientThread ct = clients.get(i);
                                writeMsg("online" + ct.username);
                            }
                        }
                        break;
                    case ChatMessage.PLAY_REQUEST:

                        String selectedUsername = this.message.getMessage();
                        String sender = this.message.getSender();


                        isBusy = false;

                        System.out.println("Clients" + gameClients );

                        for (String gameClient : gameClients) {
                            if (sender.equals(gameClient) || selectedUsername.equals(gameClient)) {


                                writeMsgToUser("userbusy" + "-" + selectedUsername, sender);
                                System.out.println("user already in Game List");
                                isBusy = true;

                            } else {
                                //isBusy=false;
                            }
                        }


                            if (sender.equals(selectedUsername)) {
                                writeMsg("Failure");
                                break;
                            } else if(!isBusy) {
                                writeMsgToUser("playRequest" + "-" + sender, selectedUsername);

                            }



                        break;

                    case ChatMessage.REPSONE_PLAY_REQUEST:
                        String msg = this.message.getMessage();
                        String[] msgSplit = msg.split("-");
                        String response = msgSplit[0];
                        String userTO = msgSplit[1];
                        //intiator = intiator + userTO;
                        String userFROM = this.message.getSender();
                        writeMsgToUser(response + "-" + userFROM, userTO);
                        break;

                    case ChatMessage.PLAY_CONNECT_FOUR:
                        //isBusy = true;

                        String userTO3 = this.message.getMessage();
                        String userFROM3 = this.username;
                        writeMsgToUser("connect4", userTO3);
                        writeMsgToUser("connect4", userFROM3);

                       /* gameClients.add(userTO3);
                        gameClients.add(userFROM3);
                        System.out.println(gameClients);*/

                        startGame(userTO3, userFROM3);


                        break;

                    case ChatMessage.REJECT_CONNECT_FOUR:
                        String user2 = this.message.getMessage();
                        String user1 = this.message.getSender();
                        writeMsgToUser("rejected" + "-" + user2, user1);
                        break;

                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        private void registerUser() {
            try (
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));

            ) {

                String readUsername;
                String readPassword;

                boolean userExists = false;
                while (!userExists) {

                    while (((readUsername = (String) sInput.readObject()) != null) &&
                            ((readPassword = (String) sInput.readObject()) != null)) {

                        String[] nextRecord;
                        userExists = false;
                        CSVReader reader = new CSVReader(new FileReader(users));
                        while ((((nextRecord = reader.readNext())) != null) && !userExists) {
                            if (nextRecord[0].equals(readUsername)) {
                                System.out.println("a client entered an already taken username");
                                sOutput.writeObject("falseRegister");
                                userExists = true;
                            }
                        }
                        if (!userExists) {

                            String[] data = {readUsername, readPassword};
                            System.out.println(socket + "Registered New User");
                            sOutput.writeObject("trueRegister");
                            username = readUsername;
                            writer.writeNext(data);
                            userExists = true;
                            break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | CsvValidationException e) {
                e.printStackTrace();
            }
        }

        //TODO if user already logged in DONE
        public void userLogin() throws IOException {

            try {

                String readUsername;
                String readPassword;

                boolean loginCheck = false;
                while (((readUsername = (String) sInput.readObject()) != null) &&
                        ((readPassword = (String) sInput.readObject()) != null)) {
                    String[] nextRecord;
                    CSVReader reader = new CSVReader(new FileReader(users));

                    while ((((nextRecord = reader.readNext())) != null) && !loginCheck) {
                        if (nextRecord[0].equals(readUsername)) {
                            if (nextRecord[1].equals(readPassword)) {
                                for (String onlineUser : onlineUsers) {
                                    username = onlineUser;
                                    // found it
                                    if (username.equals(readUsername)) {
                                        System.out.println("doubled");
                                        sOutput.writeObject("userlogged");
                                    }
                                }
                                loginCheck = true;

                            }
                        }
                    }
                    if (loginCheck) {


                        sOutput.writeObject("trueLogin");

                        //sOutput.writeObject(readUsername + " Login Accepted!");
                        username = readUsername;
                        //adding user to ArrayList
                        onlineUsers.add(username);
                        serverGUI.userIndex++;
                        System.out.println("Client: " + socket + " logged in with username " + readUsername);
                        break;
                    } else
                        sOutput.writeObject("falseLogin");
                }


            } catch (
                    CsvValidationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) sOutput.close();
            } catch (Exception e) {
                //nothing to catch
            }
            try {
                if (sInput != null) sInput.close();
            } catch (Exception e) {
            }

            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }

        private boolean writeMsgToUser(String msg, String username) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                ClientThread clientThread = findByUsername(username);
                ObjectOutputStream out = clientThread.sOutput;
                out.writeObject(msg);
                out.flush();
                //TODO sOutput flush
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }

    //TODO better have interface also for Remove and Kick Clients
    void loggedClients() {
        if (clients.isEmpty()) {
            serverGUI.appendEvent("No Current Online Users\n");
            return;
        }

        for (int i = 0; i < clients.size(); ++i) {
            ClientThread ct = clients.get(i);
            serverGUI.appendEvent((i + 1) + ") " + ct.username + " since " + ct.date);
        }


    }

    public ClientThread findByUsername(String username) {
        for (ClientThread clientThread : clients) {
            if (clientThread.username.equals(username))
                return clientThread;
        }
        return null;
    }

    void onlineUsers() {
        if (clients != null && clients.size() >= 1) {
            for (int i = 0; i < clients.size(); i++) {
                ClientThread clientThread = clients.get(i);
                serverGUI.appendClients((i + 1) + ")" + clientThread.username);

            }
        }
    }

    private void startGame(String user1, String user2) {
            /*onlineUsers.forEach(user ->{
                if (user.equals(user1) || user.equals(user2)) {
                    System.out.println("match");

                } else {
                    GameSession gameSession = new GameSession(user1, user2, gameSessionSocket);
                }
            });*/

        GameSession gameSession = new GameSession(user1, user2, gameSessionSocket);
        gameSession.gameClients.add(user1);
        gameSession.gameClients.add(user2);
        gameClients = gameSession.gameClients;


    }
}

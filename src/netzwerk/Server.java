package netzwerk;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static PrintWriter out;
    private static BufferedReader in;
    public static File users;
    private static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<String, PrintWriter>();

    private static JFrame frame;
    private static JTextField textField;
    private static JTextArea messageArea;
    private static JPanel panel;
    private static JScrollPane scroller = new JScrollPane(messageArea);
    private static JButton connectButton;
    private static JButton disconnectButton;
    private static JButton usersButton;
    private static DefaultCaret caret;
    private static JPanel inputpanel;
    private static ServerSocket listener = null;
    private static ExecutorService pool;

    public Server() {
        frame = new JFrame("Server Admin");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);

        messageArea = new JTextArea(15, 50);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setFont(Font.getFont(Font.SANS_SERIF));

        JScrollPane scroller = new JScrollPane(messageArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());

        textField = new JTextField(20);
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        usersButton = new JButton("Online Users");
        caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        inputpanel.add(textField);
        inputpanel.add(connectButton);
        inputpanel.add(disconnectButton);
        inputpanel.add(usersButton);
        panel.add(inputpanel);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        textField.requestFocus();

        textField.addActionListener(e -> {
            adminCommands(textField.getText());
            textField.setText("");
        });

        connectButton.addActionListener(e -> startServer());
        usersButton.addActionListener(e -> concurrentHashMap.forEach((k,v)->messageArea.append((k+" "))));
        disconnectButton.addActionListener(e -> {
            try {
                closeServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.frame.setVisible(true);
        users = new File("users.csv");
        pool = Executors.newFixedThreadPool(10);
        while (true) {
            Thread.sleep(20000);
            pool.execute(new Handler(listener.accept(), out, in));
        }


    }

    private void adminCommands(String command) {
        if (command.contains("@all")) {
            String msg = command.replace("@all", "");
            for (PrintWriter writer : writers) {
                writer.println("admin:" + msg);
                writer.flush();
            }
        } else if (command.contains("kick")) {
            String username = command.substring(5);
            PrintWriter writer = (PrintWriter) concurrentHashMap.get(username);
            writer.println("You have been kicked by the server admin.");
            concurrentHashMap.remove(username);
        }
    }

    private void startServer() {
        try {
        listener = new ServerSocket(51730);
        messageArea.append("The game server is running..." + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void closeServer() throws IOException {
        listener.close();
        for (PrintWriter writer : writers) {
            writer.println("Server was disconnected.");
            writer.flush();
        }
        pool.shutdown(); // Disable new tasks from being submitted
        pool.shutdownNow();


    }
    private static class Handler implements Runnable {
        private Socket socket;
        private String username;
        private PrintWriter out;
        private BufferedReader in;

        public Handler(Socket socket, PrintWriter out, BufferedReader in) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            messageArea.append("Connected: " + socket + "\n");
            try {
                registerUser();
                chat();
                if(!Thread.currentThread().isInterrupted())
                leaveChat();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void chat()  {
            messageArea.append(username + " " + "has joined the chat" + "\n");
            writers.add(out);
            concurrentHashMap.put(username, out);
            for (PrintWriter printWriter : writers) {
                printWriter.println(username + " has joined");
                printWriter.flush();
            }

            synchronized (names) {
                if (!username.isBlank() && !names.contains(username)) {
                    names.add(username);
                    out.println(names);
                }
            }
            // Accept messages from this client and broadcast them.
            while (true) {
                try {
                    String input = null;
                    input = in.readLine();
                    if (Thread.currentThread().isInterrupted()){
                        for (PrintWriter writer : writers) {
                        writer.println("bn2afel");
                        writer.flush();
                    }
                        break;
                    }
                    if (input.toLowerCase().startsWith("/quit")) break;
                    if (input.equals("")) continue;

                    for (PrintWriter writer : writers) {
                        writer.println(username + ": " + input);
                        writer.flush();
                        messageArea.append(username + ": " + input + "\n");
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void leaveChat(){
            names.remove(username);
            writers.remove(out);
            concurrentHashMap.remove(username);

            for (PrintWriter writer : writers) {
                writer.println(username + "has left the chat");
                writer.flush();
            }
        }

        public void registerUser() throws IOException {
            try (
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));

            ) {

                String userChoice = in.readLine();
                String readUsername;
                String readPassword;
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
                                    out.println("falseRegister");
                                    userExists = true;
                                }
                            }
                            if (userExists == false) {

                                String[] data = {readUsername, readPassword};
                                out.println("trueRegister");
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
                        String[] nextRecord;
                        CSVReader reader = new CSVReader(new FileReader(users));

                        while ((((nextRecord = reader.readNext())) != null) && loginCheck == false) {
                            if (nextRecord[0].equals(readUsername)) {
                                if (nextRecord[1].equals(readPassword))
                                    loginCheck = true;
                            }
                        }
                        if (loginCheck == true) {
                            out.println("trueLogin");
                            out.println(readUsername + " Login Accepted!");
                            username = readUsername;
                            System.out.println("Client: " + socket + " logged in with username " + readUsername);
                            break;
                        } else
                            out.println("falseLogin");
                    }
                }

            } catch (
                    CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }
}


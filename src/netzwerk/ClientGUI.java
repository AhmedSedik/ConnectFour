package netzwerk; /**
 * @author zozzy on 31.12.19
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/* * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    //condition set for login process
    private boolean loginFailed = false;

    //label of the message
    private JLabel label;
    private String username;
    //holds username
    private JTextField usernameField;
    //password field
    private JPasswordField passwordField;

    // the messages Textfield
    private JTextField chatTextField;
    // to hold the server address an the port number
    private JTextField tfServer, tfPort;
    // to Logout and get the list of the users
    private JButton login, logout, register, btn_clients, send;
    // for the chat room
    private JTextArea textArea;
    // connection status
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;

    String userChoices = "";
    public int userIndex;
    //Registration Components
    JFrame frame = new JFrame("Registration");
    JLabel l1, l2, l3;
    JTextField usernameRegister;
    JButton btn_register;
    JPasswordField passwordFieldRegister;


    JFrame frameOnlineUsers;
    private JList<String> list1;
    private DefaultListModel<String> listModel;
    private JButton btn_play;
    private JButton btn_close;

    // Constructor connection receiving a socket number
    ClientGUI(String host, int port) {


        super("Chat Client");
        defaultPort = port;
        defaultHost = host;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
            System.out.println(e);
        }
        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(4, 1));
        // the server name and the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
        // the two JTextField with default value for server address and port number
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);


        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        serverAndPort.add(new JLabel(""));


        // adds the Server and port field to the GUI
        northPanel.add(serverAndPort);
        JPanel userandpass = new JPanel(new GridLayout(1, 4, 1, 3));

        usernameField = new JTextField("");
        passwordField = new JPasswordField("");
        //passwordField.setHorizontalAlignment(SwingConstants.RIGHT);

        userandpass.add(new JLabel("Username: "));
        userandpass.add(usernameField);
        userandpass.add(new JLabel("Password: "));
        userandpass.add(passwordField);
        northPanel.add(userandpass);


        // the Label and the TextField
        label = new JLabel("Enter message below", SwingConstants.CENTER);
        northPanel.add(label);

        chatTextField = new JTextField("");
        chatTextField.setBackground(Color.WHITE);
        northPanel.add(chatTextField);
        add(northPanel, BorderLayout.NORTH);


        // The CenterPanel which is the chat room
        textArea = new JTextArea("Welcome to the Chat room\n", 80, 80);
        this.textArea.setSize(400, 400);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1, 1, 3));
        centerPanel.add(new JScrollPane(textArea));
        textArea.setEditable(false);

        add(centerPanel, BorderLayout.CENTER);


        // the 3 buttons
        login = new JButton("Login");
        login.addActionListener(this);
        register = new JButton("Register");
        register.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);        // you have to login before being able to logout
        btn_clients = new JButton("Online Users");
        btn_clients.addActionListener(this);
        btn_clients.setEnabled(false);        // you have to login before being able to Who is in

        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(register);
        southPanel.add(logout);
        southPanel.add(btn_clients);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(600, 600);
        setVisible(true);
        setResizable(false);

        usernameField.requestFocus();


    }

    // called by the Client to append text in the TextArea
    void append(String str) {
        textArea.append(str);
        //ta.setCaretPosition(ta.getText().length() - 1);
    }

    void appendClients(String str) {
        listModel.addElement(str);

    }

    void playRequest(String request) {
        String[] parts = request.split("-");
        String senderUsername = parts[1];
        int result = JOptionPane.showConfirmDialog(this,
                "player " + senderUsername + " has sent you a request to play Connect Four.", "Game Request", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            client.sendMessage(new ChatMessage(ChatMessage.REPSONE_PLAY_REQUEST, "true" + "-" + senderUsername, username));
        } else if (result == 1) {
            client.sendMessage(new ChatMessage(ChatMessage.REPSONE_PLAY_REQUEST, "false" + "-" + senderUsername, username));
        }
    }

    void playResponseAccepted(String response) {
        String[] parts = response.split("-");
        String senderUsername = parts[1];
        client.sendMessage(new ChatMessage(ChatMessage.PLAY_CONNECT_FOUR, senderUsername, username));
    }

    void playResponseRejected(String response) {
        String[] parts = response.split("-");
        String senderUsername = parts[1];
        client.sendMessage(new ChatMessage(ChatMessage.REJECT_CONNECT_FOUR, senderUsername, username));
    }

    void requestUserBusy(String response) {
        String[] parts = response.split("-");
        String senderUsername = parts[1];
        JOptionPane.showMessageDialog(this, "User " + senderUsername + "is Busy");
    }

    void requestRejected(String response) {
        String[] parts = response.split("-");
        String senderUsername = parts[1];
        JOptionPane.showMessageDialog(this, "User " + senderUsername + " reject your request to play Connect Four!");
    }

    void FailureRequest(String response) {

        JOptionPane.showMessageDialog(this, "You Can't Play with yourself :)!");
    }


    void loginAccepted() {
        JOptionPane.showMessageDialog(this, "Login Accepted");
        this.setTitle("Chat Client" + " (" + username + ")");
        chatTextField.requestFocus();
    }

    void loginFailed() {
        loginFailed = true;
        JOptionPane.showMessageDialog(this, "Login Failed\n Please try again!");
        login.setEnabled(true);
        connected = false;

    }

    void registerSucceed() {
        JOptionPane.showMessageDialog(this, "Registration Successful");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        this.setTitle("Chat Client" + " (" + username + ")");
        chatTextField.requestFocus();
    }

    void registerFailed() {
        JOptionPane.showMessageDialog(this, "Username already Taken!");

    }

    void kicked() {
        JOptionPane.showMessageDialog(this, "Admin has kicked you");
        login.setEnabled(true);
        this.setTitle("Chat Client");
        connected = false;
    }

    void userLoggedIn() {
        loginFailed = true;
        JOptionPane.showMessageDialog(this, "User Already Logged In!\n Please try again!");
        login.setEnabled(true);

        connected = false;
    }

    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        login.setEnabled(true);
        register.setEnabled(true);
        //logout.setEnabled(false);
        btn_clients.setEnabled(false);
        //label.setText("Enter your username below");
        //screenName.setText("");
        // reset port number and host name as a construction time
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        // let the user change them
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        // don't react to a <CR> after the username
        chatTextField.removeActionListener(this);
        connected = false;

        JOptionPane.showMessageDialog(this, "No Connection to Server\n");
    }

    void initComponentRegister() {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
            System.out.println(e);
        }
        l1 = new JLabel("Please Enter Username and Password below :");
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif", Font.BOLD, 14));

        l2 = new JLabel("Username");
        l3 = new JLabel("Password");
        usernameRegister = new JTextField();
        passwordFieldRegister = new JPasswordField();
        btn_register = new JButton("Register");
        btn_register.addActionListener(this);

        l1.setBounds(100, 30, 400, 30);
        l2.setBounds(50, 70, 180, 30);
        l3.setBounds(50, 110, 180, 30);
        usernameRegister.setBounds(120, 70, 200, 30);
        passwordFieldRegister.setBounds(120, 110, 200, 30);
        btn_register.setBounds(150, 160, 100, 30);

        frame.add(l1);
        frame.add(l2);
        frame.add(usernameRegister);
        frame.add(l3);
        frame.add(passwordFieldRegister);
        frame.add(btn_register);
        frame.getRootPane().setDefaultButton(btn_register);
        frame.setResizable(false);
        frame.setSize(450, 250);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    void initCompClientsList() {

        frameOnlineUsers = new JFrame();
        final JLabel label = new JLabel("Online Users");
        label.setSize(500, 100);
        btn_play = new JButton("Play");
        btn_play.setBounds(200, 150, 80, 30);
        btn_play.addActionListener(this);
        btn_close = new JButton("close");
        btn_close.setBounds(200, 180, 80, 30);
        btn_close.addActionListener(this);
        listModel = new DefaultListModel<>();


        list1 = new JList<>(listModel);
        list1.setBounds(100, 100, 100, 200);

        frameOnlineUsers.add(list1);
        frameOnlineUsers.add(btn_play);
        frameOnlineUsers.add(btn_close);
        frameOnlineUsers.add(label);
        frameOnlineUsers.setSize(300, 340);
        frameOnlineUsers.setLayout(null);
        frameOnlineUsers.setVisible(true);
        frameOnlineUsers.setResizable(false);

        client.sendMessage(new ChatMessage(ChatMessage.ONLINE_USERS, "", ""));

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    userIndex = list1.getSelectedIndex() + 1;
                    System.out.println(userIndex);
                }
            }
        });
        // making sure main frame is disabled while online list is opened
        frameOnlineUsers.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                setEnabled(true);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                setEnabled(false);
            }
        });
    }

    /*
     * Button or JTextField clicked
     */
    public void actionPerformed(ActionEvent event) {
        Object choice = event.getSource();
        // if it is the Logout button
        if (choice == logout) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, "", ""));
            System.out.println("User logged Out!");
            this.setTitle("Chat Client");
            return;
        }
        // if it the who is in button
        if (choice == btn_clients) {
            initCompClientsList();
            return;
        }
        if (choice == btn_play) {
            String selectedUser = list1.getSelectedValue();
            client.sendMessage(new ChatMessage(ChatMessage.PLAY_REQUEST, selectedUser, username));
            frameOnlineUsers.dispose();
            setEnabled(true);
            //TODO Waiting status
        }
        if (choice == btn_close) {
            frameOnlineUsers.dispose();
            setEnabled(true);
        }

        if (choice == register) {
            //load register frame
            initComponentRegister();
        }
        //TODO if message empty

        // this the only text coming from the ChatTextField
        if (connected && !loginFailed) {
            // just have to send the message

            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, chatTextField.getText(), ""));
            chatTextField.setText("");
            return;
        }
        if (choice == login) {
            userChoices = "/Login";
            loginRegisterServer(usernameField, passwordField);
        }

        if (choice == btn_register) {
            userChoices = "/register";
            loginRegisterServer(usernameRegister, passwordFieldRegister);
        }

    }

    /**
     * This method responsible for interacting with the server in login or register mode
     *
     * @param jUsername the Textfield of user name (register or login)
     * @param jPass     the Textfield of the password (register or login)
     */
    private void loginRegisterServer(JTextField jUsername, JPasswordField jPass) {
        String username = jUsername.getText().trim();
        String password = String.valueOf(jPass.getPassword());
        this.username = username;
        //TODO handling empty fields better later
        if (username.length() == 0)
            return;
        if (password.length() == 0)
            return;

        // empty serverAddress ignore it
        String server = tfServer.getText().trim();
        if (server.length() == 0) {

            return;
        }

        // empty or invalid port number, ignore it
        String portNumber = tfPort.getText().trim();
        if (portNumber.length() == 0)
            return;
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        } catch (Exception en) {
            return;   // nothing I can do if port number is not valid
        }

        // try creating a new Client with GUI
        client = new Client(server, port, username, password, this);

        // test if we can start the Client (open socket)
        if (!client.start()) {
            return;
        }
        //client.sendChoice("/register");

        chatTextField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        //label.setText("Enter your message below");

        connected = true;
        loginFailed = false;


        // disable login button
        login.setEnabled(false);
        // enable the 2 buttons
        logout.setEnabled(true);
        register.setEnabled(false);
        btn_clients.setEnabled(true);
        // disable the Server and Port JTextField
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        // Action listener for when the user enter a message
        chatTextField.addActionListener(this);
    }

    // to start the whole thing the server
    public static void main(String[] args) {
        new ClientGUI("localhost", 1500);

    }

}
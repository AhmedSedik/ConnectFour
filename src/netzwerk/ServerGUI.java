package netzwerk;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * @author zozzy on 31.12.19
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {

    //TODO Client list

    JLabel label;
    JTextField chatTextField;

    private static final long serialVersionUID = 1L;
    // the stop and start buttons
    private JButton stopStart;
    //clients Button
    JButton btn_Clients;
    // JTextArea for the chat room and the events
    private JTextArea chat, event, list;
    JFrame frameOnlineUsers;
    // The port number
    private JTextField tPortNumber;
    private JList<String> list1;
    private DefaultListModel<String> listModel;
    private JButton btn_kick_user;
    private JButton btn_close;
    // my server
    private Server server;
    // connection status
    private boolean connected;
    public int userIndex;

    // server constructor that receive the port to listen  for connection
    ServerGUI(int port) {
        super("Chat Server");
        server = null;
        // in the NorthPanel the PortNumber the Start and Stop buttons
        JPanel north = new JPanel(new GridLayout(4, 1));
        JPanel portPanel = new JPanel(new GridLayout(1, 1));


        portPanel.add(new JLabel("Port number: "));
        tPortNumber = new JTextField("  " + port);
        portPanel.add(tPortNumber);

        north.add(portPanel);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 1));

        stopStart = new JButton("Start");
        stopStart.addActionListener(this);
        btn_Clients = new JButton("Client List");
        btn_Clients.addActionListener(this);
        buttonsPanel.add(stopStart);
        buttonsPanel.add(btn_Clients);

        north.add(buttonsPanel);


        // the Label and the TextField
        label = new JLabel("Enter message below", SwingConstants.CENTER);
        north.add(label);

        chatTextField = new JTextField("");
        chatTextField.setBackground(Color.WHITE);
        north.add(chatTextField);
        add(north, BorderLayout.NORTH);

        // the event and chat room
        JPanel center = new JPanel(new GridLayout(2, 1));
        chat = new JTextArea(80, 80);
        chat.setEditable(false);
        appendRoom("Chat room.\n");
        center.add(new JScrollPane(chat));
        event = new JTextArea(80, 80);
        event.setEditable(false);
        appendEvent("Events log.\n");
        center.add(new JScrollPane(event));
        add(center);

//        JPanel east = new JPanel(new GridLayout(1, 1));
//        list = new JTextArea(80, 20);
//        list.setEditable(false);

        // need to be informed when the user click the close button on the frame
        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);

    }

    // append message to the two JTextArea
    // position at the end
    void appendRoom(String str) {
        chat.append(str);
        chat.setCaretPosition(chat.getText().length() - 1);
    }
    void appendEvent(String str) {
        event.append(str);
        event.setCaretPosition(chat.getText().length() - 1);
    }
    void appendClients(String str) {
        listModel.addElement(str);

    }
    // start or stop where clicked
    public void actionPerformed(ActionEvent event) {

        Object choice = event.getSource();
        if (choice == stopStart) {
            // if running we have to stop
            if (server != null) {
                server.stop();
                server = null;
                tPortNumber.setEditable(true);
                stopStart.setText("Start");
                return;
            }
            // OK start the server
            int port;
            try {
                port = Integer.parseInt(tPortNumber.getText().trim());
            } catch (Exception er) {
                appendEvent("Invalid port number");
                return;
            }
            // create a new Server
            try {
                server = new Server(port, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // and start it as a thread
            new ServerRunning().start();
            stopStart.setText("Stop");
            tPortNumber.setEditable(false);
            chatTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        server.broadcast("Admin: " + chatTextField.getText());
                        chatTextField.setText("");
                    }
                }
            });
        } else if (choice == btn_Clients) {

            server.loggedClients();
            initCompClientsList();
            //server.onlineUsers();
        } else if (choice == btn_kick_user) {
            server.kickClient();

        } else if (choice == btn_close) {
            frameOnlineUsers.dispose();
            setEnabled(true);
        }
        if (server.connected) {

        }
    }

    void initCompClientsList() {

        frameOnlineUsers = new JFrame();
        final JLabel label = new JLabel("Online Users");
        label.setSize(500, 100);
        btn_kick_user = new JButton("Kick");
        btn_kick_user.setBounds(200, 150, 80, 30);
        btn_kick_user.addActionListener(this);
        btn_close = new JButton("close");
        btn_close.setBounds(200,180,80,30);
        btn_close.addActionListener(this);
        listModel = new DefaultListModel<>();


        list1 = new JList<>(listModel);
        list1.setBounds(100, 100, 100, 200);

        frameOnlineUsers.add(list1);
        frameOnlineUsers.add(btn_kick_user);
        frameOnlineUsers.add(btn_close);
        frameOnlineUsers.add(label);
        frameOnlineUsers.setSize(450, 450);
        frameOnlineUsers.setLayout(null);
        frameOnlineUsers.setVisible(true);

        server.onlineUsers();

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    userIndex = list1.getSelectedIndex()+1;
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


    public static void main(String[] arg) {
        // start server default port 1500
        new ServerGUI(1500);
    }

    /*
     * If the user click the X button to close the application
     * I need to close the connection with the server to free the port
     */

    public void windowClosing(WindowEvent e) {
        // if my Server exist
        if (server != null) {
            try {
                server.stop();            // ask the server to close the connection
            } catch (Exception eClose) {
                //nothing to catch actually
            }
            server = null;
        }
        // dispose the frame
        dispose();
        System.exit(0);
    }

    // I can ignore the other WindowListener method
    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /*
     * A thread to run the Server
     */
    class ServerRunning extends Thread {
        public void run() {
            server.start();         // should execute until if fails
            // the server failed
            stopStart.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server crashed\n");
            server = null;
        }
    }

}




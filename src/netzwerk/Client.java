package netzwerk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    final static int ServerPort = 51730;
    Scanner scn;
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chat");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16,50);

    public Client() {
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

        private int getName() throws IOException {

            Object[] options = {"Login", "Register"};
            int n = JOptionPane.showOptionDialog(frame,
                    "Welcome to the game server! Please login or register a new account.",
                    "Login/Register",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]); //default button title

            if (n==0) {
                JPanel panel = new JPanel(new BorderLayout(5, 5));

                JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
                label.add(new JLabel("Username", SwingConstants.RIGHT));
                label.add(new JLabel("Password", SwingConstants.RIGHT));
                panel.add(label, BorderLayout.WEST);

                JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
                JTextField username = new JTextField();
                controls.add(username);
                JPasswordField password = new JPasswordField();
                controls.add(password);
                panel.add(controls, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(frame, panel, "login", JOptionPane.OK_CANCEL_OPTION);

                String user = username.getText();
                String pass = new String(password.getPassword());

                out.println("/login");
                out.println(user);
                out.println(pass);
                if (in.readLine().equals("true")) {
                    JOptionPane.showMessageDialog(frame, "Login Successful.");
                    chat();

                } else
                    JOptionPane.showMessageDialog(frame, "Login failed.");
            }
        return n;
        }

    private void run() throws UnknownHostException, IOException {
        Socket s = new Socket("127.0.0.1", ServerPort);

        scn = new Scanner(System.in);
        in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);

        getName();

        /*String username;
        String password;
        String login;

        //Ask User to Register or Login and write choice to output stream
        System.out.println("/register " + "\n" + "/login");
        String userChoice = scn.nextLine();
        if (userChoice.equals("/login") || userChoice.equals("/register"))
            System.out.println("Please Enter  Username and Password");

        out.println(userChoice);

        while ((username = scn.nextLine()) != null
                && (password = scn.nextLine()) != null) {
            out.println(username);
            out.println(password);
            login = in.readLine();

            if (login.equals("true")) {
                System.out.println(in.readLine());
                break;
            }
            else
                System.out.println(in.readLine());
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
                            if(msg==null)
                                System.exit(1);
                            System.out.println(msg);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });

            sendMessage.start();
            readMessage.start();*/

        }

    public void chat(){

        this.frame.setTitle("Chat");
        textField.setEditable(true);

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // read the message to deliver.
                    String msg = textField.getText();
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
                        if(msg==null)
                            System.exit(1);
                        messageArea.append(msg + "\n");
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
    public static void main(String[] args) throws Exception {
        var client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }


}







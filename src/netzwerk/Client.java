package netzwerk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

                if(n==0)
                    out.println("/login");
                else {
                    out.println("/register");
                    register();

                }
        return n;
        }

    private void register() throws IOException {
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

        JOptionPane.showMessageDialog(frame, panel, "Game Server", JOptionPane.OK_CANCEL_OPTION);

        String user = username.getText();
        String pass = new String(password.getPassword());
        out.println(user);
        out.println(pass);
        String ServerResponse = in.readLine();
        switch (ServerResponse) {
            case "trueRegister":
                JOptionPane.showMessageDialog(frame, user + " registration successful. ");
                chat();
                break;
            case "falseRegister":
                JOptionPane.showMessageDialog(frame, "Username Already Taken. Please enter a new username.");
                register();
                break;
        }
    }
    private void run() throws UnknownHostException, IOException {
        Socket s = new Socket("127.0.0.1", ServerPort);

        scn = new Scanner(System.in);
        in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);

        getName();
    }

    public void chat(){

        this.frame.setTitle("Chat");
        textField.setEditable(true);


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
        readMessage.start();
    }
    public static void main(String[] args) throws Exception {
        var client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }


}







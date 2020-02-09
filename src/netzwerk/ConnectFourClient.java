package netzwerk;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static netzwerk.Const.DISCONNECT_TOKEN;
/**
 * @author zozzy on 28.01.20
 */
public class ConnectFourClient extends JApplet
        implements Runnable, Constraints {
    // Indicate whether the player has the turn
    private boolean myTurn = false;

    // Indicate the token for the player
    private char myToken = ' ';

    // Indicate the token for the other player
    private char otherToken = ' ';

    // Create and initialize cells
    private Cell[][] cell =  new Cell[6][7];

    // Create and initialize a title label
    private JLabel label = new JLabel();

    // Create and initialize a status label
    private JLabel status = new JLabel();

    JPanel p;
    // Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Indicate if it runs as application
    private boolean isStandAlone = false;

    // Host name or ip
    public String host = "localhost";

    Socket socket;

    /** Initialize UI */
    public void init() {

        createMenuBar();
        // Panel p to hold cells
         p = new JPanel();


        p.setLayout(new GridLayout(6, 7, 0, 0));
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                p.add(cell[i][j] = new Cell(i, j, cell));

        // Set properties for labels and borders for labels and panel
        p.setBorder(new LineBorder(Color.black, 1));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setBorder(new LineBorder(Color.black, 1));
        status.setBorder(new LineBorder(Color.black, 1));

        // Place the panel and the labels to the applet
        add(label, BorderLayout.NORTH);
        add(p, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        // Connect to the server
        connectToServer();
    }

    private void createMenuBar() {
        var menuBar = new JMenuBar();
        var exitIcon = new ImageIcon("src/resources/exit.png");

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Disconnect", exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Disconnect From Game");
        //TODO close game window and disconnect from game server
        eMenuItem.addActionListener(e -> {
            try {
                sendInfoToServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            closeGameWindow();
        });

        fileMenu.add(eMenuItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }


    private void connectToServer() {
        try {
            // Create a socket to connect to the server
             socket = new Socket("localhost",5555);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception ex) {
            System.err.println(ex);
        }

        // Control the game on a separate thread
        Thread thread = new Thread(this);
        thread.start();
    }
    private void disconnect() {
        try {
            if (fromServer != null)
                fromServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (toServer != null)
                toServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void closeGameWindow() {
        Window win = SwingUtilities.getWindowAncestor(p);
        win.dispose();

    }

    public void run() {
        try {
            // Get notification from the server
            int player = fromServer.readInt();
            String player2 = fromServer.readUTF();
            String username = fromServer.readUTF();

            // Am I player 1 or 2?
            if (player == PLAYER1) {
                myToken = 'r';
                otherToken = 'b';
                label.setText(username +": Your color is red");
                status.setText("Waiting for " + player2 +" to join");

                // Receive startup notification from the server
                fromServer.readInt(); // Whatever read is ignored

                // The other player has joined
                status.setText(player2+ " has joined. You start first.");

                // It is my turn
                myTurn = true;


            }
            else if (player == PLAYER2) {
                myToken = 'b';
                otherToken = 'r';
                label.setText(username + ": Your color is blue");
                status.setText("Waiting for " + player2 + " to make a move");
            } else if (player == DISCONNECT_TOKEN) {
                System.out.println("Disconnected");

            }
            // Continue to play
            while (continueToPlay) {
                if (player == PLAYER1) {
                    waitForPlayerAction(); // Wait for player 1 to move
                    sendMove(); // Send the move to the server
                    receiveInfoFromServer(); // Receive info from the server
                }
                else if (player == PLAYER2) {
                    receiveInfoFromServer(); // Receive info from the server
                    waitForPlayerAction(); // Wait for player 2 to move
                    sendMove(); // Send player 2's move to the server
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Wait for the player to mark a cell */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }
        waiting = true;
    }

    /** Send this player's move to the server */
    private void sendMove() throws IOException {
        toServer.writeInt(rowSelected); // Send the selected row
        toServer.writeInt(columnSelected); // Send the selected column
        toServer.flush();

    }

    private void sendInfoToServer() throws IOException {
        toServer.writeInt(DISCONNECT_TOKEN);
        toServer.writeInt(DISCONNECT_TOKEN);
        toServer.flush();


    }
    /** Receive info from the server */
    private void receiveInfoFromServer() throws IOException {
        // Receive game status
        int status = fromServer.readInt();

        if (status == DISCONNECT_TOKEN) {
            JOptionPane.showMessageDialog(this, "Connection lost");
            closeGameWindow();
            disconnect();
            System.out.println("Disconnect");
            toServer.flush();
        }else

        if (status == PLAYER1_WON) {
            // Player 1 won, stopServer playing
            //continueToPlay = false;

            if (myToken == 'r') {

                this.status.setText("I won!");
                playSound("src/res/won.wav");
                showDialog("You have Won!", "Winner");

            }
            else if (myToken == 'b') {

                this.status.setText("You lost!");
                playSound("src/res/lose.wav");
                showDialog("You have Lost :(", "Loser");
                receiveMove();
            }
        }
        else if (status == PLAYER2_WON) {
            // Player 2 won, stopServer playing
            //continueToPlay = false;
            if (myToken == 'b') {

                this.status.setText("I won!");
                playSound("src/res/won.wav");
                showDialog("You have Won!", "Winner");

            }
            else if (myToken == 'r') {

                this.status.setText("You lost!");
                playSound("src/res/lose.wav");
                showDialog("You have Lost :(", "Loser");
                receiveMove();

            }
        }
        else if (status == DRAW) {
            // No winner, game is over
            //continueToPlay = false;
            this.status.setText("Game is over, no winner!");

            if (myToken == 'b') {
                receiveMove();
            }
        }
        else {
            receiveMove();
            this.status.setText("Your turn");
            myTurn = true; // It is my turn
        }
    }
    private void showDialog(String message,String title) {
        try {
            sendInfoToServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int input = JOptionPane.showConfirmDialog(this,
                message, title, JOptionPane.OK_CANCEL_OPTION);
        if (input == 0) {
            closeGameWindow();
        }
    }
    private void receiveMove() throws IOException {
        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();
        cell[row][column].setToken(otherToken);
        toServer.flush();
    }
    private void playSound(String soundName)
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception ex)
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace( );
        }
    }

    // An inner class for a cell
    public class Cell extends JPanel {
        // Indicate the row and column of this cell in the board
        private int row;
        private int column;
        private Cell[][] cell;

        // Token used for this cell
        private char token = ' ';

        public Cell(int row, int column, Cell[][] cell) {
            this.row = row;
            this.cell = cell;
            this.column = column;
            setBorder(new LineBorder(Color.black, 1)); // Set cell's border
            addMouseListener(new ClickListener());  // Register listener

        }

        /** Return token */
        private char getToken() {
            return token;
        }

        /** Set a new token */
        private void setToken(char c) {
            token = c;
            repaint();
        }

        /** Paint the cell */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (token == 'r') {
                g.drawOval(9, 9, getWidth() - 20, getHeight() - 20);
                g.setColor(Color.red);
                g.fillOval(9 ,9,  getWidth() - 20, getHeight() - 20);
            }
            else if (token == 'b') {
                g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
                g.setColor(Color.blue);
                g.fillOval(9 ,9,  getWidth() - 20, getHeight() - 20);
            }
        }


        /** Handle mouse click on a cell */
        private class ClickListener extends MouseAdapter {

            public void playSound(String soundName)
            {
                try
                {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
                    Clip clip = AudioSystem.getClip( );
                    clip.open(audioInputStream);
                    clip.start( );
                }
                catch(Exception ex)
                {
                    System.out.println("Error with playing sound.");

                }
            }
            public void mouseClicked(MouseEvent e) {
                playSound("src/res/sound.wav");
                int r= -1;
                for(int x =5; x>= 0; x--){
                    if(cell[x][column].getToken() == ' '){

                        r=x;
                        break;
                    }
                }
                // If cell is not occupied and the player has the turn
                if ((r != -1) && myTurn) {
                    cell[r][column].setToken(myToken);  // Set the player's token in the cell
                    myTurn = false;
                    rowSelected = r;
                    columnSelected = column;
                    status.setText("waiting for move....");
                    waiting = false; // Just completed a successful move
                }
            }
        }
    }
}
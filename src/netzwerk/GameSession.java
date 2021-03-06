package netzwerk;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static netzwerk.Const.DISCONNECT_TOKEN;
/**
 * @author zozzy on 28.01.20
 */
class GameSession extends JFrame implements Constraints {

    String user1, user2;
    ServerSocket gameSessionSocket;
    Socket player1, player2;
    ArrayList<String> gameClients;
    private ArrayList<HandleASession> clients;
    int sessionNo = 1;

    public GameSession(String user1, String user2, ServerSocket gameSessionSocket) throws HeadlessException {
        this.user1 = user1;
        this.user2 = user2;
        this.gameSessionSocket = gameSessionSocket;
        gameClients = new ArrayList<>();
        clients = new ArrayList<>();
        listen();
    }
    private void listen() {

        try {
            System.out.println("Game Session is waiting for clients to join on port: " + 5555);


            // Ready to create a session for every two players

            player1 = gameSessionSocket.accept();

            System.out.println("Player 1 joined the game session.");

            DataOutputStream dataOutputStream = new DataOutputStream(
                    player1.getOutputStream());
            dataOutputStream.writeInt(PLAYER1);
            dataOutputStream.writeUTF(user1);
            dataOutputStream.writeUTF(user2);


            player2 = gameSessionSocket.accept();

            System.out.println("Player 2 Joined the game session");

            DataOutputStream dataOutputStream2 = new DataOutputStream(
                    player2.getOutputStream());

            dataOutputStream2.writeInt(PLAYER2);
            dataOutputStream2.writeUTF(user2);
            dataOutputStream2.writeUTF(user1);
            sessionNo++;

            System.out.println(gameClients);
            System.out.println(sessionNo);

            System.out.println("Now starting game session thread....");

            HandleASession task = new HandleASession(player1, player2);
            clients.add(task);
            new Thread(task).start();

        } catch (IOException e) {
          e.printStackTrace();
        }
    }

// Define the thread class for handling a new session for two players
class HandleASession implements Runnable, Constraints {
    Socket player1;
    Socket player2;
    DataInputStream fromPlayer1;
    DataOutputStream toPlayer1;
    DataInputStream fromPlayer2;
    DataOutputStream toPlayer2;


    // Create and initialize cells
    private char[][] cell = new char[6][7];


    private boolean continueToPlay = true;

    /**
     * Construct a thread
     */
    public HandleASession(Socket player1, Socket player2){
        this.player1 = player1;
        this.player2 = player2;

        // Initialize cells
        for (char[] row : cell)
            Arrays.fill(row, ' ');

    }

    /**
     * Implement the run() method for the thread
     */
    public void run() {
        try {
            // Create data input and output streams
            fromPlayer1 = new DataInputStream(player1.getInputStream());
            toPlayer1 = new DataOutputStream(player1.getOutputStream());
            fromPlayer2 = new DataInputStream(player2.getInputStream());
            toPlayer2 = new DataOutputStream(player2.getOutputStream());


            /**Write anything to notify player 1 to startServer
             This is just to let player 1 know to startServer*/
            toPlayer1.writeInt(1);

            /** Continuously serve the players and determine and report
             the game status to the players*/
            while (true) {


                // Receive a move from player 1
                int row = fromPlayer1.readInt();
                int column = fromPlayer1.readInt();
                char token = 'r';

                if ((row == DISCONNECT_TOKEN)) {
                    sendDisconnectRequest(toPlayer2);
                    sendDisconnectRequest(toPlayer2);

                } else if (column == DISCONNECT_TOKEN) {
                    sendDisconnectRequest(toPlayer2);
                    sendDisconnectRequest(toPlayer2);

                } else
                    cell[row][column] = 'r';
                // Check if Player 1 wins
                if (checkWin('r')) {
                    toPlayer1.writeInt(PLAYER1_WON);
                    toPlayer2.writeInt(PLAYER1_WON);
                    //make sure the 2 players are logged off
                    closeSession();
                    sendMove(toPlayer2, row, column);

                    break; // Break the loop
                } else if (isFull()) { // Check if all cells are filled
                    toPlayer1.writeInt(DRAW);
                    toPlayer2.writeInt(DRAW);
                    sendMove(toPlayer2, row, column);
                    break;
                } else {
                    // Notify player 2 to take the turn
                    toPlayer2.writeInt(CONTINUE);

                    // Send player 1's selected row and column to player 2
                    sendMove(toPlayer2, row, column);
                }

                // Receive a move from Player 2
                row = fromPlayer2.readInt();
                column = fromPlayer2.readInt();

                if ((row == DISCONNECT_TOKEN)) {
                    sendDisconnectRequest(toPlayer1);
                    sendDisconnectRequest(toPlayer1);
                    closeSession();

                } else if (column == DISCONNECT_TOKEN) {
                    sendDisconnectRequest(toPlayer1);
                    sendDisconnectRequest(toPlayer1);
                    closeSession();


                } else
                    // Check if Player 2 wins
                    cell[row][column] = 'b';
                if (checkWin('b')) {
                    toPlayer1.writeInt(PLAYER2_WON);
                    toPlayer2.writeInt(PLAYER2_WON);
                    closeSession();
                    sendMove(toPlayer1, row, column);
                    break;
                } else {
                    // Notify player 1 to take the turn
                    toPlayer1.writeInt(CONTINUE);

                    // Send player 2's selected row and column to player 1
                    sendMove(toPlayer1, row, column);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            //make sure array list is empty at end of session
            closeSession();

        }
    }
    void closeSession() {
        gameClients.remove(user1);
        gameClients.remove(user2);
    }

    /**
     * Send the move to other player
     */
    private synchronized void sendMove(DataOutputStream out, int row, int column)
            throws IOException {
        out.writeInt(row); // Send row index
        out.writeInt(column); // Send column index
    }

    private synchronized void sendDisconnectRequest(DataOutputStream out)
            throws IOException {
        out.writeInt(DISCONNECT_TOKEN);
    }

    /**
     * Determine if the cells are all occupied
     */
    private boolean isFull() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                if (cell[i][j] == ' ')
                    return false; // At least one cell is not filled

        // All cells are filled
        return true;
    }

    private boolean checkWin(char token) {

        //horizontal
        for (int row = 0; row < cell.length; row++) {
            for (int col = 0; col < cell[row].length - 3; col++) {
                if (cell[row][col] != ' ' && cell[row][col] == cell[row][col + 1]
                        && cell[row][col] == cell[row][col + 2]
                        && cell[row][col] == cell[row][col + 3])
                    return true;
            }
        }

        //vertical
        for (int col = 0; col < cell[0].length; col++) {
            for (int row = 0; row < cell.length - 3; row++) {
                if (cell[row][col] != ' ' && cell[row][col] == cell[row + 1][col]
                        && cell[row][col] == cell[row + 2][col]
                        && cell[row][col] == cell[row + 3][col])
                    return true;
            }
        }

        //diagonal oben links
        for (int row = 0; row < cell.length - 3; row++) {
            for (int col = 0; col < cell[row].length - 3; col++) {
                if (cell[row][col] != ' ' && cell[row][col] == cell[row + 1][col + 1]
                        && cell[row][col] == cell[row + 2][col + 2]
                        && cell[row][col] == cell[row + 3][col + 3])
                    return true;
            }
        }

        //diagonal oben rechts
        for (int row = 0; row < cell.length - 3; row++) {
            for (int col = 3; col < cell[row].length; col++) {
                if (cell[row][col] != ' ' && cell[row][col] == cell[row + 1][col - 1]
                        && cell[row][col] == cell[row + 2][col - 2]
                        && cell[row][col] == cell[row + 3][col - 3])
                    return true;
            }
        }
        return false;
    }

}
}
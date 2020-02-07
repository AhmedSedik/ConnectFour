package netzwerk;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

class GameSession extends JFrame implements Constraints {

     String user1, user2;
     ServerSocket gameSessionSocket;

    public GameSession(String user1, String user2, ServerSocket gameSessionSocket) throws HeadlessException {
        this.user1 = user1;
        this.user2 = user2;
        this.gameSessionSocket = gameSessionSocket;
        listen();
    }

    private void listen(){

        try {
            System.out.println("Game Session is waiting for clients to join on port: " + 5555);
            int sessionNo = 1;

            // Ready to create a session for every two players


                Socket player1 = gameSessionSocket.accept();

                System.out.println("Player 1 joined the game session.");

                DataOutputStream dataOutputStream =new DataOutputStream(
                        player1.getOutputStream());

                dataOutputStream.writeInt(PLAYER1);
                dataOutputStream.writeUTF(user1);
                dataOutputStream.writeUTF(user2);

                Socket player2 = gameSessionSocket.accept();

                System.out.println("Player 2 Joined the game session");

                DataOutputStream dataOutputStream2 =new DataOutputStream(
                    player2.getOutputStream());

                dataOutputStream2.writeInt(PLAYER2);
                dataOutputStream2.writeUTF(user2);
                dataOutputStream2.writeUTF(user1);

                System.out.println("Now starting game session thread....");

                HandleASession task = new HandleASession(player1, player2);


                new Thread(task).start();

        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }
}

// Define the thread class for handling a new session for two players
class HandleASession implements Runnable, Constraints {
    private Socket player1;
    private Socket player2;
    private String user1;
    private String user2;



    // Create and initialize cells
    private char[][] cell =  new char[6][7];


    private boolean continueToPlay = true;

    /** Construct a thread */
    public HandleASession(Socket player1, Socket player2) throws IOException {
        this.player1 = player1;
        this.player2 = player2;


        // Initialize cells
        for (char[] row: cell)
            Arrays.fill(row, ' ');

    }

    /** Implement the run() method for the thread */
    public void run() {
        try {
            // Create data input and output streams
            DataInputStream fromPlayer1 = new DataInputStream(
                    player1.getInputStream());
            DataOutputStream toPlayer1 = new DataOutputStream(
                    player1.getOutputStream());
            DataInputStream fromPlayer2 = new DataInputStream(
                    player2.getInputStream());
            DataOutputStream toPlayer2 = new DataOutputStream(
                    player2.getOutputStream());




            /**Write anything to notify player 1 to start
            This is just to let player 1 know to start*/
            toPlayer1.writeInt(1);

            /** Continuously serve the players and determine and report
             the game status to the players*/
            while (true) {
            //TODO fix waiting for disconnect bug

                // Receive a move from player 1
                int row = fromPlayer1.readInt();
                int column = fromPlayer1.readInt();
                char token = 'r';



                if ((row == 55)) {
                    sendInfo(toPlayer2,55);
                    sendInfo(toPlayer2,55);

                } else if (column == 55) {
                    sendInfo(toPlayer2, 55);
                    sendInfo(toPlayer2,55);

                }else
                    cell[row][column] = 'r';
                // Check if Player 1 wins
                if (checkWin('r')) {
                    toPlayer1.writeInt(PLAYER1_WON);
                    toPlayer2.writeInt(PLAYER1_WON);
                    sendMove(toPlayer2, row, column);

                    break; // Break the loop
                }
                else if (isFull()) { // Check if all cells are filled
                    toPlayer1.writeInt(DRAW);
                    toPlayer2.writeInt(DRAW);
                    sendMove(toPlayer2, row, column);
                    break;
                }
                else {
                    // Notify player 2 to take the turn
                    toPlayer2.writeInt(CONTINUE);

                    // Send player 1's selected row and column to player 2
                    sendMove(toPlayer2, row, column);
                }

                // Receive a move from Player 2
                row = fromPlayer2.readInt();
                column = fromPlayer2.readInt();

                if ((row == 55)) {
                    sendInfo(toPlayer1,55);
                    sendInfo(toPlayer1,55);

                } else if (column == 55) {
                    sendInfo(toPlayer1, 55);
                    sendInfo(toPlayer1,55);

                }else
                // Check if Player 2 wins
                    cell[row][column] = 'b';
                if (checkWin('b')) {
                    toPlayer1.writeInt(PLAYER2_WON);
                    toPlayer2.writeInt(PLAYER2_WON);
                    sendMove(toPlayer1, row, column);
                    break;
                }
                else {
                    // Notify player 1 to take the turn
                    toPlayer1.writeInt(CONTINUE);

                    // Send player 2's selected row and column to player 1
                    sendMove(toPlayer1, row, column);
                }
            }
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }

    /** Send the move to other player */
    private synchronized void sendMove(DataOutputStream out, int row, int column)
            throws IOException {
        out.writeInt(row); // Send row index
        out.writeInt(column); // Send column index
    }

    private synchronized void sendInfo(DataOutputStream out, int code)
    throws IOException{
        out.writeInt(code);

    }

    /** Determine if the cells are all occupied */
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
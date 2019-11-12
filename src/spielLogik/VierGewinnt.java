package spielLogik;
import java.util.Scanner;
import java.util.Stack;

public class VierGewinnt  extends Spiel implements Protokollierbar {
    //vars
    private Spieler currentSpieler;
    private Spieler spieler1;
    private Spieler spieler2;
    private Scanner userInput = new Scanner(System.in);

    private Stack stack = new Stack();
    private int lastRow;


    /*
    @Param spielfeld instance from the board to access coordinates and methods
    @Param row, col coordinates
     */
    @Override
    public boolean spielzug(Spielfeld spielfeld, int row, int col, Spieler currentSpieler) {

        //checking of the @col ist full
        if (spielfeld.getSpielfeld()[0][col] != '☐') {
            System.out.println("Stack is full!");
            spielerWechseln();

        }

        //making the move dh. assigning the color of the players on the board
        for (int row1 = spielfeld.getRows() - 1; row1 >= 0; row1--) {
            if (spielfeld.getSpielfeld()[row1][col] == '☐') {
                spielfeld.getSpielfeld()[row1][col] = currentSpieler.getFarbe();
                lastRow  = row1;
                return true;
            }

        }
        return true;
    }

    private boolean checkGewinner(Spielfeld spielfeld) {

        //horizontal
        for (int row = 0; row < spielfeld.getSpielfeld().length; row++) {
            for (int col = 0; col < spielfeld.getSpielfeld()[row].length - 3; col++) {
                if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col + 1]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col + 2]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col + 3])
                    return true;
            }
        }

        //vertical
        for (int col = 0; col < spielfeld.getSpielfeld()[0].length; col++) {
            for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++) {
                if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 1][col]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 2][col]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 3][col])
                    return true;
            }
        }

        //diagonal oben links
        for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++) {
            for (int col = 0; col < spielfeld.getSpielfeld()[row].length - 3; col++) {
                if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 1][col + 1]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 2][col + 2]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 3][col + 3])
                    return true;
            }
        }

        //diagonal oben rechts
        for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++) {
            for (int col = 3; col < spielfeld.getSpielfeld()[row].length; col++) {
                if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 1][col - 1]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 2][col - 2]
                        && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row + 3][col - 3])
                    return true;
            }
        }
        return false;
    }

    /*
    checking if the board is full and no one has won
     */
    private void checkDraw(Spielfeld board) {
        for (int i = 0; i < board.getColumns(); i++) {
            if (board.getSpielfeld()[0][i] == '☐')
                return;
        }
        System.out.println("Draw!!!");
        System.exit(0);
    }

    @Override
    public void add(int col, int lastRow, Spieler currentSpieler) {

        stack.push(currentSpieler.getName());
        stack.push(col);
        stack.push(lastRow);


    }

    @Override
    public void remove(Stack stack) {
        stack.pop();
        stack.pop();
        stack.pop();
    }

    /*
    taking all required data from User
    @Param x,y size of the board
    @Param player1Name, player2Name
    @Param farbeS1, farbeS2  colors of each player's discs
     */
    @Override
    public void durchgang() throws IndexOutOfBoundsException {
        String player1Name, player2Name;

        //getting board coordinates from user
        int x, y;
        System.out.println("please Enter Board Coordinates");
        System.out.println("Vertically: ");
        x = userInput.nextInt();
        System.out.println("Horizontally: ");
        y = userInput.nextInt();
        Spielfeld board = new Spielfeld(x, y);


        board.speilfeldFuellen(board.getSpielfeld(), '☐');
        board.feldDarestellung(board.getSpielfeld());
        //Taking players names and setting them
        System.out.println("Player 1 please enter your name:");
        player1Name = userInput.next();
        spieler1 = new Spieler(player1Name, false);
        System.out.println("Player 2 please enter your name:");

        player2Name = userInput.next();
        spieler2 = new Spieler(player2Name, false);

        //taking players Color to play with
        char farbeS1, farbeS2;
        System.out.println("Player1 color (R for Red, Y for yellow)");
        farbeS1 = userInput.next().charAt(0);
        farbeS1 = Character.toUpperCase(farbeS1);
        System.out.println("Player2 color (R for Red, Y for yellow)");
        farbeS2 = userInput.next().charAt(0);
        farbeS2 = Character.toUpperCase(farbeS2);
        //setting the color corresponding to each player
        spieler1.setFarbe(farbeS1);
        spieler2.setFarbe(farbeS2);
        currentSpieler = spieler1;

        while (true) {
            System.out.println("Player " + currentSpieler.getName() + " turn");

            System.out.println("Please enter column number between 1 and " + board.getColumns());


            if (!userInput.hasNextInt()) {
                System.out.println("Wrong, " + currentSpieler.getName() + " please enter a column number between 1 to " + board.getColumns());
                userInput.next();
                continue;

            } else {
                int number = userInput.nextInt();

                //Handling if user entered number out of the Matrix
                try {
                    spielzug(board, 0, number - 1, currentSpieler);
                    add(number-1,lastRow, currentSpieler);
                    board.feldDarestellung(board.getSpielfeld());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Wrong Entry! ");
                    continue;
                }
            }

            if (checkGewinner(board)) {
                System.out.println(currentSpieler.getName() + " HAS WON!!");
                break;
            }
            checkDraw(board);
            spielerWechseln();
        }
    }

    private void spielerWechseln() {
        if (currentSpieler == spieler1)
            currentSpieler = spieler2;
        else
            currentSpieler = spieler1;

    }
}

import java.util.Scanner;

public class Futtern extends Spiel implements Protokollierbar {
    //vars
    private static Spieler currentSpieler;
    private static Spieler spieler1;
    private static Spieler spieler2;
    Scanner userInput = new Scanner(System.in);

    private static void spielerWechseln() {
        if (currentSpieler == spieler1)
            currentSpieler = spieler2;
        else
            currentSpieler = spieler1;

    }



    @Override
    public void add(int lastCol, int lastRow, Spieler currentSpieler) {

    }

    @Override
    public void remove(int lastCol, int lastRow, Spielfeld speilfeld) {

    }


    @Override
    public boolean spielzug(Spielfeld spielfeld, int row,int col, Spieler currentSpieler) {
        for (int row1 = row; row1 < spielfeld.getSpielfeld().length; row1++){
            for (int col1=col; col1 < spielfeld.getSpielfeld()[row].length; col1++){

                if (spielfeld.getSpielfeld()[row1][col1] == '☐')
                    spielfeld.getSpielfeld()[row1][col1] = ' ';
                else
                    break;

            }
        }

        return true;
    }

    @Override
    public void durchgang() {
        String player1Name, player2Name;
//getting board coordinates from user
        int x ,y;
        System.out.println("please Enter Board Coordinates");
        System.out.println("Vertically: ");
        x = userInput.nextInt();
        System.out.println("Horizontally: ");
        y = userInput.nextInt();


        Spielfeld board = new Spielfeld(x,y);

        board.speilfeldFuellen(board.getSpielfeld(), '☐');
        board.feldDarestellung(board.getSpielfeld());

        System.out.println("Player 1 please enter your name:");

        player1Name = userInput.next();
        spieler1 = new Spieler(player1Name, false);
        System.out.println("Player 2 please enter your name:");

        player2Name = userInput.next();
        spieler2 = new Spieler(player2Name, false);


        currentSpieler = spieler1;



        while (true) {

            System.out.println("Player "+ currentSpieler.getName() + " turn");
            System.out.println("Please enter point coordinates(x,y) ");


            if (!userInput.hasNextInt()) {
                System.out.println("Wrong, " + currentSpieler.getName() + " please enter a column number between 1 to "+board.getColumns());
                userInput.next();
                continue;

            }else{
                int r = userInput.nextInt() - 1;
                int c = userInput.nextInt() - 1;
                if (c >= 0 && c <= board.getColumns()-1 && r>=0 && r<=board.getRows()-1 ) {
                    //try catch
                    spielzug(board,r,c,currentSpieler);
                    board.feldDarestellung(board.getSpielfeld());


                } else {
                    System.out.println("Wrong Entry! ");
                    continue;
                }
            }


            if (board.getSpielfeld()[0][0] == ' ') {
                System.out.println(currentSpieler.getName() + " has lost!");
                break;
            }
            spielerWechseln();

        }


    }
    //want to check if the the place alrealy played
    private boolean checkAlreadyMove(Spielfeld board) {
        for (int i = 0; i < board.getColumns(); i++) {
            if (board.getSpielfeld()[0][i] == '☐')
                return false;
        }
        System.out.println("Draw!!!");
        System.exit(0);
        return true;
    }
}

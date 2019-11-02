import java.util.Scanner;

public class Futtern extends Spiel implements Protokollierbar {

    private static Spieler currentSpieler;
    private static Spieler spieler1;
    private static Spieler spieler2;


    private static void spielerWechseln() {
        if (currentSpieler == spieler1)
            currentSpieler = spieler2;
        else
            currentSpieler = spieler1;

    }

    public boolean spielzug(Spielfeld spielfeld, int row, int col) {
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
    public void add(int lastCol, int lastRow, Spieler currentSpieler) {

    }

    @Override
    public void remove(int lastCol, int lastRow, Spielfeld speilfeld) {

    }


    @Override
    public boolean spielzug(Spielfeld spielfeld, int spalte, Spieler currentSpieler) {
        return false;
    }

    @Override
    public void durchgang() {
        Scanner scanner = new Scanner(System.in);
        int x ,y;
        x = scanner.nextInt();
        y = scanner.nextInt();
        Spielfeld board = new Spielfeld(x,y);

        board.speilfeldFuellen(board.getSpielfeld(), '☐');
        board.feldDarestellung(board.getSpielfeld());
        spieler1 = new Spieler('A', false);
        spieler2 = new Spieler('B', false);

        currentSpieler = spieler1;



        while (true) {
            System.out.println("Please enter column number");
            Scanner scanner2 = new Scanner(System.in);
            int r = scanner.nextInt() - 1;
            int c = scanner.nextInt() - 1;


            spielzug(board,r,c);
            board.feldDarestellung(board.getSpielfeld());

            if (board.getSpielfeld()[0][0] == ' ') {
                System.out.println(currentSpieler.getFarbe() + " has lost!");
                break;
            }
            spielerWechseln();

        }


    }
}

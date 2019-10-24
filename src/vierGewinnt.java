import java.util.Scanner;

public class vierGewinnt extends Spiel implements Protokollierbar {




    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        int x ,y;
        x = scanner.nextInt();
        y = scanner.nextInt();
        char [][] board = new char[x][y];
        Spielfeld.speilfeldFuellen(board,'.');
        Spielfeld.feldDarestellung(board);


    }


    @Override
    public void spielzeug(int lastCol, int lastRow, Spieler currentSpieler) {

    }

    @Override
    public void entfernen(int lastCol, int lastRow, Spielfeld speilfeld) {

    }

    @Override
    public void spielzug(Spieler spieler, Spielfeld spielfeld) {

    }

    @Override
    public void durchgang(Spieler spieler, Spielfeld spielfeld) {

    }
}

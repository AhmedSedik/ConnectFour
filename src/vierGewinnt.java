import java.util.Scanner;

public class vierGewinnt extends Spiel implements Protokollierbar {




    public static void main(String args[]) {
        Spieler spieler1,spieler2;
        Scanner scanner = new Scanner(System.in);
        int x ,y;
        x = scanner.nextInt();
        y = scanner.nextInt();
        Spielfeld board = new Spielfeld(x,y);
        /*char [][] board = new char[x][y];
        Spielfeld.speilfeldFuellen(board,'☐');
        Spielfeld.feldDarestellung(board);
*/
        board.speilfeldFuellen(board.getSpielfeld(), '☐');
        board.feldDarestellung(board.getSpielfeld());

        /*System.out.println("please Select Players");
        Scanner scanner1 = new Scanner(System.in);
        char farbeS1, farbeS2;
        farbeS1 = scanner1.next().charAt(0);
        farbeS2 = scanner1.next().charAt(0);

        spieler1 = new Spieler(farbeS1, false);
        spieler2 = new Spieler(farbeS2, false);*/






    }


    @Override
    public void add(int lastCol, int lastRow, Spieler currentSpieler) {


    }

    @Override
    public void remove(int lastCol, int lastRow, Spielfeld speilfeld) {


    }

    @Override
    public void spielzug(Spieler spieler, Spielfeld spielfeld) {

    }

    @Override
    public void durchgang(Spieler spieler, Spielfeld spielfeld) {


    }
}

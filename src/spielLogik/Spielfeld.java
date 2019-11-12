package spielLogik;
import java.util.Arrays;

public class Spielfeld {

    private int rows;
    private int columns;
    private char[][] spielfeld;
    //private LinkedList<Character> spielFeld2;


    Spielfeld(int rows, int columns) {
        this.spielfeld = new char[rows][columns];
        this.rows = rows;
        this.columns = columns;
		/*this.spielFeld2 = new LinkedList<>();
        spielFeld2.add((char) rows);
        spielFeld2.add((char) columns);*/

    }

    //Empty Constructor
    public Spielfeld() {

    }


    void speilfeldFuellen(char[][] spielfeld, char k) {
        /*for (int row = 0; row < spielfeld.length; row++) {
            java.util.Arrays.fill(spielfeld[row], 0, spielfeld[row].length, k);
        }
*/
        for (char[] chars : spielfeld) {
            Arrays.fill(chars, 0, chars.length, k);
        }

    }

    void feldDarestellung(char[][] spielfeld) {
        int row, col;


        System.out.println("");

        for (row = 0; row < spielfeld.length; row++) {
            System.out.print("|");
            for (col = 0; col < spielfeld[row].length; col++) {

                System.out.print(" " + spielfeld[row][col] + " |");

            }
            System.out.println("");


        }
        //not necessary
        for (int columnLabel = 1; columnLabel <= spielfeld[row - 1].length; columnLabel++) {

            System.out.print("  " + columnLabel + "* ");
        }
        System.out.println();


    }


    char[][] getSpielfeld() {
        return spielfeld;
    }

    public void setSpielfeld(char[][] spielfeld) {
        this.spielfeld = spielfeld;
    }

    int getRows() {
        return rows;
    }

    int getColumns() {
        return columns;
    }

    void setRows(int rows) {
        this.rows = rows;
    }

    void setColumns(int columns) {
        this.columns = columns;
    }
}

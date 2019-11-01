import java.util.Scanner;

public class vierGewinnt extends Spiel implements Protokollierbar {

	private static Spieler currentSpieler; 



    public static void main(String args[]) {
        Spieler spieler1,spieler2;
        Scanner scanner = new Scanner(System.in);
        int x ,y;
        x = scanner.nextInt();
        y = scanner.nextInt();
        Spielfeld board = new Spielfeld(x,y);
     
        /*char [][] board = new char[x][y];
        Spielfeld.speilfeldFuellen(board,'â˜�');
        Spielfeld.feldDarestellung(board);
*/
        board.speilfeldFuellen(board.getSpielfeld(), '☐');
        board.feldDarestellung(board.getSpielfeld());

        System.out.println("Farbe Player1, Player2");
        Scanner scanner1 = new Scanner(System.in);
        char farbeS1, farbeS2;
        farbeS1 = scanner1.next().charAt(0);
        farbeS2 = scanner1.next().charAt(0);

        spieler1 = new Spieler(farbeS1, false);
        spieler2 = new Spieler(farbeS2, false);
        currentSpieler = spieler1; 
        
        System.out.println("Please enter column number");
        Scanner scanner2 = new Scanner(System.in);
        int r = scanner.nextInt() - 1;
        add2oshata(board,r,currentSpieler);
        board.feldDarestellung(board.getSpielfeld());
   
        
       
       // while (true) {
        	
        	
        	
        

    }

    public static boolean add2oshata (Spielfeld spielfeld, int spalte, Spieler currentSpieler ) {
    	
    	if (spielfeld.getSpielfeld()[0][0] != '☐') {
    		System.out.println("Die Spalte ist voll!");
    		return false;
    	}
    	
    	for(int reihe = spielfeld.getRows() - 1; reihe >= 0; reihe --) {
    		
    		if(spielfeld.getSpielfeld()[reihe][spalte] == '☐') {
    				spielfeld.getSpielfeld()[reihe][spalte] = currentSpieler.getName();
    				System.out.println("hi");
    				return true; 
    		}
    	}
    	return true;
    }
    
    public boolean checkGewinner () {
    	return true;
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

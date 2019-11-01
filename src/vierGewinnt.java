import java.util.Scanner;

public class vierGewinnt extends Spiel implements Protokollierbar {

	private static Spieler currentSpieler; 
	private static Spieler spieler1;
	private static Spieler spieler2;
	

    public static void main(String args[]) {
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
     
        while (true) {   
 
        System.out.println("Please enter column number");
        Scanner scanner2 = new Scanner(System.in);
        int r = scanner.nextInt() - 1;
        add2oshata(board,r,currentSpieler);
        board.feldDarestellung(board.getSpielfeld());
        if(checkGewinner(board)) {
        	System.out.println(currentSpieler.getName() + " HAS WON!!");
        	break;
        }
        spielerWechseln();
     }
    
    }
    
    private static void spielerWechseln() {
    	if (currentSpieler == spieler1)
    		currentSpieler = spieler2;
    		else 
    			currentSpieler = spieler1;
    			
    }
    
    public static boolean add2oshata (Spielfeld spielfeld, int spalte, Spieler currentSpieler ) { 	
    	if (spielfeld.getSpielfeld()[0][0] != '☐') {
    		System.out.println("Die Spalte ist voll!");
    		return false;
    	}

    	for(int reihe = spielfeld.getRows() - 1; reihe >= 0; reihe --) {    		
    		if(spielfeld.getSpielfeld()[reihe][spalte] == '☐') {
    				spielfeld.getSpielfeld()[reihe][spalte] = currentSpieler.getName();
    				return true; 
    		}
    	}
    	return true;
    }
    
    public static boolean checkGewinner (Spielfeld spielfeld) {
    	
    	//horizontal
    	for (int row = 0; row < spielfeld.getSpielfeld().length; row++){
    		for (int col = 0; col < spielfeld.getSpielfeld()[row].length - 3; col++){
    			if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col+1] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col+2] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row][col+3])
    				return true;
    		}
    	}
    	
    	//vertical
    	for (int col = 0; col < spielfeld.getSpielfeld()[0].length; col++){
    		for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++){
    			if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+1][col]
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+2][col] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+3][col])
    				return true;
    		}
    	}
    	
    	//diagonal oben links
    	for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++){
    		for (int col = 0; col < spielfeld.getSpielfeld()[row].length - 3; col++){
    			if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+1][col+1] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+2][col+2]
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+3][col+3])
    				return true;
    		}
    	}
    	
    	//diagonal oben rechts
    	for (int row = 0; row < spielfeld.getSpielfeld().length - 3; row++){
    		for (int col = 3; col < spielfeld.getSpielfeld()[row].length; col++){
    			if (spielfeld.getSpielfeld()[row][col] != '☐' && spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+1][col-1] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+2][col-2] 
    				&& spielfeld.getSpielfeld()[row][col] == spielfeld.getSpielfeld()[row+3][col-3])
    		return true;
    		}
    	}
    	return false;
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

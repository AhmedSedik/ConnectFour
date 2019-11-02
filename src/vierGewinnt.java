import java.util.Scanner;

public class vierGewinnt extends Spiel implements Protokollierbar {
	//vars
	private Spieler currentSpieler;
	private Spieler spieler1;
	private Spieler spieler2;
	private Scanner userInput = new Scanner(System.in);


    private  void spielerWechseln() {
    	if (currentSpieler == spieler1)
    		currentSpieler = spieler2;
    		else 
    			currentSpieler = spieler1;

    }

    @Override
    public boolean spielzug (Spielfeld spielfeld, int col, Spieler currentSpieler ) {
    	if (spielfeld.getSpielfeld()[0][0] != '☐') {
    		System.out.println("Die Spalte ist voll!");
			return false;
    	}

    	for(int row = spielfeld.getRows() - 1; row >= 0; row --) {


    		if(spielfeld.getSpielfeld()[row][col] == '☐') {
    				spielfeld.getSpielfeld()[row][col] = currentSpieler.getFarbe();
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
    public void durchgang() {
    	String player1Name, player2Name;


		int x ,y;
		x = userInput.nextInt();
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


		System.out.println("Farbe Player1, Player2");

		char farbeS1, farbeS2;
		farbeS1 = userInput.next().charAt(0);
		farbeS2 = userInput.next().charAt(0);


		spieler1.setFarbe(farbeS1);
		spieler2.setFarbe(farbeS2);
		currentSpieler = spieler1;

		while (true) {
			System.out.println("Player "+ currentSpieler.getName() + " turn");
			System.out.println("Please enter column number between 1 and " + board.getColumns());


			if (!userInput.hasNextInt()) {
				System.out.println("Wrong, " + currentSpieler.getName() + " please enter a column number between 1 to "+board.getColumns());
				userInput.next();
				continue;


			}else{
				int number = userInput.nextInt();
				if (number > 0 && number <= board.getColumns()) {
					spielzug(board, number-1,currentSpieler);
					board.feldDarestellung(board.getSpielfeld());
				} else {
					System.out.println("Wrong Entry! ");
					continue;
				}
			}



			if(checkGewinner(board)) {
				System.out.println(currentSpieler.getName() + " HAS WON!!");
				break;
			}
			spielerWechseln();
		}


    }
}

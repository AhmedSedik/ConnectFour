public class Spielfeld {

	private int rows;
    private int columns;
	private char [][] spielfeld;
	//private LinkedList<Character> spielFeld2;

	
	public Spielfeld (int rows, int columns) {
        this.spielfeld = new char[rows][columns];
        this.rows = rows;
        this.columns = columns;
		/*this.spielFeld2 = new LinkedList<>();
        spielFeld2.add((char) rows);
        spielFeld2.add((char) columns);*/

	}


	public  char[][] speilfeldFuellen (char[][] spielfeld, char k) {
		for (int row = 0; row < spielfeld.length; row++){
			java.util.Arrays.fill(spielfeld[row], 0, spielfeld[row].length, k);
			}
		return spielfeld;
		
	}
    public  void feldDarestellung(char[][] spielfeld){
    	System.out.println();
    		for (int row = 0; row < spielfeld.length; row++){
    			System.out.print("|");
    			for (int col = 0; col < spielfeld[row].length; col++){
    				System.out.print(" " + spielfeld[row][col] + " |");
    	}
    	System.out.println();
    	}
    }
    	
    
	public char [][] getSpielfeld() {
		return spielfeld;
	}

	public void setSpielfeld(char [][] spielfeld) {
		this.spielfeld = spielfeld;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
}

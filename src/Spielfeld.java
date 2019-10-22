public class Spielfeld {

	private int rows;
    private int columns;
	private char [][] spielfeld;
	
	public Spielfeld () {
		this.spielfeld = new char[rows][columns];
	}

	public static char[][] speilfeldFuellen (char[][] spielfeld, char k) {
		for (int row = 0; row < spielfeld.length; row++){
			java.util.Arrays.fill(spielfeld[row], 0, spielfeld[row].length, k);
			}
		return spielfeld;
		
	}
    public static void feldDarestellung(char[][] spielfeld){
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




}

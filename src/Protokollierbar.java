public interface Protokollierbar {
	 
	static int lastCol = -1;
	static int lastRow = -1;
	
	public void spielzeug(int lastCol, int lastRow, Spieler currentSpieler);
	
	public void entfernen(int lastCol, int lastRow, Spielfeld speilfeld); 
}

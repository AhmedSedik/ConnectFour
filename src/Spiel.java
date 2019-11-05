public abstract class Spiel  {


  private Spieler currentSpieler;
  private Spielfeld spielfeld;

  
  public abstract boolean spielzug(Spielfeld spielfeld, int col,int row, Spieler currentSpieler);

  public abstract void durchgang();
    

	public Spielfeld getSpielfeld() {
		return spielfeld;
	}
	public void setSpielfeld(Spielfeld spielfeld) {
		this.spielfeld = spielfeld;
	}
	public Spieler getCurrentSpieler() {
		return currentSpieler;
	}
	public void setCurrentSpieler(Spieler currentSpieler) {
		this.currentSpieler = currentSpieler;
	}
   



}

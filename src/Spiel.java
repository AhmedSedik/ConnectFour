public abstract class Spiel {


  private Spieler currentSpieler;
  private Spielfeld spielfeld;

  
  public abstract void spielzug(Spieler spieler, Spielfeld spielfeld);
  public abstract void durchgang(Spieler spieler, Spielfeld spielfeld);
    

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

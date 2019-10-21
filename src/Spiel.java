public abstract class Spiel {

  private Spieler spieler1;
  private Spieler spieler2;
  private Spielfeld spielfeld;
  
  public abstract void spielzug(Spieler spieler1, Spieler spieler2, Spielfeld spielfeld);
  public abstract void durchgang(Spieler spieler1, Spieler spieler2, Spielfeld spielfeld);
    
	public Spieler getSpieler1() {
		return spieler1;
	}
	public void setSpieler1(Spieler spieler1) {
		this.spieler1 = spieler1;
	}
	public Spieler getSpieler2() {
		return spieler2;
	}
	public void setSpieler2(Spieler spieler2) {
		this.spieler2 = spieler2;
	}
	public Spielfeld getSpielfeld() {
		return spielfeld;
	}
	public void setSpielfeld(Spielfeld spielfeld) {
		this.spielfeld = spielfeld;
	}
   



}

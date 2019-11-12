package spielLogik;
public class Spieler {

    private char farbe;
    private String name;
    private boolean isComputer;




    Spieler(String name, boolean isComputer) {
        this.name = name;
        this.isComputer = isComputer;
    }
    //default Constructor
    Spieler() {

    }

    public char getFarbe() {
        return farbe;
    }

    public void setFarbe(char farbe) {
        this.farbe = farbe;
    }

    public boolean isComputer() {
        return isComputer;
    }

    public void setComputer(boolean computer) {
        this.isComputer = computer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

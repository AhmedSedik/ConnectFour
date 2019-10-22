public class Spieler {

    private char name;
    private boolean isComputer;

    public Spieler(char name, boolean computer) {
        this.name = name;
        this.isComputer = computer;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public boolean isComputer() {
        return isComputer;
    }

    public void setComputer(boolean computer) {
        this.isComputer = computer;
    }
}

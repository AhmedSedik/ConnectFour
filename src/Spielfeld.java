public abstract class Spielfeld {

    private char [][] spielfeld= new char[6][7];


    public Spielfeld(char[][] spielfeld, int rows, int columns) {
        this.spielfeld = spielfeld;

    }

    public abstract void feldDarestellung();




}

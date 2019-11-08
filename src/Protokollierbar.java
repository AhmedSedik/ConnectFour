import java.util.Stack;

public interface Protokollierbar {
	 
	static int col = 0;
	static int row = 0;

	
	public void add(int col, int row, Spieler currentSpieler);

	public void remove(Stack stack);
}

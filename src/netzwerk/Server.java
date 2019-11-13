package netzwerk;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import spielLogik.*;

public class Server {		
	
	
	public static void main (String[] args) throws Exception {
		try (var listener =  new ServerSocket(51719)) {
			System.out.println("The game server is running...");
			ExecutorService pool = Executors.newFixedThreadPool(10);
			while (true) {
				pool.execute(new Handler(listener.accept()));
            }
		}
		
	}

	private static class Handler implements Runnable {
		private Socket socket;
		Scanner input; 
		PrintWriter output;

		Handler(Socket socket) { this.socket = socket; }
		
		@Override 
		public void run() {
			System.out.println("Connected: " + socket);
			try {
               setup();
			}
			catch (Exception e) {
                e.printStackTrace();
			}
			finally {
        }
	}
	private void setup() throws IOException {
		input = new Scanner(socket.getInputStream());
		output = new PrintWriter(socket.getOutputStream(), true);
        output.println("Welcome. Please choose game: 1. Vier Gewinnt 2.Futtern");
        int x = input.nextInt();
        if (x == 1) {
        	VierGewinnt game1 = new VierGewinnt();
        	game1.durchgang();
		}
		
	}
	}
}


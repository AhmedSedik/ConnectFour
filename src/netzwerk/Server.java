package netzwerk;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static void main(String[] args) throws Exception {

		try (var listener = new ServerSocket(51730)) {
			System.out.println("The game server is running...");
			ExecutorService pool = Executors.newFixedThreadPool(10);
			while (true) {
				pool.execute(new Handler(listener.accept()));
			}
		}

	}

	private static class Handler implements Runnable {
		private Socket socket;

		Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			System.out.println("Connected: " + socket);
			try (

					PrintWriter out =
							new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
			) {
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					out.println(inputLine);

			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+  " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}
	}
}
		



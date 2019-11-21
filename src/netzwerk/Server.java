package netzwerk;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

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
        private String[] listOfUsers;
        private HashSet<String> h;

        Handler(Socket socket) {
            this.socket = socket;
            this.h = new HashSet<String>();
        }

        @Override
        public void run() {
			System.out.println(getUsername());
        	System.out.println("Connected: " + socket);
            try (
                    PrintWriter out =
                            new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            ) {
                String readUsername;
                String readPassword = null;

                while (((readUsername = in.readLine()) != null) &&
                        ((readPassword = in.readLine()) != null)) {
					h.add(readUsername);
					setCredentials(readUsername, readPassword);
					System.out.println("Credentials saved to preferences");

				}
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port " + socket
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
		Preferences preferences = Preferences.userRoot().node("accounts/userData");
			//	Preferences.userNodeForPackage(Handler.class);


		public void setCredentials(String username, String password) {
			preferences.put("db_username", username);
			preferences.put("db_password", password);
		}

		public String getUsername() {
			return preferences.get("db_username", null);
		}

		public String getPassword() {
			return preferences.get("db_password", null);
		}


    }
}
		



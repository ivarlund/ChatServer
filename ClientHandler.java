
import java.net.Socket;
import java.io.*;

/**
 * Client handler class for each connecting client.
 * Manages I/O and connection in a seperate thread for each
 * connected client.
 * 
 * @author Ivar Lund
 * ivlu1468
 * ivarnilslund@gmail.com
 * 
 */
public class ClientHandler extends Thread {

	private PrintWriter output;
	private InputStreamReader input;
	private BufferedReader reader;
	private final Socket clientSocket;
	private boolean alive = true;
	private final Server server;

	/**
	 * Class constructor. Takes socket and server provided at initialization.
	 * 
	 * @param socket connecting socket.
	 * @param server server main thread.
	 */
	public ClientHandler(Socket socket, Server server) {
		this.clientSocket = socket;
		this.server = server;
	}

	/**
	 * Executes worker method and terminating method when running variable is set to
	 * false.
	 */
	public void run() {
		while (alive) {
			clientWorker();
		}
		killIO();
	}

	/**
	 * Worker method of this class. Sends message to connecting sockets and
	 * terminates when connecting socket disconnects.
	 */
	private void clientWorker() {
		String msg;
		setupIO();
		try {
			while ((msg = reader.readLine()) != null) {
				server.broadcast(msg, getClientName());
			}
		} catch (IOException e) {
			server.updateDisplay("Client " + getClientName() + " brutally disconnected.");
		}
		killIO();
		server.updateDisplay("Client " + getClientName() + " disconnected.");
	}

	/**
	 * Terminating method. Closes I/O, socket and terminates thread. Tasks server to
	 * remove this thread from corresponding list.
	 */
	private void killIO() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Error: could not close socket" + clientSocket.toString() + ".");
			e.printStackTrace();
		}
		alive = false;
		server.removeClient(this);
	}

	/**
	 * Initiates I/O.
	 */
	private void setupIO() {
		try {
			input = new InputStreamReader(clientSocket.getInputStream());
			reader = new BufferedReader(input);
			output = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Error: could not setup I/O.");
			killIO();
			e.printStackTrace();
		}
	}

	/**
	 * Simple method to process and provide client name.
	 * 
	 * @return processed name of connected client.
	 */
	public String getClientName() {
		return clientSocket.getInetAddress().toString().substring(1);
	}

	/**
	 * Outputs message to connected client.
	 * 
	 * @param msg message to be output.
	 */
	public void sendMessage(String msg) {
		output.println(getClientName() + ": " + msg);
	}

}

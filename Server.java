
/**
 * Server program. Implements a server that chatclients can connect to.
 * Handles all separate threads of ClientHandler class.
 * 
 * @author Ivar Lund
 * ivlu1468
 * ivarnilslund@gmail.com'
 * 
 */
import java.util.List;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Server & main class of the server program. Puts each connecting client into
 * its own thread to store in a list.
 * 
 * @author Ivar Lund
 *
 */
@SuppressWarnings("serial")
public class Server extends JFrame {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private List<ClientHandler> clientList = Collections.synchronizedList(new ArrayList<ClientHandler>());
	private JTextArea display;

	/**
	 * Class constructor. Defines UI and executes worker method. Takes one parameter
	 * at startup for ServerSockets port number.
	 * 
	 * @param portNr The port to set for ServerSocket.
	 */
	public Server(int portNr) {
		super("");
		setLayout(new BorderLayout());

		display = new JTextArea();
		display.setEditable(false);
		JScrollPane pane = new JScrollPane(display);
		pane.setBorder(new EmptyBorder(15, 15, 15, 15));
		add(pane, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(720, 540);
		setVisible(true);

		serverWorker(portNr);
	}

	/**
	 * Class worker thread. Accepts incoming socket connections and initiates
	 * handlers for each and stores them in a synchronized list. Sets UI name to
	 * server address.
	 * 
	 * @param portNr server port number provided by constructor.
	 */
	private void serverWorker(int portNr) {
		try {
			serverSocket = new ServerSocket(portNr);
			super.setTitle("IP: " + serverSocket.getInetAddress().toString() + " port: " + portNr);
			updateDisplay("Ready for clients!");
			while (true) {
				clientSocket = serverSocket.accept();
				updateDisplay(
						"Client " + clientSocket.getInetAddress().toString().substring(1) + " connected" + "\n\r");
				ClientHandler handler = new ClientHandler(clientSocket, this);
				clientList.add(handler);
				handler.start();
			}
		} catch (IOException e) {
			System.out.println("Error: could not setup server.");
			System.exit(1);
			e.printStackTrace();
		}

	}

	/**
	 * Takes a message and name strings from handler classes and broadcasts message
	 * to all connected sockets in the handler list identified by the name.
	 * 
	 * @param msg    message to broadcast
	 * @param sender message identifying name
	 */
	public void broadcast(String msg, String sender) {
		for (ClientHandler handler : clientList) {
			if (!handler.isAlive())
				removeClient(handler);
			else
				handler.sendMessage(msg);
		}
		updateDisplay(sender + ": " + msg);
	}

	/**
	 * Method to append UI textArea.
	 * 
	 * @param str string to be appended.
	 */
	public void updateDisplay(String str) {
		display.append(str + "\n\r");
	}

	/**
	 * Simple method to remove ClientHandler threads.
	 * 
	 * @param clientHandler thread to be removed.
	 */
	public void removeClient(ClientHandler clientHandler) {
		clientList.remove(clientHandler);
	}

	/**
	 * Main method. Takes portNr as first argument.
	 * 
	 * @param args holds arguments from user.
	 */
	public static void main(String[] args) {
		int portNr = args.length > 0 ? portNr = Integer.parseInt(args[0]) : 2000;

		new Server(portNr);
	}
}

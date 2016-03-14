package edu.nku.server;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import edu.nku.common.Message;
import edu.nku.common.MessageSenderThread;

public class ServerListenerThread extends Thread {
	private Server server;
	private DataInputStream inputStream;

	public ServerListenerThread(Server server, DataInputStream inputStream) {
		this.server = server;
		this.inputStream = inputStream;
		start();
	}

	// Read InputStream for message from senderThread.
	// Read toThread name/ID from message.
	// Send message to toThread.
	public void run() {
		while (!Thread.interrupted()) {
			try {
				String jsonMessage = "";
				synchronized (inputStream) {
					jsonMessage = inputStream.readUTF();
				}
				Gson g = new Gson();
				Message messageReceived = g.fromJson(jsonMessage, Message.class);

				// Message from client that it has reached the End of File
				// Tell the server end of file has been reached
				// Close this listener thread
				if (messageReceived.messageBody.equals("END_OF_FILE")) {
					server.eofReceived();
					this.interrupt();
				} else {
					new MessageSenderThread(server.getOutputStream(messageReceived.toThread), messageReceived);
				}
			} catch (IOException e) {
				System.out.println("Error processing in ServerListenerThread - will exit");
				e.printStackTrace();
				this.interrupt();
			}
		}
	}

}

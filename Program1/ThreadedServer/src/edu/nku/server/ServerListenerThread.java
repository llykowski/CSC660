package edu.nku.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import edu.nku.common.Message;

public class ServerListenerThread extends Thread {
	private Server server;
	private Socket socket;

	public ServerListenerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		start();
	}

	// Read InputStream for message from senderThread.
	// Read toThread name/ID from message.
	// Send message to toThread.
	public void run() {
		while (true) {
			try {
				DataInputStream inputStream = new DataInputStream(socket.getInputStream());
				String jsonMessage = inputStream.readUTF();
				Gson g = new Gson();
				Message messageReceived = g.fromJson(jsonMessage, Message.class);
				server.sendMessage(messageReceived.toThread, messageReceived);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

package edu.nku.server;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import edu.nku.common.Message;

public class ServerListenerThread extends Thread {
	private Server server;
	private DataInputStream inputStream;
	private boolean shouldRun;

	public ServerListenerThread(Server server, DataInputStream inputStream) {
		this.server = server;
		this.inputStream = inputStream;
		this.shouldRun = true;
		start();
	}

	// Read InputStream for message from senderThread.
	// Read toThread name/ID from message.
	// Send message to toThread.
	public void run() {
		
		while (shouldRun) {
			try {
				String jsonMessage = "";
				int element;
				while((element = inputStream.read()) != -1)
				{
					char elementChar = (char) element;
					if(String.valueOf(elementChar).equals("{") || jsonMessage.length() >= 1)
					{
						jsonMessage = jsonMessage + String.valueOf(elementChar);	
					} else {
						continue;
					}
					
					if(String.valueOf(elementChar).equals("}"))
					{
						System.out.println(jsonMessage);
						Gson g = new Gson();
						Message messageReceived = g.fromJson(jsonMessage, Message.class);
						server.sendMessage(messageReceived.toThread, messageReceived);
						jsonMessage = "";
					}
					
				}
				
			} catch (IOException e) {
				System.out.println("Error processing in ServerListenerThread - will exit");
				shouldRun = false;
				e.printStackTrace();
			}
		}
	}

}

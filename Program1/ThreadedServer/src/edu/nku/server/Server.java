package edu.nku.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import edu.nku.common.Message;
import edu.nku.common.MessageSenderThread;

public class Server {
	private ServerSocket serverSocket;
	private HashMap<Integer, DataOutputStream> outputStreams = new HashMap<Integer, DataOutputStream>();
	private final int MAX_CONNECTIONS = 5;
	private Integer currentConnections = 0;

	// TODO: Check for all messages received/sent - close all sockets and stop
	// execution of clients & server.
	public Server(int iPort) throws IOException {
		listen(iPort);
	}

	private void listen(int iPort) throws IOException {
		serverSocket = new ServerSocket(iPort);
		// Accept 10 connections - spawn off listening thread for each
		// connection
		while (currentConnections < MAX_CONNECTIONS) {
			Socket socket = serverSocket.accept();
			outputStreams.put(currentConnections, new DataOutputStream(socket.getOutputStream()));
			new ServerListenerThread(this, new DataInputStream(socket.getInputStream()));
			currentConnections++;
		}
	}

	// Find outputStream for correct thread in HashMap
	protected synchronized DataOutputStream getOutputStream(int iToThread) {
		return outputStreams.get(iToThread);
	}

	protected void eofReceived(){
		synchronized(currentConnections){
			currentConnections--;
		}
		
		if(currentConnections == 0)
		{
			Message message = new Message(-1, -1, "CLOSE_CONNECTION", 0);
			for(DataOutputStream outputStream : outputStreams.values()){
				new MessageSenderThread(outputStream, message);
			}
		}
		
	}

	public static void main(String[] args) throws IOException {
		new Server(4448);
	}
}

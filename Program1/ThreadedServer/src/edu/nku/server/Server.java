package edu.nku.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.nku.common.Message;
import edu.nku.common.MessageSenderThread;

public class Server {
	private ServerSocket serverSocket;
	private HashMap<Integer, DataOutputStream> outputStreams = new HashMap<Integer, DataOutputStream>();
	private final int MAX_CONNECTIONS = 3;
	private int currentConnections = 0;
	private int numberOfActiveClients = 0;
	private ExecutorService threadPool;
	
	// TODO: Check for all messages received/sent - close all sockets and stop execution of clients & server.
	public Server(int iPort) throws IOException{
		listen(iPort);
	}
	
	private void listen(int iPort) throws IOException{
		serverSocket = new ServerSocket(iPort);
		threadPool = Executors.newFixedThreadPool(MAX_CONNECTIONS); 
		// Accept 10 connections - spawn off listening thread for each connection
		while(currentConnections < MAX_CONNECTIONS){
			Socket socket = serverSocket.accept();
			outputStreams.put(currentConnections, new DataOutputStream(socket.getOutputStream()));		 
			threadPool.execute(new ServerListenerThread(this, new DataInputStream(socket.getInputStream())));
			currentConnections++;
		}
		numberOfActiveClients = currentConnections;
	}
	
	// Find outputStream for correct thread in HashMap. Spawn thread to send message so no blocking.
	protected synchronized void sendMessage(int iToThread, Message sMessage) throws IOException{
		DataOutputStream outputStream;

		synchronized (outputStreams) {
			outputStream = outputStreams.get(iToThread);

			if (sMessage.messageBody.equals("READING_FINISHED")) {
				numberOfActiveClients--;
				System.out.println("Reading Finished # of active clients == " + numberOfActiveClients);
			} else {
				new MessageSenderThread(outputStream, sMessage);
			}
		}

		if (numberOfActiveClients == 0) {
			

			for (DataOutputStream outStream : outputStreams.values()) {
				Message closeMessage = new Message(0, 0, "CLOSE_MESSAGE", 0);
				new MessageSenderThread(outStream, closeMessage);
			}

			threadPool.shutdownNow();
			System.out.println("Shutting down the server now.");
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws IOException{
		new Server(4448);
	}
}

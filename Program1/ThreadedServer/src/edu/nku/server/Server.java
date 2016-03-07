package edu.nku.server;

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
	private final int MAX_CONNECTIONS = 3;
	private int currentConnections = 0;
	
	// TODO: Check for all messages received/sent - close all sockets and stop execution of clients & server.
	public Server(int iPort) throws IOException{
		listen(iPort);
	}
	
	private void listen(int iPort) throws IOException{
		serverSocket = new ServerSocket(iPort);
		
		// Accept 10 connections - spawn off listening thread for each connection
		while(currentConnections < MAX_CONNECTIONS){
			Socket socket = serverSocket.accept();
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			outputStreams.put(currentConnections, outputStream);
			new ServerListenerThread(this, socket);
			currentConnections++;
		}
	}
	
	// Find outputStream for correct thread in HashMap. Spawn thread to send message so no blocking.
	protected void sendMessage(int iToThread, Message sMessage) throws IOException{
		DataOutputStream outputStream;
		synchronized(outputStreams){
			outputStream = outputStreams.get(iToThread);
		}
		new MessageSenderThread(outputStream, sMessage);
	}
	
	static public void main(String[] args) throws IOException{
		new Server(4448);
	}
}

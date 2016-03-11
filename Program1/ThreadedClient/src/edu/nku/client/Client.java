package edu.nku.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.nku.common.LogicalClock;
import edu.nku.common.Message;
import edu.nku.common.MessageSenderThread;

public class Client extends Thread {
	private Socket socket;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private final String sHost = "localhost";
	private int processNumber;
	private LogicalClock currentLogicalLocalTime;
	private ClientListenerThread listenerThread;
	
	public Client(int processNumber, int iPort) {
		try {
			// Connect to Server. Create new Logical Clock for thread.
			// Spawn off own Listener thread to listen for messages.
			socket = new Socket(sHost, iPort);
			
			this.processNumber = processNumber;
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			this.currentLogicalLocalTime = new LogicalClock();
			
			//TODO: Create LogicalClock. Pass to Listener Thread
			this.listenerThread = new ClientListenerThread(inputStream, currentLogicalLocalTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// TODO: Read in File
		File file = new File(getClass().getClassLoader().getResource(processNumber + "input.txt").getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.split(" ").length == 1) {
					// Sleep Thread (simulate working0. Increment Clock by sleep time				
					try {
						int iSleepTime = Integer.parseInt(line);
						System.out.println("PROCESS #" + processNumber + " Sleeping for " + (iSleepTime / 1000.00)
								+ " seconds Local time = " + currentLogicalLocalTime.getCurrentTime());
						Thread.sleep(iSleepTime);
						// TODO: Increment LogicalClock by Sleep Time
						currentLogicalLocalTime.incrementTimeBySleep((iSleepTime / 1000.00));
					} catch (NumberFormatException | InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					// Send Out Message. Create message object. Create Sender Thread
					// to send message. Increment Clock by 1.
					try {
						// Message(fromThread, toThread, messageBody, currentLocalTime)
						// TODO: Add current local time as parameter for new Message
						Message messageSent = new Message(processNumber, Integer.parseInt(line.split(" ")[0]),
								line.substring(line.indexOf(" ") + 1), currentLogicalLocalTime.getCurrentTime());
						System.out.println("PROCESS #" + processNumber + " Sending message \"" + messageSent.messageBody
								+ "\" to PROCESS #" + messageSent.toThread
								+ " Local time = " + currentLogicalLocalTime.getCurrentTime());
						new MessageSenderThread(outputStream, messageSent);
						// TODO: Increment Local Time by 1 for sending message.
						currentLogicalLocalTime.incrementTimeBy1();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			scanner.close();
			
			new MessageSenderThread(outputStream, new Message(0,0,"READING_FINISHED",0));
			System.out.println("Closing out of client # " +processNumber);
			
			while(listenerThread.isAlive()){
				//something 
			}
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

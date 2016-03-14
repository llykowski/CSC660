package edu.nku.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

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

	public Client(int processNumber, int iPort) {
		try {
			// Connect to Server. Create new Logical Clock for thread.
			// Spawn off own Listener thread to listen for messages.
			socket = new Socket(sHost, iPort);

			this.processNumber = processNumber;
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			this.currentLogicalLocalTime = new LogicalClock();
			new ClientListenerThread(inputStream, currentLogicalLocalTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// Read in File
		File file = new File(getClass().getClassLoader().getResource(processNumber + "input.txt").getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.split(" ").length == 1) {
					// Sleep Thread (simulate working). Increment Clock by sleep
					// time
					try {
						int iSleepTime = Integer.parseInt(line);
						System.out.println("PROCESS #" + processNumber + " Sleeping for " + (iSleepTime / 1000.00)
								+ " seconds Local time = " + currentLogicalLocalTime.getCurrentTime());
						Thread.sleep(iSleepTime);
						// Increment LogicalClock by Sleep Time
						currentLogicalLocalTime.incrementTimeBySleep(iSleepTime);
					} catch (NumberFormatException | InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					// Send Out Message. Create message object. Create Sender Thread to send message. Increment Clock by 1.
					try {
						// Message(fromThread, toThread, messageBody, currentLocalTime)
						Message messageSent = new Message(processNumber, Integer.parseInt(line.split(" ")[0]),
								line.substring(line.indexOf(" ") + 1), currentLogicalLocalTime.getCurrentTime());
						System.out.println("PROCESS #" + processNumber + " Sending message " + messageSent.messageBody
								+ " to PROCESS #" + messageSent.toThread + " Local time = "
								+ currentLogicalLocalTime.getCurrentTime());
						synchronized(outputStream){
							new MessageSenderThread(outputStream, messageSent);
						}
						// Increment Local Time by 1 for sending message.
						currentLogicalLocalTime.incrementTimeBy1();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			scanner.close();
			
			// End of File Reached - Tell Server
			Message messageSent = new Message(processNumber, -1,
					"END_OF_FILE", currentLogicalLocalTime.getCurrentTime());
			synchronized(outputStream){
				new MessageSenderThread(outputStream, messageSent);
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

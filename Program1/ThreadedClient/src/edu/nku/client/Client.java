package edu.nku.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.nku.common.MessageSenderThread;
import edu.nku.common.Message;

public class Client extends Thread {
	private Socket socket;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private final String sHost = "localhost";
	private int processNumber;
	//private LogicalClock currentLocalTime;

	public Client(int processNumber, int iPort) {
		try {
			// Connect to Server. Create new Logical Clock for thread.
			// Spawn off own Listener thread to listen for messages.
			socket = new Socket(sHost, iPort);
			this.processNumber = processNumber;
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			//TODO: Create LogicalClock. Pass to Listener Thread
			new ClientListenerThread(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String fileName = processNumber+"input.txt";
		// TODO: Read in File
		
		// TODO: REMOVE count & Random # stuff  - Used for TESTING
		int count = 0;
		while (count < 10) {
			
			String line = "" + ((int) ((Math.random() * 3)));
			line += " message " + count + " of 10 messages for all things";
			if (processNumber == 0 && count % 2 == 0) {
				line = "" + (((int) (Math.random() * 3.) + 1) * 1000);
			}
			if (processNumber == 1 && count % 2 == 1) {
				line = "" + (((int) (Math.random() * 5.) + 1) * 1000);
			}
			if (processNumber == 2 && count % 2 == 1) {
				line = "" + (((int) (Math.random() * 2.) + 1) * 1000);
			}
			
			// Actual Program
			if (line.split(" ").length == 1) {
				// Sleep Thread (simulate working). Increment Clock by sleep time
				try {
					int iSleepTime = Integer.parseInt(line);
					System.out.println("PROCESS #" + processNumber + " Sleeping for " + (iSleepTime / 1000.00)
							+ " seconds Local time = currentLocalTime.getCurrentTime()");
					Thread.sleep(iSleepTime);
					// TODO: Increment LogicalClock by Sleep Time
				} catch (NumberFormatException | InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				// Send Out Message. Create message object. Create Sender Thread
				// to send message. Increment Clock by 1.
				try {
					// Message(fromThread, toThread, messageBody, currentLocalTime)
					//TODO: Add current local time as parameter for new Message
					Message messageSent = new Message(processNumber, Integer.parseInt(line.split(" ")[0]),
							line.substring(line.indexOf(" ") + 1), 0);
					System.out.println("PROCESS #" + processNumber + " Sending message \"" + messageSent.messageBody
							+ "\" to PROCESS #" + messageSent.toThread + " Local time = currentLocalTime.getCurrentTime()");
					new MessageSenderThread(outputStream, messageSent);
					// TODO: Increment Local Time by 1 for sending message.
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			count++;
		}
	}

}

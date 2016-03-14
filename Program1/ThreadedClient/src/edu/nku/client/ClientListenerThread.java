package edu.nku.client;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import edu.nku.common.LogicalClock;
import edu.nku.common.Message;

public class ClientListenerThread extends Thread {
	private DataInputStream inputStream;
	private LogicalClock currentLogicalLocalTime;

	public ClientListenerThread(DataInputStream inputStream, LogicalClock currentLogicalLocalTime) {
		this.inputStream = inputStream;
		this.currentLogicalLocalTime = currentLogicalLocalTime;
		start();
	}

	// Listen for messages through the input stream. Parse JSON message.
	// Increment Local time based on decision of MAX clock from other process
	public void run() {
		while (!Thread.interrupted()) {
			try {
				String jsonMessage = "";
				synchronized (inputStream) {
					jsonMessage = inputStream.readUTF();
				}
				Gson g = new Gson();
				Message messageReceived = g.fromJson(jsonMessage, Message.class);

				// Increment Logical Clock. Decide between received clock and
				// current clock.
				currentLogicalLocalTime.decideIncrement(messageReceived.localTime);

				// Message from server to close down connections once every
				// process has reached end of file.
				if (messageReceived.messageBody.equals("CLOSE_CONNECTION")) {
					this.interrupt();
				} else {
					// Print out Message & Current Time.
					System.out.println(
							"PROCESS #" + messageReceived.toThread + " Receiving message " + messageReceived.messageBody
									+ " Local time = " + currentLogicalLocalTime.getCurrentTime());
				}
			} catch (IOException e) {
				System.out.println("Error processing in ClientListenerThread - will exit");
				e.printStackTrace();
				this.interrupt();
			}
		}
	}
}

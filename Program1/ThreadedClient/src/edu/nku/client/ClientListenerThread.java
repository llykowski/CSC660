package edu.nku.client;

import java.io.DataInputStream;
import java.io.IOException;
import com.google.gson.Gson;

import edu.nku.common.Message;

public class ClientListenerThread extends Thread {
	private DataInputStream inputStream;
	// private LogicalClock currentLocalTime;

	// TODO: Pass LogicalClock to thread also
	public ClientListenerThread(DataInputStream inputStream) {
		this.inputStream = inputStream;
		// this.currentLocalTime = currentLocalTime;
		start();
	}

	// Listen for messages through the input stream. Parse JSON message.
	// Increment Local time based on decision of MAX clock from other process
	public void run() {
		while (true) {
			try {
				String jsonMessage = inputStream.readUTF();
				Gson g = new Gson();
				Message messageReceived = g.fromJson(jsonMessage, Message.class);
				// TODO: Increment Logical Clock. Decide between current clock
				// time and clock time received from message.
				// Print out Current Time.
				System.out.println("PROCESS #" + messageReceived.toThread + " Receiving message \""
						+ messageReceived.messageBody + "\" Local time = currentLocalTime.getCurrentTime()");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

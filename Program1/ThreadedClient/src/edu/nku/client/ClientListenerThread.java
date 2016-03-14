package edu.nku.client;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import edu.nku.common.LogicalClock;
import edu.nku.common.Message;

public class ClientListenerThread extends Thread {
	private DataInputStream inputStream;
	private LogicalClock currentLogicalLocalTime;

	// TODO: Pass LogicalClock to thread also
	public ClientListenerThread(DataInputStream inputStream, LogicalClock currentLogicalLocalTime) {
		this.inputStream = inputStream;
		this.currentLogicalLocalTime = currentLogicalLocalTime;
		start();
	}

	// Listen for messages through the input stream. Parse JSON message.
	// Increment Local time based on decision of MAX clock from other process
	public void run() {
		while (shouldRun) {
			try {
				String jsonMessage = "";
				int element;
				while ((element = inputStream.read()) != -1) {
					char elementChar = (char) element;
					if (String.valueOf(elementChar).equals("{") || jsonMessage.length() >= 1) {
						jsonMessage = jsonMessage + String.valueOf(elementChar);
					} else {
						continue;
					}

					if (String.valueOf(elementChar).equals("}")) {
						System.out.println(jsonMessage);
						Gson g = new Gson();
						Message messageReceived = g.fromJson(jsonMessage, Message.class);
						// TODO: Increment Logical Clock. Decide between current
						// clock
						currentLogicalLocalTime.decideIncrement(messageReceived.localTime);
						// TODO: add a good comment

						if (messageReceived.messageBody.equals("CLOSE_MESSAGE")) {
							System.out.println("Closing down client #");
							break;
						}
						// time and clock time received from message.
						// Print out Current Time.
						System.out.println("PROCESS #" + messageReceived.toThread + " Receiving message \""
								+ messageReceived.messageBody + "\" Local time = "
								+ currentLogicalLocalTime.getCurrentTime());
						jsonMessage = "";
					}

				}
				break;
			} catch (IOException e) {
				System.out.println("Error processing in ServerListenerThread - will exit");
				shouldRun = false;
				e.printStackTrace();
			}
		}
	}
}

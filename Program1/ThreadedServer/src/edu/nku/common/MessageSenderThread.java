package edu.nku.common;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

public class MessageSenderThread extends Thread {
	private DataOutputStream outputStream;
	private Message message;

	public MessageSenderThread(DataOutputStream outputStream, Message message) {
		this.outputStream = outputStream;
		this.message = message;
		this.start();
	}

	// Write a message out to the outputStream as JSON
	public void run() {
		try {
			Gson g = new Gson();
			synchronized (outputStream) {
				outputStream.writeUTF(g.toJson(message));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

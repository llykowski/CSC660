package edu.nku.common;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

public class MessageSenderThread extends Thread {
	private DataOutputStream outputStream;
	private Message message;
	public MessageSenderThread(DataOutputStream outputStream, Message message){
		this.outputStream = outputStream;
		this.message = message;
		this.start();
	}
	
	public void run(){
		try {
			Gson g = new Gson();
			outputStream.writeUTF(g.toJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

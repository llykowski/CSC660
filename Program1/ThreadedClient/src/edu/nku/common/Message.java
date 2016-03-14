package edu.nku.common;

public class Message {
	public int fromThread;
	public int toThread;
	public String messageBody;
	public int localTime;
	
	public Message(int fromThread, int toThread, String messageBody, int localTime){
		this.fromThread = fromThread;
		this.toThread = toThread;
		this.messageBody = messageBody;
		this.localTime = localTime;
	}
}

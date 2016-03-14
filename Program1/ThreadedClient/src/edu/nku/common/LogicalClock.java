package edu.nku.common;

public class LogicalClock {
	private double currentTime;
	
	public LogicalClock(){
		currentTime = 0;
	}
	
	public synchronized void incrementTimeBy1(){
		currentTime++;
	}
	
	public synchronized void incrementTimeBySleep(double sleepTime){
		currentTime += sleepTime;
	}
	
	public synchronized void decideIncrement(int otherTime){
		currentTime = Math.max(currentTime + 1, otherTime + 1);
	}
	
	public synchronized double getCurrentTime(){
		return currentTime;
	}
	
	
}

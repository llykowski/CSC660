package edu.nku.common;

public class LogicalClock {
	private int currentTime;

	public LogicalClock() {
		currentTime = 0;
	}

	public synchronized void incrementTimeBy1() {
		currentTime++;
	}

	public synchronized void incrementTimeBySleep(int sleepTime) {
		currentTime += sleepTime;
	}

	public synchronized void decideIncrement(int otherTime) {
		currentTime = Math.max(currentTime + 1, otherTime + 1);
	}

	public synchronized int getCurrentTime() {
		return currentTime;
	}

}

package model;

import java.io.Serializable;

public class SunFlower extends Plant {

	protected static final int FULL_TIME = 0;	
	protected static final int STRENGTH = 0;
	protected static final int FULL_HEALTH = 100;
	protected static final int PRICE = 50;
	public static final int COIN_BONUS = 25;
	static int currentTime = 0;
	
	/**
	 * The constructor, constructs a SunFlower by calling the plant class with a super method.
	 */
	public SunFlower() {
		super(FULL_TIME,STRENGTH,FULL_HEALTH,PRICE,"SunFlower");
	}
		
	//Can't inherit the function since this one deals with static vars. Can override though
	/**
	 * This method keeps track of the turns for when the sunflower is available again.
	 */
	public void newTurn() {
		if (currentTime!= 0)
			currentTime = currentTime - 1;
	}
	
	/**
	 * This method checks if the sunflower is available.
	 * @return A boolean, true if it is available otherwise false.
	 */
	public boolean isAvailable() {
		return (currentTime == 0);
	}
	
	/**
	 * This method makes the sunflower available to the player.
	 */
	public void makeAvailable() { //Not used but might need at some point? 
		currentTime = 0;
	}
	
	/**
	 * This method gets the current time (Will be overridden by the child class).
	 * @return A int, returns the current time.
	 */
	public int getCurrentTime() {
		return currentTime;
	}
	
	/**
	 * This method sets current time.
	 * @param currentTime
	 */
	public void setCurrentTime(int currentTime) {
		SunFlower.currentTime = currentTime;
	}

	@Override
	/**
	 * This method resets the plants static timer.
	 */
	public void resetTime() {
		currentTime = fullTime;
	}
}

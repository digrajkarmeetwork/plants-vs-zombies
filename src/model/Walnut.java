package model;

public class Walnut extends Plant {
	protected static final int FULL_TIME = 4;	
	protected static final int STRENGTH = 0; 
	protected static final int FULL_HEALTH = 1000;
	protected static final int PRICE = 50;
	private static int currentTime = 0;
	
	/**
	 * The constructor, constructs a Walnut by calling the plant class with a super method.
	 */
	public Walnut() {
		super(FULL_TIME,STRENGTH,FULL_HEALTH,PRICE,"Walnut");
	}
	
	/**
	 * This method decrements the plant's static timer.
	 */
	@Override
	public void newTurn() {
		if (currentTime != 0)
			currentTime = currentTime - 1;
	}

	/**
	 * This method check for when the pea shooter is available for the player to
	 * purchase and use in the game.
	 * @return True if the current time is equal to zero otherwise false.
	 */
	@Override
	public boolean isAvailable() {
		return (currentTime == 0);
	}

	/**
	 * This method gets the current time.
	 * @return currentTime.
	 */
	@Override
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * This method sets the pea shooter's current time.
	 * @param currentTime
	 */
	@Override
	public void setCurrentTime(int currentTime) {
		Walnut.currentTime = currentTime;
	}

	/**
	 * This method resets the plants static timer.
	 */
	@Override
	public void resetTime() {
		currentTime = fullTime;
	}
	
}

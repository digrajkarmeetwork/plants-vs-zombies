package model;

public class Potatoe extends Plant {

	protected static final int FULL_TIME = 2;	
	protected static final int STRENGTH = 1000; 
	protected static final int FULL_HEALTH = 1;
	protected static final int PRICE = 25;
	private static int currentTime = 0;
	
	/**
	 * This constructor, constructs a Potatoe by calling the plant class with a super method.
	 */
	public Potatoe() {
		super(FULL_TIME,STRENGTH,FULL_HEALTH,PRICE,"Potatoe");
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
		Potatoe.currentTime = currentTime;
	}

	/**
	 * This method resets the plants static timer.
	 */
	@Override
	public void resetTime() {
		currentTime = fullTime;
	}
}

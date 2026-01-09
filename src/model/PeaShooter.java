package model;

public class PeaShooter extends Plant {

	protected static final int FULL_TIME = 2;
	protected static final int STRENGTH = 150;
	protected static final int FULL_HEALTH = 500;
	protected static final int PRICE = 100;
	private static int currentTime = 0;
	
	/**
	 * This constructor, constructs a pea shooter by calling the plant class with a
	 * super method.
	 */
	public PeaShooter() {
		super(FULL_TIME, STRENGTH, FULL_HEALTH, PRICE, "PeaShooter");
	}

	/**
	 * This method is when the Pea Shooter shoots a zombie.
	 */
	@Override
	public void go(Board board) {
		for (int i = board.getX(this); i < Board.GRID_WIDTH; i++) {
			if (board.getObject(board.getY(this), i) instanceof Zombie) {
				attack((Zombie) board.getObject(board.getY(this), i));
			}
		}
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
		PeaShooter.currentTime = currentTime;
	}

	/**
	 * This method resets the plants static timer.
	 */
	@Override
	public void resetTime() {
		currentTime = fullTime;
	}
}

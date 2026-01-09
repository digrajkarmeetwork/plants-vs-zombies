package model;

public class BurrowingBailey extends Zombie{
	public static final int STRENGTH = 100;
	public static final int FULL_HEALTH = 200;
	

	/**
	 * The constructor, constructs BurrowingBailey by calling the Zombie class with a super method.
	 */
	public BurrowingBailey() {
		super(STRENGTH, FULL_HEALTH, "BurrowingBailey");
	}
	
	/**
	 * This method allows BurrowingBailey to move on the board.
	 */
	@Override
	public void go(Board board) {
		GridObject l = board.toTheLeft(this);
		GridObject ll = board.toTheLeft(l);
		
		//BurrowingBailey burrows under the next plant if there is a NullSPace
		//To the next plant's left
		if (l instanceof Plant && ll instanceof NullSpace)
			board.move(this, (NullSpace) ll);
		//Regular move to the left.
		else if (l instanceof NullSpace)
			board.move(this, (NullSpace) l);
		//Regular attack
		else if (l instanceof Plant)
			attack((Plant)l);
	}
	
}
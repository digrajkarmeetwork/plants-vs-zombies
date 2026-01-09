package model;

public class FrankTheTank extends Zombie{
	public static final int STRENGTH = 150;
	public static final int FULL_HEALTH = 400;
	

	/**
	 * The constructor, constructs FrankTheTank by calling the Zombie class with a super method.
	 */
	public FrankTheTank() {
		super(STRENGTH, FULL_HEALTH, "FrankTheTank");
	}
}
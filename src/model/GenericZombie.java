package model;

public class GenericZombie extends Zombie{
	public static final int STRENGTH = 100;
	public static final int FULL_HEALTH = 250;
	
	/**
	 * The constructor, constructs a GenericZombie by calling the Zombie class with a super method.
	 */
	public GenericZombie() {
		super(STRENGTH, FULL_HEALTH, "GenericZombie");
	}
}
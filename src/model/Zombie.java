package model;

import java.io.Serializable;

public abstract class Zombie extends GridObject implements Serializable {
	protected int fullHealth;
	protected int strength; //The amount of health taken off a plant from 1 attack
	protected int health;
	
	/**
	 * This constructor, construct a zombie.
	 * @param strength
	 * @param health
	 * @param objectTitle
	 */
	public Zombie(int strength, int health, String objectTitle) {
		this.fullHealth = health;
		this.strength = strength;
		this.health = health;
		this.objectTitle = objectTitle;
	}
	
	/**
	 * This method allows the zombies to move on the board.
	 */
	public void go(Board board) {
		GridObject left = board.toTheLeft(this);
		if (left instanceof Plant )
			((Plant)left).loseHealth(strength);
		else if (left instanceof NullSpace)
			board.move(this, (NullSpace)left);		
	}
	
	/**
	 * This method is for when a zombie loses health when being attacked by a plant.
	 * If the zombie's health is zero it is removed from the board.
	 * @param plantStrength(int), the plant strength for attacking a zombie.
	 */
	public void loseHealth(int plantStrength) {
		health = health - plantStrength;
	}

	/**
	 * This method is for when a zombie attacks a plant.
	 * @param plant (plant), a plant that is being attacked.
	 */
	public void attack(Plant plant) {
		plant.loseHealth(strength);
	}
	
	/**
	 * This method gets strength.
	 * @return strength
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * This method sets the strength.
	 * @param strength
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * This method gets the health.
	 * @return health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * This method sets the health
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	
	/**
	 * This method gets the fullHealth
	 * @return fullHealth
	 */
	public int getFullHealth() {
		return fullHealth;
	}
	
	/**
	 * This method compares zombie objects. returns true both zombies are 
	 * of the same type and have equal attributes.
	 * @param zombie
	 * @return boolean
	 */
	public boolean equals(Zombie zombie) {
		if (!(zombie.getObjectTitle().equals(getObjectTitle())))
			return false;
		if (!(zombie.health == health))
			return false;
		return true;
	}
}
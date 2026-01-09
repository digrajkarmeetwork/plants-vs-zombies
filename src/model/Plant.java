package model;

import java.io.Serializable;

public abstract class Plant extends GridObject implements Serializable{
	public int fullHealth;
	public int fullTime; 
	protected int health;
	protected int strength;
	protected int price;

	/**
	 * This constructs a plant.
	 */
	public Plant(int fullTime, int strength, int health, int price, String objectTitle) {
		this.fullHealth = health;
		this.fullTime = fullTime;
		this.strength = strength;
		this.health = health;
		this.objectTitle = objectTitle;
		this.price = price;
	}
	
	/**
	 * This method allows the plants to move on the board.
	 */
	public void go(Board board) {
		GridObject right = board.toTheRight(this);
		if (right instanceof Zombie)
			((Zombie)right).loseHealth(strength);;
	}
	
	/**
	 * This method reduces the plant's health when a zombie has attack the plant.
	 * If the plant's health is zero it is removed from the board.
	 * @param zombieStrength (int), the zombie's strength for attacking a plant.
	 */
	public void loseHealth(int zombieStrength) {
		health = health - zombieStrength;
	}
	
	/**
	 * This method is for when a plant attacks a zombie (Will be overridden by the child class).
	 * @param zombie (Zombie), a zombie that is being attacked.
	 */
	public void attack(Zombie zombie) {
		zombie.loseHealth(strength);
	}
	
	/**
	 * This method decrements the plants static timer.
	 */
	public abstract void newTurn();
	
	/**
	 * This method resets the time (Will be overridden by the child class).
	 */
	public abstract void resetTime();
	
	
	/**
	 * This method gets the current time (Will be overridden by the child class).
	 * @return A int, returns the current time.
	 */
	public abstract int getCurrentTime();
	
	/**
	 * This method checks if the plant is available for the round (Will be overridden by the child class).
	 * @return A boolean, true if it is available otherwise false.
	 */
	public abstract boolean isAvailable();
	
	/**
	 * This method gets the full time.
	 * @return fullTime
	 */
	public int getFullTime() {
		return fullTime;
	}

	/**
	 * This method sets full time.
	 * @param fullTime
	 */
	public void setFullTime(int fullTime) {
		this.fullTime = fullTime;
	}

	/**
	 * This method gets health.
	 * @return health.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * This method sets health.
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * This method gets strength.
	 * @return strength.
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * This set strength.
	 * @param strength
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * This method gets the price.
	 * @return price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * This method sets price.
	 * @param price
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * This method sets current time.
	 * @param currentTime
	 */
	public abstract void setCurrentTime(int currentTime);
	
	/**
	 * This method gets the fullHealth
	 * @return fullHealth
	 */
	public int getFullHealth() {
		return fullHealth;
	}
	
	/**
	 * This method compares this Plant to another plant
	 * returns true if they are both the same class and have
	 * all the same attributes.
	 * @param plant
	 * @return boolean
	 */
	public boolean equals(Plant plant) {
		if (!(plant.getObjectTitle().equals(getObjectTitle())))
			return false;
		if (!(plant.health == health))
			return false;
		if (!(plant.getCurrentTime() == getCurrentTime()))
			return false;
		return true;
	}
}

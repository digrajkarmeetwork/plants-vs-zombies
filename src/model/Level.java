package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {
	public ArrayList<Plant> allPlants; // Not actually all plants, its all plant TYPES (1 instance of each)
	private ArrayList<Zombie> allZombies;
	public int coins = 50;
	private int numOfZombies;
	private int levelNo;
	private boolean isCustomLevel;

	public Level(int lvl) {
		levelNo = lvl;

		if (levelNo == 1)
			level1();
		else if (levelNo == 2)
			level2();
		else if(levelNo == 3)
			level3();
	}
	
	/**
	 * Custom Level Builder
	 * @param zombies
	 */
	public Level(ArrayList<Zombie> zombies) {
		setCustomLevel(true);
		this.coins = 50;
		allPlants = new ArrayList<Plant>();
		allPlants.add(new SunFlower());
		allPlants.add(new VenusFlyTrap());
		allPlants.add(new Potatoe());
		allPlants.add(new Walnut());
		allPlants.add(new PeaShooter());
		this.allZombies = zombies;
		for (Plant plant : allPlants) {
			plant.setCurrentTime(0);
		}
	}

	/**s
	 * sets the num of zombies
	 */
	public void setNumOfZombies(int num) {
		numOfZombies = num; 
	}

	/**
	 * gets the num of zombies
	 */
	public int getNumOfZombies() {
		return numOfZombies;
	}

	/**
	 * if next level exists return true, false otherwise
	 */
	public boolean nextLevelExists() {
		if (levelNo < 3) {
			return true;
		}
		return false;
	}

	public int getLevelNo() {
		return levelNo;
	}
	
	/**
	 * This method is the first level of the game.
	 */
	public void level1() {
		startLevel();
		numOfZombies = 2;
		int previousCoins = coins; // The constructors below will take away coins must account for this
		// available plants keeps ONLY 1 instance of each type available in level
		allPlants.add(new SunFlower());
		allPlants.add(new VenusFlyTrap());
		allPlants.add(new Potatoe());
		allPlants.add(new Walnut());
		allPlants.add(new PeaShooter());
		coins = previousCoins;

		// zombies type/amount needs to be generated/level
		for (int i = 0; i < numOfZombies; i++) {
			allZombies.add(new GenericZombie());
		}

		for (Plant plant : allPlants) {
			plant.setCurrentTime(0);
		}
	}

	/**
	 * This method is the second level of the game.
	 */
	public void level2() {
		startLevel();
		numOfZombies = 3;
		int previousCoins = coins; // The constructors below will take away coins must account for this
		// available plants keeps ONLY 1 instance of each type available in level
		allPlants.add(new SunFlower());
		allPlants.add(new VenusFlyTrap());
		allPlants.add(new Potatoe());
		allPlants.add(new Walnut());
		allPlants.add(new PeaShooter());
		coins = previousCoins;

		// zombies type/amount needs to be generated/level
		for (int i = 0; i < numOfZombies; i++) {
			allZombies.add(new GenericZombie());
			allZombies.add(new FrankTheTank());
		}

		for (Plant plant : allPlants) {
			plant.setCurrentTime(0);
		}
	}

	/**
	 * This method is the third level of the game.
	 */
	public void level3() {
		startLevel();
		numOfZombies = 4;
		int previousCoins = coins; // The constructors below will take away coins must account for this
		// available plants keeps ONLY 1 instance of each type available in level
		allPlants.add(new SunFlower());
		allPlants.add(new VenusFlyTrap());
		allPlants.add(new Potatoe());
		allPlants.add(new Walnut());
		allPlants.add(new PeaShooter());
		coins = previousCoins;

		// zombies type/amount needs to be generated/level
		for (int i = 0; i < numOfZombies; i++) {
			allZombies.add(new GenericZombie());
			allZombies.add(new FrankTheTank());
			allZombies.add(new BurrowingBailey());
		}

		for (Plant plant : allPlants) {
			plant.setCurrentTime(0);
		}
	}

	/**
	 * This method initialize the plant and zombie array.
	 */
	public void startLevel() {
		// something to clean the board here;
		allZombies = new ArrayList<Zombie>();
		allPlants = new ArrayList<Plant>();
	}

	/**
	 * This method checks that a player has a plant they can play.
	 * @return True if the player can afford a plant otherwise false.
	 */
	public boolean plantAffordable() {
		for (Plant plant : allPlants) {
			if (plant.getPrice() < coins)
				return true;
		}
		return false;
	}

	/**
	 * This method returns all the zombies that are to be played for the current
	 * level.
	 * @return allZombies
	 */
	public ArrayList<Zombie> getAllZombies() {
		return allZombies;
	}

	/**
	 * This method sets all the zombies that are to be played for the current level.
	 * @param zombies.
	 */
	public void setAllZombies(ArrayList<Zombie> zombies) {
		allZombies = zombies;
	}

	/**
	 * This method checks if there are no more zombies left to play for the current
	 * level. 
	 * @return True if the array of zombies is empty otherwise false.
	 */
	public boolean zombiesEmpty() {
		return (allZombies.isEmpty());
	}

	/**
	 * This method gets all the plants array.
	 * @return plant array.
	 */
	public ArrayList<Plant> getAllPlants() {
		return allPlants;
	}

	public boolean isCustomLevel() {
		return isCustomLevel;
	}

	public void setCustomLevel(boolean isCustomLevel) {
		this.isCustomLevel = isCustomLevel;
	}
}

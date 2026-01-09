package model;

import java.util.ArrayList;

import junit.framework.TestCase;

public class LevelTest extends TestCase {
	private Level level;
	
	protected void setUp() throws Exception {
		level = new Level(1);
	}

	/**
	 * This method test if zombies empty is false.
	 */
	public void testLevelIsEmptyFalse() {
		assertEquals(level.zombiesEmpty(),false);
	}
	
	/**
	 * This method test if zombies empty is true.
	 */
	public void testLevelIsEmptyTrue() {
		level.setAllZombies(new ArrayList<Zombie>());
		assertEquals(level.zombiesEmpty(),true);
	}
	
	/**
	 * This method test plant affordable is true.
	 */
	public void testPlantAffordableTrue() {
		level.coins = 9999;
		assertEquals(level.plantAffordable(),true);
	}
	
	/**
	 * This method test plant affordable is false.
	 */
	public void testPlantAffordableFalse() {
		level.coins = -1;
		assertEquals(level.plantAffordable(),false);
	}
}

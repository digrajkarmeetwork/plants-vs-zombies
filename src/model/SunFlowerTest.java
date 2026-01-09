package model;

import junit.framework.TestCase;

public class SunFlowerTest extends TestCase {
	
	private SunFlower sunFlower;
	private Board board;
	private Level level;
	
	protected void setUp() {
		sunFlower = new SunFlower();
		level = new Level(1);
	}
	
	/**
	 * This method tests the newTurn method if current time is not 0.
	 */
	public void testNewTurnNotZero() {
		sunFlower.setCurrentTime(3);
		sunFlower.newTurn();
		assertEquals(sunFlower.getCurrentTime(),2);
	}
	
	/**
	 * This method tests the newTurn() method if current time is 0.
	 */
	public void testNewTurnZero() {
		sunFlower.setCurrentTime(0);
		sunFlower.newTurn();
		assertEquals(sunFlower.getCurrentTime(),0);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is 0.
	 */
	public void testIsAvailableTrue() {
		sunFlower.setCurrentTime(0);
		assertEquals(sunFlower.isAvailable(),true);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is not 0.
	 */
	public void testIsAvailableFalse() {
		sunFlower.setCurrentTime(3);
		assertEquals(sunFlower.isAvailable(),false);
	}
}

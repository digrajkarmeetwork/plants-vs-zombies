package model;

import junit.framework.TestCase;

public class WalnutTest extends TestCase {
	
	private Walnut walnut;
	private Board board;
	private Level level;
	
	protected void setUp() {
		walnut = new Walnut();
		level = new Level(1);
	}
	
	/**
	 * This method test the newTurn method if the current time is not 0
	 */
	public void testNewTurnNotZero() {
		walnut.setCurrentTime(3);
		walnut.newTurn();
		assertEquals(walnut.getCurrentTime(),2);
	}
	
	/**
	 * This method tests the newTurn() method if current time is 0.
	 */
	public void testNewTurnZero() {
		walnut.setCurrentTime(0);
		walnut.newTurn();
		assertEquals(walnut.getCurrentTime(),0);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is 0.
	 */
	public void testIsAvailableTrue() {
		walnut.setCurrentTime(0);
		assertEquals(walnut.isAvailable(),true);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is not 0.
	 */
	public void testIsAvailableFalse() {
		walnut.setCurrentTime(3);
		assertEquals(walnut.isAvailable(),false);
	}
}

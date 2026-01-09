package model;

import junit.framework.TestCase;

public class PotatoeTest extends TestCase {
	private Potatoe potatoe;
	private Board board;
	private Level level;
	
	protected void setUp() {
		potatoe = new Potatoe();
		level = new Level(1);
	}
	
	/**
	 * This method tests the newTurn method if current time is not 0.
	 */
	public void testNewTurnNotZero() {
		potatoe.setCurrentTime(3);
		potatoe.newTurn();
		assertEquals(potatoe.getCurrentTime(),2);
	}
	
	
	/**
	 * This method tests the newTurn() method if current time is 0.
	 */
	public void testNewTurnZero() {
		potatoe.setCurrentTime(0);
		potatoe.newTurn();
		assertEquals(potatoe.getCurrentTime(),0);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is 0.
	 */
	public void testIsAvailableTrue() {
		potatoe.setCurrentTime(0);
		assertEquals(potatoe.isAvailable(),true);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is not 0.
	 */
	public void testIsAvailableFalse() {
		potatoe.setCurrentTime(3);
		assertEquals(potatoe.isAvailable(),false);
	}
}

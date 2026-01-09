package model;

import junit.framework.TestCase;

public class PeaShooterTest extends TestCase {

	private PeaShooter peaShooter;
	private Board board;
	private Level level;
	
	protected void setUp() {
		peaShooter = new PeaShooter();
		level = new Level(1);
	}
	
	/**
	 * This method test the newTurn method if the current time is not 0
	 */
	public void testNewTurnNotZero() {
		peaShooter.setCurrentTime(3);
		peaShooter.newTurn();
		assertEquals(peaShooter.getCurrentTime(),2);
	}
	
	/**
	 * This method tests the newTurn() method if current time is 0.
	 */
	public void testNewTurnZero() {
		peaShooter.setCurrentTime(0);
		peaShooter.newTurn();
		assertEquals(peaShooter.getCurrentTime(),0);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is 0.
	 */
	public void testIsAvailableTrue() {
		peaShooter.setCurrentTime(0);
		assertEquals(peaShooter.isAvailable(),true);
	}
	
	/**
	 * This method tests the isAvailable() method if current time is not 0.
	 */
	public void testIsAvailableFalse() {
		peaShooter.setCurrentTime(3);
		assertEquals(peaShooter.isAvailable(),false);
	}

}

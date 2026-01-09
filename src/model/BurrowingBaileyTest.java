package model;

import junit.framework.TestCase;

public class BurrowingBaileyTest extends TestCase {
	private BurrowingBailey b1;
	private SunFlower s1;
	private VenusFlyTrap v1,v2;
	private Board board;
	private Level level;

	protected void setUp() {
		board = new Board();
		board.setupGrid();
		b1 = new BurrowingBailey();
		s1 = new SunFlower();
		v1 = new VenusFlyTrap();
		v2 = new VenusFlyTrap();
		level = new Level(1);
		board.setLevel(level);
	}
	
	/**
	 * This method tests go when burrowingbailey moves one to the left
	 */
	public void testGoAdvanceOne() {
		board.placeZombie(b1,1,5);
		board.boardTurn();
		assertEquals(b1, board.getObject(1, 4));
	}
	
	/**
	 * This method tests go when burrowingbailey tunnels under a plant
	 */
	public void testGoAdvanceTwo() {
		board.placeZombie(b1, 1, 5);
		board.placePlant(v1, 1, 4);
		board.boardTurn();
		assertEquals(b1, board.getObject(1, 3));
	}
	
	/**
	 * This method tests go when burrowing bailey is blocked by two plants
	 * and must attack.
	 */
	public void testGoAttack() {
		board.placeZombie(b1, 1, 5);
		board.placePlant(v1, 1, 4);
		board.placePlant(s1, 1, 3);
		board.boardTurn();
		assertEquals(v1.getHealth(),v1.getFullHealth()-b1.getStrength());
	}
}

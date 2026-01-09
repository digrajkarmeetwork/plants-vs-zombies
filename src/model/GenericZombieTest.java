package model;

import junit.framework.TestCase;

public class GenericZombieTest extends TestCase {
	
	private GenericZombie g1 = null;
	private SunFlower s1 = null;
	private VenusFlyTrap v1,v2;
	private Board board;
	private Level level;
	
	/**
	 * This method constructs the object to be tested.
	 */
	protected void setUp() {
		board = new Board();
		board.setupGrid();
		g1 = new GenericZombie();
		s1 = new SunFlower();
		v1 = new VenusFlyTrap();
		v2 = new VenusFlyTrap();
		level = new Level(1);
		board.setLevel(level);
	}
	
	/**
	 * This method test loseHealth() method.
	 */
	public void testLoseHealth() {
		int health = g1.getHealth();
		g1.loseHealth(20);	
		assertEquals(g1.getHealth(), health - 20);
		//assertEquals("Error in loseHealth Method",250);
	}
	
	/**
	 * This method test if the zombie dies.
	 */
	public void testLoseHealthDie() {
		board.placeZombie(g1, 2, 2);
		g1.loseHealth(1000);	
		boolean boardContainsZombie = true;
		board.boardTurn();
		if (!board.zombiesOnBoard.contains(g1))
			boardContainsZombie = false;
		assertEquals(boardContainsZombie, false);
		//assertEquals("Error in loseHealth Method",250);
	}
	
	/**
	 * This method test the attack() method.
	 */
	public void testAttack() {
		int plantHealth = v1.getHealth();
		g1.attack(v1);
		assertEquals(v1.getHealth(), plantHealth - g1.getStrength());
	}
	
	/**
	 * This method test the if the zombie moves on the grid.
	 */
	public void testGoAdvance() {
		board.placeZombie(g1, 2, 2);
		g1.go(board);
		assertEquals(board.getObject(2,1), g1);
	}
	
	/**
	 * This method test the if attack work with the go method.
	 */
	public void testGoAttack() {
		board.placeZombie(g1, 3, 3);
		board.placePlant(v2,3,2);
		g1.go(board);
		assertEquals(v2.getHealth(), VenusFlyTrap.FULL_HEALTH - GenericZombie.STRENGTH);
	}
}

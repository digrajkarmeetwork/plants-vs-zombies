package model;

import java.util.ArrayList;

import junit.framework.TestCase;

public class BoardTest extends TestCase {
	public SunFlower s1;
	public SunFlower s2;
	public VenusFlyTrap v1;
	public GenericZombie g1,g2;
	public Board board;
	public Level level;
	
	protected void setUp() {
		s1 = new SunFlower();
		s2 = new SunFlower();
		v1 = new VenusFlyTrap();
		g1 = new GenericZombie();
		g2 = new GenericZombie();
		board = new Board(new CommandManager());
		board.setupGrid();
		level = new Level(1);
		board.setLevel(level);

	}

	/**
	 * This test the spawnZombies() method.
	 */
//	public void testSpawnZombies() {
//		board.spawnZombies();
//		boolean zombieInG = false;
//		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
//			if (board.getObject(i,Board.GRID_WIDTH-1) instanceof GenericZombie)
//				zombieInG = true;
//		}
//		assertEquals("Testing Zombie Spwan",zombieInG, false);
//	}
	
	/**
	 * This test the spawnZombiesEmpty() method.
	 */
	public void testSpawnZombiesEmpty() {
		level.setAllZombies(new ArrayList<Zombie>());
		board.spawnZombies();
		boolean zombieInG = false;
		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
			if (board.getObject(i,Board.GRID_WIDTH-1) instanceof GenericZombie)
				zombieInG = true;
		}
		assertEquals("Testing Zombie Spwan",zombieInG, false);
	}
	
	/**
	 * This test the prepareNextTurn() method.
	 */
	public void testPrepareNextTurn() {
		board.setupGrid();
		board.placePlant(s1, 2, 2);
		board.placePlant(s2, 1, 1);
		board.placePlant(v1, 3, 3);
		level.coins = 0;
		
		board.prepareNextTurn();
		
		assertEquals(level.coins, SunFlower.COIN_BONUS * 2);		
	}
	
	/**
	 * This test the zombiesInFirstColumnTrue() method.
	 */
	public void testZombiesInFirstColumnTrue() {
		board.placeZombie(g1, 3, 0);
		assertEquals(board.zombiesInFirstColumn(), true);
	}
	
	/**
	 * This test the zombiesInFirstColumnFalse() method.
	 */
	public void testZombiesInFirstColumnFalse() {
		assertEquals(board.zombiesInFirstColumn(), false);
	}
	
	/**
	 * This test toTheLeftZombie() method.
	 */
	public void testToTheLeftZombie() {
		board.placeZombie(g1, 0, 0);
		board.placePlant(s1, 0, 1);
		assertEquals(board.toTheLeft(s1), g1);	
	}
	
	/**
	 * This test the toTheLeftNull() method.
	 */
	public void testToTheLeftNull() {
		board.placeZombie(g1, 0, 0);
		assertEquals(board.toTheLeft(g1), null);	
	}
	
	/**
	 * This test the toTheRightPlant() method.
	 */
	public void testToTheRightPlant() {
		board.placeZombie(g1, 0, 0);
		board.placePlant(s1, 0, 1);
		assertEquals(board.toTheRight(g1), s1);
	}
	
	/**
	 * This test the toTheRightNull() method.
	 */
	public void testToTheRightNull() {
		board.placeZombie(g1, 0, Board.GRID_WIDTH-1);
		assertEquals(board.toTheRight(g1), null);
	}
	
	/**
	 * This test the move() method.
	 */
	public void testMove() {
		board.placePlant(s1, 2, 2);
		board.move(s1, (NullSpace) board.getObject(2, 3));
		assertEquals(board.getObject(2, 3), s1);
	}
	
	/**
	 * This test the removePlant() method.
	 */
	public void testRemovePlant() {
		board.placePlant(s1, 2, 2);
		board.remove(s1);
		boolean isNullSpace = false;
		if (board.getObject(2,2) instanceof NullSpace)
			isNullSpace = true;
		assertEquals(isNullSpace, true);
	}
	
	/**
	 * This test the removeZombie() method.
	 */
	public void testRemoveZombie() {
		board.placeZombie(g1, 2, 2);
		board.remove(g1);
		boolean isNullSpace = false;
		if (board.getObject(2,2) instanceof NullSpace)
			isNullSpace = true;
		assertEquals(isNullSpace, true);
	}
	
	/**
	 * This test removing an object not on the board.
	 */
	public void testRemoveObjectNotOnBoard() {
		board.placeZombie(g1, 2, 2);
		assertEquals(false,board.remove(g2));
	}
	
	/**
	 * This tests the startBoardTurn() method.
	 */
	public void testStartBoardTurn() {
		board.startBoardTurn();
		assertEquals(board.commandManager.isUndoAvailable(), true);
	}
	
	/**
	 * This method test BoardTurnDeadPlant.
	 */
	public void testBoardTurnDeadPlant() {
		board.placePlant(s1, 0, 0);
		board.placeZombie(g1, 0, 1);
		board.boardTurn();
		assertEquals(true, board.getPlantsOnBoard().isEmpty());
	}
	
	/**
	 * This method test BoardTurnDeadZombie.
	 */
	public void testBoardTurnDeadZombie() {
		board.placePlant(v1, 0, 0);
		board.placeZombie(g1, 0, 1);
		board.placePlant(s1,3,3);
		g1.setHealth(1);
		board.boardTurn();		
		assertEquals(true, !board.getZombiesOnBoard().contains(g1));
	}
}

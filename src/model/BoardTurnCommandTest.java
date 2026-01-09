package model;

import junit.framework.TestCase;

public class BoardTurnCommandTest extends TestCase{
	private GenericZombie g1;
	private SunFlower s1;
	private VenusFlyTrap v1,v2;
	private Board board;
	private Level level;
	
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
	 * This method tests execute when a loss of health occurs
	 */
	public void testHealthLossExecute() {
		board.placeZombie(g1, 0, 5);
		board.placePlant(v1, 0, 4);
		g1.setStrength(10);
		v1.setStrength(10);
		g1.setHealth(100);
		v1.setHealth(100);
		new BoardTurnCommand(board, level).execute();
		assertEquals(90, g1.getHealth());
		assertEquals(90, v1.getHealth());
	}
	
	/**
	 * This method tests undo when a loss of health occured
	 */
	public void testHealthLossUndo() {
		board.placeZombie(g1, 0, 5);
		board.placePlant(v1, 0, 4);
		g1.setStrength(10);
		v1.setStrength(10);
		g1.setHealth(100);
		v1.setHealth(100);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		assertEquals(100, ((Zombie)board.getObject(0, 5)).getHealth());
		assertEquals(100, ((Plant)board.getObject(0, 4)).getHealth());
	}
	
	/**
	 * This method tests redo when a loss of health occured
	 */
	public void testHealthLossRedo() {
		board.placeZombie(g1, 0, 5);
		board.placePlant(v1, 0, 4);
		g1.setStrength(10);
		v1.setStrength(10);
		g1.setHealth(100);
		v1.setHealth(100);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		btc.redo();
		assertEquals(90, ((Zombie)board.getObject(0, 5)).getHealth());
		assertEquals(90, ((Plant)board.getObject(0, 4)).getHealth());
	}
	
	/**
	 * This method tests execute when a plants timer is to be decremented.
	 */
	public void testPlantTimerExecute() {
		board.placePlant(v1, 0, 4);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		assertEquals(4, ((Plant)board.getObject(0, 4)).getCurrentTime());
	}
	
	/**
	 * This method tests undo when a plants timer has been decremented.
	 */
	public void testPlantTimerUndo() {
		board.placePlant(v1, 0, 4);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		assertEquals(5, ((Plant)board.getObject(0, 4)).getCurrentTime());
	}
	
	/**
	 * This method tests redo when a plants timer has been decremented
	 */
	public void testPlantTimerRedo() {
		board.placePlant(v1, 0, 4);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		btc.redo();
		assertEquals(4, ((Plant)board.getObject(0, 4)).getCurrentTime());
	}
	
	/**
	 * This method tests execute when an object on the board will die.
	 */
	public void testObjectDeathExecute() {
		v1.setHealth(20);
		board.placePlant(v1, 0, 4);
		board.placeZombie(g1, 0, 5);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		assertEquals(true,(board.getObject(0, 4) instanceof NullSpace));
	}
	
	/**
	 * This method tests undo when an object has died.
	 */
	public void testObjectDeathUndo() {
		v1.setHealth(20);
		board.placePlant(v1, 0, 4);
		board.placeZombie(g1, 0, 5);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		assertEquals(true,(board.getObject(0, 4) instanceof VenusFlyTrap));
	}
	
	/**
	 * This method tests redo when an object has died.
	 */
	public void testObjectDeathRed() {
		v1.setHealth(20);
		board.placePlant(v1, 0, 4);
		board.placeZombie(g1, 0, 5);
		v1.setCurrentTime(5);
		BoardTurnCommand btc = new BoardTurnCommand(board, level);
		btc.execute();
		btc.undo();
		btc.redo();
		assertEquals(true,(board.getObject(0, 4) instanceof NullSpace));
	}
}

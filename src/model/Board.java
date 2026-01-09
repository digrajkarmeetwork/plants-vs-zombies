package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Board implements Serializable{
	public static final int GRID_HEIGHT = 6;
	public static final int GRID_WIDTH = 9;
	private State gridState;
	
	public enum State implements Serializable{
		POSITIONS, STATS, DISABLED;
	}
	
	public GridObject[][] grid;
	public ArrayList<GridObject> gridObjects = new ArrayList<GridObject>();
	public ArrayList<Zombie> zombiesOnBoard = new ArrayList<Zombie>();
	public ArrayList<Plant> plantsOnBoard = new ArrayList<Plant>();
	public CommandManager commandManager;
	
	private Level level;
	
	//Use this constructor for tests only
	public Board() {
	}
	
	public Board(CommandManager cm) {
		commandManager = cm;
	}

	/**
	 * This method sets up and prints the grid.
	 */
	public void setupGrid() {
		grid = new GridObject[GRID_HEIGHT][GRID_WIDTH];
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				grid[i][j] = new NullSpace();
			}
		}
	}
	
	/**
	 * This method spawns the zombies on the board.
	 */
	public void spawnZombies() {
		if (level.getAllZombies().isEmpty())
			return;
		
		int yPos = ThreadLocalRandom.current().nextInt(0, Board.GRID_HEIGHT-1);
		int randZombie = ThreadLocalRandom.current().nextInt(0, level.getAllZombies().size());
		Zombie zombie = level.getAllZombies().remove(randZombie); 

		if (isEmpty(yPos, Board.GRID_WIDTH - 1))
			placeZombie(zombie, yPos, Board.GRID_WIDTH - 1);
	}
	
	/**
	 * This method starts the board's turn in the command manager
	 */
	public void startBoardTurn() {
		commandManager.executeCommand(new BoardTurnCommand(this, level));
	}
	
	/**
	 * This method executes the board's turn
	 */
	public void boardTurn() {
		//All plants then all zombies on the board - Advance or attack
		if (!zombiesOnBoard.isEmpty()) {
			for (Plant plant : plantsOnBoard)
				plant.go(this);
			
			for (Zombie zombie : zombiesOnBoard)			
				zombie.go(this);
		}
		removeTheDead();
		//Spawn
		spawnZombies();

		//Give player coins reduce count down on plant timers
		prepareNextTurn();
	}
	
	/**
	 * This method prepares for the upcomming turn
	 */
	public void prepareNextTurn() {
		for (Plant plant: plantsOnBoard) {
			if (plant instanceof SunFlower)
				level.coins = level.coins + SunFlower.COIN_BONUS;
		}
		for (Plant plant: level.allPlants) {
			plant.newTurn();
		}
	}

	/**
	 * This method checks if there is any zombies in the first column.
     * @return A boolean, true is there is any zombies in the first column otherwise false.
	 */
	public boolean zombiesInFirstColumn() {
		for (int i = 0; i < GRID_HEIGHT; i++) {
			if (getObject(i, 0) instanceof Zombie) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method places a plant on the grid.
	 * @param plant (Plant), a plant.
	 * @param posY  (int), the y-coordinate of the grid.
	 * @param posX  (int), the x-coordinate of the grid.
	 */
	public void placePlant(Plant plant, int posX, int posY) {
		grid[posX][posY] = plant;
		plantsOnBoard.add(plant);
		gridObjects.add(plant);
		level.coins -= plant.getPrice();
		plant.setCurrentTime(plant.getFullTime());
	}

	/**
	 * This method places a zombie on the grid.
	 * @param zombie (Zombie), a zombie.
	 * @param posY   (int), the y-coordinate of the grid.
	 * @param posX   (int), the x-coordinate of the grid.
	 */
	public void placeZombie(Zombie zombie, int posX, int posY) {
		grid[posX][posY] = zombie;
		zombiesOnBoard.add(zombie);
		gridObjects.add(zombie);
	}

	/**
	 * This method gets the object to the left.
	 * @param zombie (Zombie), a zombie.
	 * @return gridOject (GridObject), an object on the grid.
	 */
	public GridObject toTheLeft(GridObject zombie) { // mostly just called by zombies but no need to specify
		int j = getX(zombie);
		int i = getY(zombie);
		if ((i != -1 || j != -1) && j!=0)
			return (grid[i][j - 1]);
		return null;
	}

	/**
	 * This method gets the object to the right.
	 * @param plant (Plant), a plant.
	 * @return gridObject (GridObject), an object on the grid.
	 */
	public GridObject toTheRight(GridObject plant) {
		int j = getX(plant);
		int i = getY(plant);
		if ((i != -1 || j != -1) && j!= Board.GRID_WIDTH-1) {
			return grid[i][j + 1];
		}
		return null;
	}

	/**
	 * This method moves a grid object on the board.
	 * @param gridObject (GridObject), the object on the grid.
	 * @param nullSpace  (NullSpace), empty space.
	 */
	public void move(GridObject gridObject, NullSpace nullSpace) {
		int j = getX(gridObject);
		int i = getY(gridObject);
		int jnext = getX(nullSpace);
		int inext = getY(nullSpace);
		grid[inext][jnext] = gridObject;
		grid[i][j] = nullSpace;
	}

	/**
	 * This method moves a grid object on the board. 
	 * @param gridObject (GridObject), the object on the grid.
	 * @param nullSpace  (NullSpace), empty space.
	 */
	public boolean remove(GridObject gridObject) {
		int j = getX(gridObject);
		int i = getY(gridObject);
		if (i!=-1 && j!=-1) {
			grid[i][j] = new NullSpace();
			
			gridObjects.remove(gridObject);
			if (gridObject instanceof Zombie )
				zombiesOnBoard.remove(gridObject);
			if (gridObject instanceof Plant)
				plantsOnBoard.remove(gridObject);
			
			if(grid[i][j] instanceof NullSpace) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method gets the x-coordinate of the gridObject. 
	 * @param gridObject (Grid Object), the object on the grid.
	 * @return A int, the x-coordinate of the gridObject.
	 */
	public int getX(GridObject gridObject) {
		int i, j = 0;
		for (i = 0; i < GRID_HEIGHT; i++) {
			for (j = 0; j < GRID_WIDTH; j++) {
				if (grid[i][j].equals(gridObject))
					return j;
			}
		}
		return -1;
	}

	/**
	 * This method gets the y-coordinate of the gridObject.
	 * @param gridObject (GridObject), the object on the grid.
	 * @return A int, the y-coordinate of the gridObject.
	 */
	public int getY(GridObject gridObject) {
		int i, j = 0;
		for (i = 0; i < GRID_HEIGHT; i++) {
			for (j = 0; j < GRID_WIDTH; j++) {
				if (grid[i][j].equals(gridObject))
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * This method sweeps the board for dead grid objects after any turn.
	 * Removes dead plants or zombies from the board.
	 */
	private void removeTheDead() {
		Iterator<Plant> iteratorP = plantsOnBoard.iterator();
		Iterator<Zombie> iteratorZ = zombiesOnBoard.iterator();
		while(iteratorP.hasNext())
		{ 
			Plant p = iteratorP.next();
			if (p.getHealth() <= 0) {
				iteratorP.remove();
				remove(p);
			}
		}
		while(iteratorZ.hasNext())
		{
			Zombie z = iteratorZ.next();
			
			if (z.getHealth() <= 0) {
				iteratorZ.remove();
				remove(z);
			}
		}
	} 	
	
	public boolean noSunflowers() {
		for (Plant plant: plantsOnBoard) {
			if (plant instanceof SunFlower)
				return false;
		}
		return true;
	}
	
	public void clear() {
		plantsOnBoard = new ArrayList<Plant>();
		zombiesOnBoard = new ArrayList<Zombie>();
		gridObjects = new ArrayList<GridObject>();
	}


	/**
	 * This method checks if a location from the grid is empty.
	 * @param posY (int), the y-coordinate on the grid.
	 * @param posX (int), the x-coordinate on the grid.
	 * @return A boolean, true if the position is empty otherwise false.
	 */
	public boolean isEmpty(int posY, int posX) {
		return (getObject(posY, posX) instanceof NullSpace);
	}

	/**
	 * This method gets the object that is stored in the grid.
	 * @param i (int), this is the x coordinate of the grid.
	 * @param j (int), this is the y coordinate of the grid.
	 * @return A GridObject, the item on the grid.
	 */
	public GridObject getObject(int i, int j) {
		return grid[i][j];
	}
	 /**
	  * This method gets the gridObject array
	  * @return gridObjects
	  */
	public ArrayList<GridObject> getGridObjects() {
		return gridObjects;
	}

	/**
	 * This method sets the gridObject array
	 * @param gridObjects
	 */
	public void setGridObjects(ArrayList<GridObject> gridObjects) {
		this.gridObjects = gridObjects;
	}

	/**
	 * This method gets the zombiesOnBoard
	 * @return zombiesOnBoard
	 */
	public ArrayList<Zombie> getZombiesOnBoard() {
		return zombiesOnBoard;
	}

	/**
	 * This method sets the zombiesOnBoard
	 * @param zombiesOnBoard
	 */
	public void setZombiesOnBoard(ArrayList<Zombie> zombiesOnBoard) {
		this.zombiesOnBoard = zombiesOnBoard;
	}

	/**
	 * This method gets the plantsOnBoard
	 * @return plantsOnBoard
	 */
	public ArrayList<Plant> getPlantsOnBoard() {
		return plantsOnBoard;
	}

	/**
	 * This method sets the plantsOnBoard
	 * @param plantsOnBoard
	 */
	public void setPlantsOnBoard(ArrayList<Plant> plantsOnBoard) {
		this.plantsOnBoard = plantsOnBoard;
	}
	
	public void setLevel(Level lvl) {
		this.level = lvl;
	}
	
	public Level getLevel() {
		return level;
	}

	public State getGridState() {
		return gridState;
	}

	public void setGridState(State gridState) {
		this.gridState = gridState;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	
	//Used for debugging
	/**
	 * This method prints the grid. Used for debugging
	 * @param objects
	 */
//	public void printGrid(GridObject[][] objects) {
//		for (int i = 0; i < GRID_HEIGHT; i++) {
//			for (int j = 0; j < GRID_WIDTH; j++) {
//				if (objects[i][j] instanceof GenericZombie)
//					System.out.print("[ g ]");
//				else if (objects[i][j] instanceof SunFlower)
//					System.out.print("[ S ]");
//				else if (objects[i][j] instanceof VenusFlyTrap)
//					System.out.print("[ V ]");
//				else if(objects[i][j] instanceof PeaShooter)
//					System.out.print("[ PS ]");
//				else if(objects[i][j] instanceof Potatoe)
//					System.out.print("[ P ]");
//				else if(objects[i][j] instanceof Walnut)
//					System.out.print("[ W ]");
//				else
//					System.out.print("[   ]");
//			}
//			System.out.print("\n");
//		}	
//	}

}
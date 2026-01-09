package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class BoardTurnCommand implements Command, Serializable{
	private Board board;
	private Level level;
    //objects on board plants on board zombies on board.
    //levelPlants levelZombies
	private int					previousCoins;
    private GridObject[][]		previousGridState;
    private ArrayList<GridObject> previousGridObjects;
    private ArrayList<Plant>	previousPlantsOnBoard;
    private ArrayList<Zombie> 	previousZombiesOnBoard;
    private ArrayList<Zombie>	previousLevelZombies;
    private ArrayList<Plant> 	previousPlantsWillDecrement;
    
    private int					nextCoins;
    private GridObject[][] 		nextGridState;
    private ArrayList<GridObject> nextGridObjects;
    private ArrayList<Plant>	nextPlantsOnBoard;
    private ArrayList<Zombie> 	nextZombiesOnBoard;
    private ArrayList<Zombie>	nextLevelZombies;
    
    /**
     * This constructor saves the values on the board before and after 
     * the board's turn.
     * @param board
     */
    public BoardTurnCommand(Board board, Level level) {
    	this.board = board;
    	this.level = level;
    	previousGridObjects = new ArrayList<GridObject>();
    	previousPlantsOnBoard = new ArrayList<Plant>();
    	previousZombiesOnBoard = new ArrayList<Zombie>();
    	previousLevelZombies = new ArrayList<Zombie>();
    	previousPlantsWillDecrement = new ArrayList<Plant>();
    	nextGridObjects = new ArrayList<GridObject>();
    	nextPlantsOnBoard = new ArrayList<Plant>();
    	nextZombiesOnBoard = new ArrayList<Zombie>();
    	nextLevelZombies = new ArrayList<Zombie>();
    	
    	previousCoins = level.coins;
    	
    	for (Zombie zombie: level.getAllZombies())
    		previousLevelZombies.add(zombie);
    	
        previousGridState = new GridObject[Board.GRID_HEIGHT][Board.GRID_WIDTH];
        nextGridState = new GridObject[Board.GRID_HEIGHT][Board.GRID_WIDTH];
        
        for (int i = 0; i < Board.GRID_HEIGHT; i++) {
        	for (int j = 0; j < Board.GRID_WIDTH; j++) {
        		GridObject o = board.grid[i][j];
        		if (o instanceof Plant) {
        			Plant clonePlant = (Plant)GridObjectFactory.createNewGridObject(o.getObjectTitle());
        			clonePlant.setHealth(((Plant)o).getHealth());
        			previousPlantsOnBoard.add(clonePlant);
        			previousGridObjects.add(clonePlant);
        			previousGridState[i][j] = clonePlant;
        			if (((Plant)o).getCurrentTime() != 0)
        				previousPlantsWillDecrement.add((Plant)o);
        		}
        		else if (o instanceof Zombie) {
        			Zombie cloneZombie = (Zombie)GridObjectFactory.createNewGridObject(o.getObjectTitle());
        			cloneZombie.setHealth(((Zombie)o).getHealth());
        			previousZombiesOnBoard.add(cloneZombie);
        			previousGridObjects.add(cloneZombie);
        			previousGridState[i][j] = cloneZombie;
        		}
        		else
        			previousGridState[i][j] = new NullSpace();
        	}
        }
     
        board.boardTurn();
           
        nextCoins = level.coins;
        
        for (GridObject o: board.gridObjects) {
    		if (o instanceof Plant) {
    			nextPlantsOnBoard.add((Plant) o);
    			nextGridObjects.add(o);
    		}
    		if (o instanceof Zombie) {
    			nextZombiesOnBoard.add((Zombie)o);
    			nextGridObjects.add(o);
    		}
    	}
        
        for (Zombie zombie: level.getAllZombies())
    		nextLevelZombies.add(zombie);
    	
        for (int i = 0; i < Board.GRID_HEIGHT; i++) {
        	for (int j = 0; j < Board.GRID_WIDTH; j++) {
        		GridObject o = board.grid[i][j];
        		nextGridState[i][j] = o;
        	}
        }
    }
   
    /**
     * Execute occurs in contstructor however must still implement method
     * from Command interface.
     */
    public void execute() {
    }
    	
    /**
     * This method undoes an entire board's turn. Brings player back to having pressed 
     * "End Turn".
     */
    public void undo() {
    	for (int i = 0; i < Board.GRID_HEIGHT; i++) {
    		for (int j = 0; j < Board.GRID_WIDTH; j++) {
    			board.grid[i][j] = previousGridState[i][j];
    		}
    	}
    	
    	for (Plant plant: level.getAllPlants()) {
    		for (Plant decPlant: previousPlantsWillDecrement) {
    			if (plant.getClass() == decPlant.getClass()) {
    				plant.setCurrentTime(plant.getCurrentTime()+1);
    				break;
    			}
    		}
    	}
    	
    	level.coins = previousCoins;
    	level.setAllZombies(previousLevelZombies);
    	board.gridObjects = previousGridObjects;
    	board.plantsOnBoard = previousPlantsOnBoard;
    	board.zombiesOnBoard = previousZombiesOnBoard;	
    }
    	

    /**
     * This method performs a redo on the last undo.
     */
	public void redo() {
    	for (int i = 0; i < Board.GRID_HEIGHT; i++) {
    		for (int j = 0; j < Board.GRID_WIDTH; j++) {
    			board.grid[i][j] = nextGridState[i][j];
    		}
    	}
    	for (Plant plant: level.allPlants) {
    		plant.newTurn();
    	}
    	level.coins = nextCoins;
    	level.setAllZombies(nextLevelZombies);
    	board.gridObjects = nextGridObjects;
    	board.plantsOnBoard = nextPlantsOnBoard;
    	board.zombiesOnBoard = nextZombiesOnBoard;
    }
}

package controller;

import model.Level;
import model.NullSpace;

import model.PlacePlantCommand;

import model.PeaShooter;
import model.Plant;
import model.Potatoe;
import model.SunFlower;
import model.VenusFlyTrap;
import model.Walnut;
import model.Zombie;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import model.Board;
import model.Board.State;
import model.BurrowingBailey;
import model.CommandManager;
import model.FrankTheTank;
import model.GenericZombie;
import model.GridObject;
import model.GridObjectFactory;
import view.View;

public class Controller {
	static Scanner reader = new Scanner(System.in);
	private Board board;
	private View view;
	private CommandManager commandManager;
	private Level level;
	private boolean isStartOfLevel;

	/**
	 * The constructor, constructs the controller.
	 * 
	 * @param board
	 * @param view
	 * @param cm
	 */
	public Controller(Board board, View view, CommandManager cm) {
		this.view = view;
		this.board = board;
		commandManager = cm;
		startGame(1);
	}

	/**
	 * This method starts the game for Plant Versus Zombies.
	 */
	public void startGame(int levelNo) {
		// Initialize the level and grid
		level = new Level(levelNo);
		board.setLevel(level);
		board.setupGrid();
		board.clear();
	}

	/**
	 * This method initialize GUI's Action Listeners
	 */
	public void initController() {
		// Initialize action listener for the help tab to generate information panel
		view.getHelp().addActionListener(e -> spawnInfoFrame());
		view.getImportOption().addActionListener(e -> importFromFile());

		view.getExportOption().addActionListener(e -> exportToFile());
		// Initialize action listener for all plant buttons
		view.getPlants().addListSelectionListener(e -> plantSelected(e));

		view.level1.addActionListener(e -> initLevel(1));
		view.level2.addActionListener(e -> initLevel(2));
		view.level3.addActionListener(e -> initLevel(3));

		view.editLevel.addActionListener(e -> editLevel());
		view.getConfirm().addActionListener(e -> confirmLevelChoices());

		// Initialize action listener for all of the grid buttons
		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
			for (int j = 0; j < Board.GRID_WIDTH; j++)
				view.getButtons()[i][j].addActionListener(e -> gridPositionSelected(e));
		}
		// Initialize action listener for the end turn button
		view.getEndTurn().addActionListener(e -> endTurn());

		// Initialize and define action listener for the undo button
		view.getUndoTurn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.commandManager.undo();
				gridCond(State.STATS);
			}
		});
		// Initialize and define action listener for the redo button
		view.getRedoTurn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.commandManager.redo();
				gridCond(State.STATS);
			}
		});
	}

	private void confirmLevelChoices() { 
		int numGenericZombie = 0;
		int numFrankTheTank = 0;
		int numBurrowingBailey = 0;
		
		try {
			numGenericZombie = Integer.parseInt(view.getGenericZombieCB().getText());
			numFrankTheTank = Integer.parseInt(view.getFrankTheTankCB().getText());
			numBurrowingBailey = Integer.parseInt(view.getBurrowingBaileyCB().getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null,"You may only enter numbers in the fields");
			return;
		}
		
		if (numGenericZombie == 0 && numFrankTheTank == 0 && numBurrowingBailey == 0) {
			JOptionPane.showMessageDialog(null,"You must select at least 1 zombie");
			return;
		}
		
		if (numGenericZombie < 0 || numFrankTheTank < 0 || numBurrowingBailey < 0) {
			JOptionPane.showMessageDialog(null,"You cannot select negative zombies");
			return;
		}
		
		ArrayList<Zombie> userZombie = new ArrayList<Zombie>();
		
		for (int i = 0; i < numGenericZombie; i++)
			userZombie.add(new GenericZombie());
		for (int i = 0; i < numFrankTheTank; i++)
			userZombie.add(new FrankTheTank());
		for (int i = 0; i < numBurrowingBailey; i++)
			userZombie.add(new BurrowingBailey());
		
		level = new Level(userZombie);
		board.setLevel(level);
		board.clear();
		board.setupGrid();
		endTurn();
		JOptionPane.showMessageDialog(null, "Confirmed Choices!");
		view.getLevelEditorFrame().dispose();
	}

	private void editLevel() {
		view.makeLevelEditor();
	}

	private void initLevel(int levelNo) {
		board.setLevel(new Level(levelNo));
	}

	// Action listener for plant buttons
	private void plantSelected(ListSelectionEvent arg0) {
		// If statements deals with multiple events fired by one click
		if (arg0.getValueIsAdjusting() || view.getPlants().getSelectedValue() == null)
			return;
		// Extract the name of the plant from the list item selected
		String plantName = ((JLabel) view.getPlants().getSelectedValue().getComponent(0)).getText();
		// For all of the plants available in current level
		for (Plant plant : level.allPlants) {
			// Continue for loop until we find an instance of plant selected
			if (!plantName.equals(plant.getObjectTitle()))
				continue;
			else {
				// If the plant is not affordable notify the user, disable the grid, and clear
				// the selection
				if (!plant.isAvailable()) {
					JOptionPane.showMessageDialog(null,
							"This plant is available in " + plant.getCurrentTime() + " turn(s)");
					gridCond(State.STATS);
					view.getPlants().clearSelection();
					return;
				}
				// If the plant is not affordable notify the user, disable the grid, and clear
				// the selection
				if (plant.getPrice() > level.coins) {
					JOptionPane.showMessageDialog(null, "You cannot afford this plant");
					gridCond(State.STATS);
					view.getPlants().clearSelection();
					return;
				}
				// If this statement is reached the user has chosen a valid plant. Enable the
				// grid so it can be placed
				gridCond(State.POSITIONS);
				return;
			}
		}
	}

	/**
	 * This method corresponds to the players selected position on the grid.
	 * 
	 * @param e
	 */
	private void gridPositionSelected(ActionEvent e) {
		// Action command corresponds to i j
		String s = e.getActionCommand();
		String[] rowcol = s.split(" ");

		int i = Integer.parseInt(rowcol[0]);
		int j = Integer.parseInt(rowcol[1]);

		// If the grid is in the STATS state the player has selected an
		// area on the board to view an objects stats
		if (board.getGridState() == State.STATS) {
			// Get the grid object and display its stats
			GridObject selected = board.getObject(i, j);
			view.displayStats(selected);
			return;
		}

		String plantSelected = ((JLabel) view.getPlants().getSelectedValue().getComponent(0)).getText();

		// Immediately disable the grid once player has selected where to place their
		// plant
		gridCond(State.DISABLED);
		// Disable the flower buttons, the player must wait until the board turn has
		// ended
		plantButtonsEnabled(false);
		// Clear the plant list selection so once enabled the user can select a new
		// plant
		view.getPlants().clearSelection();
		// Add the plant to the board
		commandManager.executeCommand(
				new PlacePlantCommand(board, level, (Plant) GridObjectFactory.createNewGridObject(plantSelected),
						Integer.parseInt(rowcol[0]), Integer.parseInt(rowcol[1])));
		// Display coins
		view.getCoins().setText("       Sun Points: " + level.coins);
		// Allow player to check current stats of any object
		gridCond(State.STATS);
		// Enable the flower buttons in case player would like to plant enother plant
		plantButtonsEnabled(true);
	}

	/**
	 * This method ends the turn.
	 */
	private void endTurn() {
		isStartOfLevel = false;
		// Plants and zombies attack then zombies spawn
		board.startBoardTurn();
		// Update the coins on the GUI
		view.getCoins().setText("       Sun Points: " + level.coins);
		// Update the grid
		gridCond(State.DISABLED);
		// Check if a win or loss has occured
		playerWinLose();
		// If no plant is affordable the player is gifted coins.
		if (!level.plantAffordable() && board.noSunflowers()) {
			JOptionPane.showMessageDialog(view, "Wow you just found " + (50 - level.coins) + " Sun Points...");
			level.coins = 50;
		}

		// for (int i = 0; i < Board.GRID_HEIGHT; i++) {
		// for (int j = 0; j < Board.GRID_WIDTH; j++) {
		// view.playAnimation(view.getButtons()[i][j], board.grid[i][j]);
		// }
		// }

		// Board turn has ended, allow the player to pick another plant
		plantButtonsEnabled(true);
		gridCond(State.STATS);
	}

	/**
	 * This method checks to see if the player has won or lost
	 */
	private void playerWinLose() {
		// If Player Wins the Level because there are no zombies to be spawned an no
		// zombies on the board
		if (level.zombiesEmpty() && board.zombiesOnBoard.isEmpty()) {
			// Spawn a dialog to inform user
			JOptionPane.showMessageDialog(null, "!!!!!!!YOU WON!!!!!!!!");
			if (level.nextLevelExists() && !level.isCustomLevel()) {
				startGame(level.getLevelNo() + 1);
				JOptionPane.showMessageDialog(null, "Starting Level " + (level.getLevelNo()));
				return;
			}
			// Dispose of the GUI. Game has ended
			view.dispose();
			System.exit(0);
			return;
		}
		// If Player Loses the Level because zombies have reached the first column
		if (board.zombiesInFirstColumn()) {
			// Spawn a dialog to inform user
			JOptionPane.showMessageDialog(null, "You Lost....");
			// Dispose of the GUI. Game has ended
			view.dispose();
			System.exit(0);
			return;
		}
		return;
	}

	/**
	 * This is the action listener for clicking on an object on the grid to view its
	 * stats. Spawns a dialog displaying stats.
	 */
	private void spawnInfoFrame() {
		view.makeInfoFrame();
	}

	/**
	 * This method refreshes the board and sets the unoccupied buttons to enabled or
	 * disabled according to the parameter passed.
	 * 
	 * @param state
	 */
	private void gridCond(State state) {
		board.setGridState(state);
		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
			for (int j = 0; j < Board.GRID_WIDTH; j++) {
				JButton button = view.getButtons()[i][j];
				// Update the btton at the specified location
				view.updateButton(view.getButtons()[i][j], board.grid[i][j]);

				switch (state) {
				case STATS:
					if (!board.isEmpty(i, j)) {
						button.setEnabled(true);
					} else
						button.setEnabled(false);
					break;
				case POSITIONS:
					if (!board.isEmpty(i, j) || j == Board.GRID_WIDTH - 1)
						button.setEnabled(false);
					else {
						button.setEnabled(true);
						button.setContentAreaFilled(true);
					}
					break;
				case DISABLED:
					button.setEnabled(false);
					button.setContentAreaFilled(false);
					;
					break;
				}
			}
		}
		view.getCoins().setText("       Sun Points: " + level.coins);

		view.getUndoTurn().setEnabled(board.commandManager.isUndoAvailable());
		view.getRedoTurn().setEnabled(board.commandManager.isRedoAvailable());

		// Refresh the GUI
		view.revalidate();
		view.repaint();
	}

	private void importFromFile() {
		String PVZDirectory = System.getenv("APPDATA") + "/PlantsVsZombies/";
		File file = new File(PVZDirectory);

		if (!file.exists() || file.list().length == 0) {
			JOptionPane.showMessageDialog(view, "You have no saved games");
			return;
		}

		String[] fileNameArray = file.list();

		for (int i = 0; i < fileNameArray.length; i++) {
			fileNameArray[i] = fileNameArray[i].substring(0, fileNameArray[i].length() - 4);
		}

		int selection = JOptionPane.showOptionDialog(view, "Please Select A Save", "", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, fileNameArray, fileNameArray[0]);
		if (selection == -1)
			return;

		Board boardIn = null;
		try {
			FileInputStream fileIn = new FileInputStream(PVZDirectory + file.list()[selection]);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			boardIn = (Board) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Board class not found");
			c.printStackTrace();
			return;
		}
		board = boardIn;
		level = boardIn.getLevel();
		commandManager = boardIn.getCommandManager();
		view.getPlants().clearSelection();
		gridCond(boardIn.getGridState());
	}

	private void exportToFile() {
		String PVZDirectory = System.getenv("APPDATA") + "/PlantsVsZombies/";
		String exportFile = JOptionPane.showInputDialog("Please enter a name for your save");

		boolean specialCharacter = true;
		while (specialCharacter) {
			if (exportFile == null)
				return;
			Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(exportFile);
			specialCharacter = m.find();
			if (specialCharacter)
				exportFile = JOptionPane.showInputDialog("No special characters...nice try");
		}

		new File(PVZDirectory).mkdirs();

		try {
			FileOutputStream fileOut = new FileOutputStream(PVZDirectory + exportFile + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(board);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	/**
	 * This enables or disables all of the plant's buttons.
	 * 
	 * @param enabled
	 */
	private void plantButtonsEnabled(boolean enabled) {
		view.getPlants().setEnabled(enabled);
	}
}
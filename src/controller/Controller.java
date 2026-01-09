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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;

import model.Board;
import model.Board.State;
import model.BurrowingBailey;
import model.CommandManager;
import model.FrankTheTank;
import model.GenericZombie;
import model.GridObject;
import model.GridObjectFactory;
import util.SoundManager;
import view.AnimationManager;
import view.GameOverDialog;
import view.GridCellButton;
import view.PlantCardPanel;
import view.StartScreen;
import view.View;

public class Controller {
	static Scanner reader = new Scanner(System.in);
	private Board board;
	private View view;
	private CommandManager commandManager;
	private Level level;
	private boolean isStartOfLevel;
	private AnimationManager animationManager;
	private boolean gameStarted = false;

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
		animationManager = new AnimationManager();

		// Don't start game immediately - show start screen first
		setupStartScreen();
	}

	/**
	 * Setup the start screen with listeners.
	 */
	private void setupStartScreen() {
		view.setStartScreenListener(new StartScreen.StartScreenListener() {
			@Override
			public void onPlayGame() {
				startGameFromMenu(1);
			}

			@Override
			public void onSelectLevel(int level) {
				startGameFromMenu(level);
			}

			@Override
			public void onLevelEditor() {
				view.showGameScreen();
				editLevel();
			}
		});

		// Show start screen
		view.showStartScreen();
	}

	/**
	 * Start game from the start menu.
	 */
	private void startGameFromMenu(int levelNo) {
		SoundManager.play(SoundManager.BUTTON_CLICK);
		view.showGameScreen();
		startGame(levelNo);
		gameStarted = true;

		// Initialize plant cards with level data
		initPlantCards();

		// Register entities for animation
		registerAllAnimations();

		// Update UI
		gridCond(State.STATS);
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
		isStartOfLevel = true;
	}

	/**
	 * Initialize plant cards in the view.
	 */
	private void initPlantCards() {
		if (level == null || level.allPlants == null) return;

		Plant[] plants = level.allPlants.toArray(new Plant[0]);
		view.initPlantCards(plants);

		// Set up plant card listeners
		PlantCardPanel[] cards = view.getPlantCards();
		if (cards != null) {
			for (int i = 0; i < cards.length; i++) {
				final int index = i;
				cards[i].setListener(card -> {
					plantCardSelected(index);
				});
			}
		}

		// Update card states
		updatePlantCardStates();
	}

	/**
	 * Update plant card visual states based on coins and cooldowns.
	 */
	private void updatePlantCardStates() {
		if (level == null) return;
		view.updatePlantCards(level.coins);
	}

	/**
	 * Register all grid entities for animation.
	 */
	private void registerAllAnimations() {
		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
			for (int j = 0; j < Board.GRID_WIDTH; j++) {
				GridObject obj = board.getObject(i, j);
				if (obj != null && !(obj instanceof NullSpace)) {
					JButton button = view.getButtons()[i][j];
					if (button instanceof GridCellButton) {
						animationManager.registerEntity((GridCellButton) button, obj);
					}
				}
			}
		}
	}

	/**
	 * This method initialize GUI's Action Listeners
	 */
	public void initController() {
		// Initialize action listener for the help tab to generate information panel
		view.getHelp().addActionListener(e -> spawnInfoFrame());
		view.getImportOption().addActionListener(e -> importFromFile());
		view.getExportOption().addActionListener(e -> exportToFile());

		// Initialize action listener for all plant buttons (legacy JList)
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
				SoundManager.play(SoundManager.BUTTON_CLICK);
				board.commandManager.undo();
				gridCond(State.STATS);
				registerAllAnimations();
			}
		});

		// Initialize and define action listener for the redo button
		view.getRedoTurn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SoundManager.play(SoundManager.BUTTON_CLICK);
				board.commandManager.redo();
				gridCond(State.STATS);
				registerAllAnimations();
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

		// Initialize plant cards for custom level
		initPlantCards();

		endTurn();
		SoundManager.play(SoundManager.BUTTON_CLICK);
		JOptionPane.showMessageDialog(null, "Confirmed Choices!");
		view.getLevelEditorFrame().dispose();
	}

	private void editLevel() {
		view.makeLevelEditor();
	}

	private void initLevel(int levelNo) {
		board.setLevel(new Level(levelNo));
	}

	/**
	 * Handle plant card selection (new UI).
	 */
	private void plantCardSelected(int index) {
		if (level == null || level.allPlants == null) return;
		if (index < 0 || index >= level.allPlants.size()) return;

		Plant plant = level.allPlants.get(index);

		// Check availability
		if (!plant.isAvailable()) {
			JOptionPane.showMessageDialog(null,
					"This plant is available in " + plant.getCurrentTime() + " turn(s)");
			gridCond(State.STATS);
			view.clearPlantSelection();
			return;
		}

		// Check affordability
		if (plant.getPrice() > level.coins) {
			SoundManager.play(SoundManager.BUTTON_CLICK);
			JOptionPane.showMessageDialog(null, "You cannot afford this plant");
			gridCond(State.STATS);
			view.clearPlantSelection();
			return;
		}

		// Valid selection - enable grid for placement
		SoundManager.play(SoundManager.BUTTON_CLICK);
		view.selectPlantCard(index);
		gridCond(State.POSITIONS);
	}

	// Action listener for plant buttons (legacy JList)
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
				SoundManager.play(SoundManager.BUTTON_CLICK);
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

		// Get selected plant name
		String plantSelected = null;

		// Try new plant cards first
		int selectedCardIndex = view.getSelectedPlantIndex();
		if (selectedCardIndex >= 0 && selectedCardIndex < level.allPlants.size()) {
			plantSelected = level.allPlants.get(selectedCardIndex).getObjectTitle();
		} else if (view.getPlants().getSelectedValue() != null) {
			// Fall back to legacy JList
			plantSelected = ((JLabel) view.getPlants().getSelectedValue().getComponent(0)).getText();
		}

		if (plantSelected == null) {
			gridCond(State.STATS);
			return;
		}

		// Immediately disable the grid once player has selected where to place their
		// plant
		gridCond(State.DISABLED);
		// Disable the flower buttons, the player must wait until the board turn has
		// ended
		plantButtonsEnabled(false);
		// Clear the plant list selection so once enabled the user can select a new
		// plant
		view.getPlants().clearSelection();
		view.clearPlantSelection();

		// Play plant placement sound
		SoundManager.play(SoundManager.PLANT_PLACE);

		// Add the plant to the board
		Plant newPlant = (Plant) GridObjectFactory.createNewGridObject(plantSelected);
		commandManager.executeCommand(
				new PlacePlantCommand(board, level, newPlant, i, j));

		// Flash the cell to indicate placement
		JButton button = view.getButtons()[i][j];
		if (button instanceof GridCellButton) {
			((GridCellButton) button).flashPlantPlaced();
			// Register the new plant for animation
			animationManager.registerEntity((GridCellButton) button, newPlant);
		}

		// Display coins
		view.getCoins().setText("       Sun Points: " + level.coins);
		view.updateSunPoints(level.coins);

		// Update plant card states
		updatePlantCardStates();

		// Allow player to check current stats of any object
		gridCond(State.STATS);
		// Enable the flower buttons in case player would like to plant another plant
		plantButtonsEnabled(true);
	}

	/**
	 * This method ends the turn.
	 */
	private void endTurn() {
		isStartOfLevel = false;

		// Check if zombies will spawn this turn
		boolean zombiesWillSpawn = !level.zombiesEmpty();

		// Plants and zombies attack then zombies spawn
		board.startBoardTurn();

		// Play combat sounds if there are zombies on board
		if (!board.zombiesOnBoard.isEmpty()) {
			SoundManager.play(SoundManager.CHOMP);
		}

		// Play zombie spawn sound if new zombies appeared
		if (zombiesWillSpawn) {
			// Delay zombie sound slightly for effect
			Timer zombieTimer = new Timer(300, e -> {
				SoundManager.play(SoundManager.ZOMBIE_GROAN);
				((Timer) e.getSource()).stop();
			});
			zombieTimer.setRepeats(false);
			zombieTimer.start();
		}

		// Play sun collection sound (sunflowers generate sun)
		boolean hasSunflowers = false;
		for (int row = 0; row < Board.GRID_HEIGHT; row++) {
			for (int col = 0; col < Board.GRID_WIDTH; col++) {
				if (board.getObject(row, col) instanceof SunFlower) {
					hasSunflowers = true;
					break;
				}
			}
			if (hasSunflowers) break;
		}
		if (hasSunflowers) {
			SoundManager.play(SoundManager.SUN_COLLECT);
		}

		// Update the coins on the GUI
		view.getCoins().setText("       Sun Points: " + level.coins);
		view.updateSunPoints(level.coins);

		// Update plant card states
		updatePlantCardStates();

		// Update the grid
		gridCond(State.DISABLED);

		// Re-register animations for any new entities
		registerAllAnimations();

		// Flash cells with zombies
		flashZombieCells();

		// Check if a win or loss has occurred
		playerWinLose();

		// If no plant is affordable the player is gifted coins.
		if (!level.plantAffordable() && board.noSunflowers()) {
			JOptionPane.showMessageDialog(view, "Wow you just found " + (50 - level.coins) + " Sun Points...");
			level.coins = 50;
			view.updateSunPoints(level.coins);
			updatePlantCardStates();
		}

		// Board turn has ended, allow the player to pick another plant
		plantButtonsEnabled(true);
		gridCond(State.STATS);
	}

	/**
	 * Flash cells containing zombies.
	 */
	private void flashZombieCells() {
		for (int i = 0; i < Board.GRID_HEIGHT; i++) {
			for (int j = 0; j < Board.GRID_WIDTH; j++) {
				GridObject obj = board.getObject(i, j);
				if (obj instanceof Zombie) {
					JButton button = view.getButtons()[i][j];
					if (button instanceof GridCellButton) {
						((GridCellButton) button).flashDanger();
					}
				}
			}
		}
	}

	/**
	 * This method checks to see if the player has won or lost
	 */
	private void playerWinLose() {
		// If Player Wins the Level because there are no zombies to be spawned and no
		// zombies on the board
		if (level.zombiesEmpty() && board.zombiesOnBoard.isEmpty()) {
			// Stop animations
			animationManager.stopAllAnimations();

			// Show victory dialog
			SoundManager.play(SoundManager.VICTORY);

			final int currentLevel = level.getLevelNo();
			final boolean hasNextLevel = level.nextLevelExists() && !level.isCustomLevel();

			GameOverDialog.showVictory(view, currentLevel, new GameOverDialog.GameOverListener() {
				@Override
				public void onNextLevel() {
					if (hasNextLevel) {
						startGame(currentLevel + 1);
						initPlantCards();
						registerAllAnimations();
						gridCond(State.STATS);
					}
				}

				@Override
				public void onTryAgain() {
					startGame(currentLevel);
					initPlantCards();
					registerAllAnimations();
					gridCond(State.STATS);
				}

				@Override
				public void onMainMenu() {
					animationManager.stopAllAnimations();
					view.showStartScreen();
				}
			});
			return;
		}

		// If Player Loses the Level because zombies have reached the first column
		if (board.zombiesInFirstColumn()) {
			// Stop animations
			animationManager.stopAllAnimations();

			// Show defeat dialog
			SoundManager.play(SoundManager.DEFEAT);

			final int currentLevel = level.getLevelNo();

			GameOverDialog.showDefeat(view, currentLevel, new GameOverDialog.GameOverListener() {
				@Override
				public void onNextLevel() {
					// Not applicable for defeat
				}

				@Override
				public void onTryAgain() {
					startGame(currentLevel);
					initPlantCards();
					registerAllAnimations();
					gridCond(State.STATS);
				}

				@Override
				public void onMainMenu() {
					animationManager.stopAllAnimations();
					view.showStartScreen();
				}
			});
			return;
		}
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
				// Update the button at the specified location
				view.updateButton(button, board.grid[i][j]);

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
		view.clearPlantSelection();

		// Reinitialize plant cards for loaded level
		initPlantCards();

		// Re-register animations
		registerAllAnimations();

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
			SoundManager.play(SoundManager.BUTTON_CLICK);
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
		// Plant cards are always enabled but visually show availability
	}
}

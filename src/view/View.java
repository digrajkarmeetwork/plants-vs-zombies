package view;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import model.Board;
import model.GridObject;
import model.Level;
import model.NullSpace;
import model.Plant;
import model.Zombie;
import util.ResourceLoader;
import util.SoundManager;

/**
 * Main game view with modern UI, animations, and themed styling.
 */
public class View extends JFrame {

    // Screen state
    public enum ScreenState {
        START_SCREEN,
        GAME_SCREEN
    }

    private ScreenState currentScreen = ScreenState.START_SCREEN;

    // Start screen
    private StartScreen startScreen;

    // Game components
    private JPanel gamePanel;
    private JMenuBar menuBar;
    private JMenu menu, levels;
    private JMenuItem start, restart, help, importOption, exportOption;
    public JMenuItem editLevel, level1, level2, level3;

    // Sun points display
    private JLabel sunPointsLabel;
    private JPanel sunPointsPanel;
    private JLabel levelInfoLabel;

    // Action buttons
    private JButton undoTurn, endTurn, redoTurn, confirm;

    // Grid
    private JPanel gridLayoutButtons;
    private GridCellButton[][] gridButtons;

    // Plant selection (new style)
    private JPanel plantSelectionPanel;
    private PlantCardPanel[] plantCards;
    private int selectedPlantIndex = -1;

    // Legacy plant list (for compatibility)
    private JList<JPanel> menuList;
    private JLabel coins;

    // Animation manager
    private AnimationManager animationManager;

    // Level editor
    private JTextField genericZombieCB, frankTheTankCB, burrowingBaileyCB;
    private ArrayList<JTextField> availableZombies;
    private JTextArea numOfZombies;
    private JPanel genericZombiePanel, frankTheTankPanel, burrowingBaileyPanel;
    private JLabel genericZombieLabel, frankTheTankLabel, burrowingBaileyLabel;
    private JDialog levelEditorFrame;

    // Callback for start screen
    private StartScreen.StartScreenListener startScreenListener;

    /**
     * Constructor - creates the view.
     */
    public View() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(GameTheme.PANEL_BACKGROUND);

        // Preload resources
        ResourceLoader.preloadAllEntities();

        // Create animation manager
        animationManager = new AnimationManager(this);

        // Create start screen
        createStartScreen();

        // Create game panel (hidden initially)
        createGamePanel();

        // Show start screen
        showStartScreen();

        // Window settings
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Plants VS Zombies");

        // Setup keyboard shortcuts
        setupKeyboardShortcuts();

        pack();
        setVisible(true);
    }

    /**
     * Create the start screen.
     */
    private void createStartScreen() {
        startScreen = new StartScreen();
    }

    /**
     * Create the main game panel.
     */
    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(GameTheme.PANEL_BACKGROUND);

        // Create menu bar
        createMenuBar();

        // Create top info panel
        JPanel topPanel = createTopPanel();
        gamePanel.add(topPanel, BorderLayout.NORTH);

        // Create grid
        createGrid();
        gamePanel.add(gridLayoutButtons, BorderLayout.CENTER);

        // Create plant selection panel
        createPlantSelectionPanel();
        gamePanel.add(plantSelectionPanel, BorderLayout.WEST);

        // Create bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the menu bar.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(GameTheme.PANEL_SECONDARY);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GameTheme.PANEL_BORDER));

        // Style menus
        menu = new JMenu("Menu");
        menu.setForeground(GameTheme.TEXT_LIGHT);
        levels = new JMenu("Levels");
        levels.setForeground(GameTheme.TEXT_LIGHT);

        // Menu items
        start = createMenuItem("Start");
        restart = createMenuItem("Restart");
        help = createMenuItem("Help");
        importOption = createMenuItem("Load");
        exportOption = createMenuItem("Save");

        menu.add(start);
        menu.add(restart);
        menu.addSeparator();
        menu.add(help);
        menu.addSeparator();
        menu.add(importOption);
        menu.add(exportOption);

        // Level items
        editLevel = createMenuItem("Level Editor");
        level1 = createMenuItem("Level 1");
        level2 = createMenuItem("Level 2");
        level2.setEnabled(false);
        level3 = createMenuItem("Level 3");
        level3.setEnabled(false);

        levels.add(editLevel);
        levels.addSeparator();
        levels.add(level1);
        levels.add(level2);
        levels.add(level3);

        confirm = new JButton("Confirm");
        styleButton(confirm);

        menuBar.add(menu);
        menuBar.add(levels);

        // Sound toggle
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setForeground(GameTheme.TEXT_LIGHT);
        JCheckBoxMenuItem soundToggle = new JCheckBoxMenuItem("Sound Effects", true);
        soundToggle.addActionListener(e -> SoundManager.setSoundEnabled(soundToggle.isSelected()));
        optionsMenu.add(soundToggle);
        menuBar.add(optionsMenu);

        // Main menu button on right
        menuBar.add(Box.createHorizontalGlue());
        JMenuItem mainMenuBtn = new JMenuItem("Main Menu");
        mainMenuBtn.addActionListener(e -> showStartScreen());
        menuBar.add(mainMenuBtn);
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(GameTheme.PANEL_SECONDARY);
        item.setForeground(GameTheme.TEXT_LIGHT);
        return item;
    }

    private void styleButton(JButton button) {
        button.setBackground(GameTheme.BUTTON_NORMAL);
        button.setForeground(GameTheme.BUTTON_TEXT);
        button.setFont(GameTheme.FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(GameTheme.BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(GameTheme.BUTTON_NORMAL);
                }
            }
        });
    }

    /**
     * Create top info panel with sun points and level info.
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GameTheme.PANEL_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Menu bar
        panel.add(menuBar, BorderLayout.NORTH);

        // Info bar
        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setBackground(GameTheme.PANEL_SECONDARY);
        infoBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Sun points panel
        sunPointsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sunPointsPanel.setOpaque(false);

        JLabel sunIcon = new JLabel("\u2600"); // Sun symbol
        sunIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        sunIcon.setForeground(GameTheme.SUN_GOLD);
        sunPointsPanel.add(sunIcon);

        sunPointsLabel = new JLabel("50");
        sunPointsLabel.setFont(GameTheme.FONT_SUN_POINTS);
        sunPointsLabel.setForeground(GameTheme.SUN_GOLD);
        sunPointsPanel.add(sunPointsLabel);

        // Legacy coins label (for compatibility)
        coins = new JLabel("       Sun Points: 50");
        coins.setForeground(GameTheme.SUN_GOLD);

        infoBar.add(sunPointsPanel, BorderLayout.WEST);

        // Level info
        levelInfoLabel = new JLabel("Level 1");
        levelInfoLabel.setFont(GameTheme.FONT_HEADING);
        levelInfoLabel.setForeground(GameTheme.TEXT_LIGHT);
        infoBar.add(levelInfoLabel, BorderLayout.EAST);

        panel.add(infoBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the game grid.
     */
    private void createGrid() {
        gridLayoutButtons = new JPanel(new GridLayout(Board.GRID_HEIGHT, Board.GRID_WIDTH, 2, 2));
        gridLayoutButtons.setBackground(GameTheme.PANEL_BACKGROUND);
        gridLayoutButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gridButtons = new GridCellButton[Board.GRID_HEIGHT][Board.GRID_WIDTH];

        for (int i = 0; i < Board.GRID_HEIGHT; i++) {
            for (int j = 0; j < Board.GRID_WIDTH; j++) {
                gridButtons[i][j] = new GridCellButton(i, j, Board.GRID_WIDTH);
                gridButtons[i][j].setEnabled(false);
                gridButtons[i][j].setActionCommand(i + " " + j);
                gridLayoutButtons.add(gridButtons[i][j]);
            }
        }

        // Legacy buttons array for compatibility
        View.buttons = new JButton[Board.GRID_HEIGHT][Board.GRID_WIDTH];
        for (int i = 0; i < Board.GRID_HEIGHT; i++) {
            for (int j = 0; j < Board.GRID_WIDTH; j++) {
                buttons[i][j] = gridButtons[i][j];
            }
        }
    }

    /**
     * Create plant selection panel.
     */
    private void createPlantSelectionPanel() {
        plantSelectionPanel = new JPanel();
        plantSelectionPanel.setLayout(new BoxLayout(plantSelectionPanel, BoxLayout.Y_AXIS));
        plantSelectionPanel.setBackground(GameTheme.PANEL_BACKGROUND);
        plantSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        plantSelectionPanel.setPreferredSize(new Dimension(150, 0));

        // Title
        JLabel title = new JLabel("Plants");
        title.setFont(GameTheme.FONT_HEADING);
        title.setForeground(GameTheme.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        plantSelectionPanel.add(title);
        plantSelectionPanel.add(Box.createVerticalStrut(10));

        // Plant cards will be added by initPlantCards()

        // Legacy menu list (hidden, for compatibility)
        menuList = new JList<>();
        menuList.setVisible(false);
    }

    /**
     * Initialize plant cards with actual plant data.
     */
    public void initPlantCards(Plant[] plants) {
        // Remove old cards
        if (plantCards != null) {
            for (PlantCardPanel card : plantCards) {
                plantSelectionPanel.remove(card);
            }
        }

        // Create new cards
        plantCards = new PlantCardPanel[plants.length];

        for (int i = 0; i < plants.length; i++) {
            final int index = i;
            plantCards[i] = new PlantCardPanel(plants[i], i);
            plantCards[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            plantCards[i].setListener(card -> selectPlant(index));
            plantSelectionPanel.add(plantCards[i]);
            plantSelectionPanel.add(Box.createVerticalStrut(5));
        }

        plantSelectionPanel.revalidate();
        plantSelectionPanel.repaint();
    }

    /**
     * Update plant card states based on current coins.
     */
    public void updatePlantCards(int currentCoins) {
        if (plantCards != null) {
            for (PlantCardPanel card : plantCards) {
                card.updateState(currentCoins);
            }
        }
    }

    /**
     * Select a plant by index.
     */
    public void selectPlant(int index) {
        // Deselect all
        if (plantCards != null) {
            for (int i = 0; i < plantCards.length; i++) {
                plantCards[i].setSelected(i == index);
            }
        }
        selectedPlantIndex = index;

        // Update legacy list selection for compatibility
        if (menuList != null && index >= 0) {
            menuList.setSelectedIndex(index);
        }
    }

    /**
     * Get selected plant index.
     */
    public int getSelectedPlantIndex() {
        return selectedPlantIndex;
    }

    /**
     * Clear plant selection.
     */
    public void clearPlantSelection() {
        selectedPlantIndex = -1;
        if (plantCards != null) {
            for (PlantCardPanel card : plantCards) {
                card.setSelected(false);
            }
        }
        if (menuList != null) {
            menuList.clearSelection();
        }
    }

    /**
     * Create bottom panel with action buttons.
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(GameTheme.PANEL_SECONDARY);
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GameTheme.PANEL_BORDER));

        undoTurn = new JButton("Undo [U]");
        styleButton(undoTurn);
        undoTurn.setEnabled(false);

        endTurn = new JButton("End Turn [Space]");
        styleButton(endTurn);
        endTurn.setBackground(GameTheme.ACCENT_GREEN);

        redoTurn = new JButton("Redo [R]");
        styleButton(redoTurn);
        redoTurn.setEnabled(false);

        panel.add(undoTurn);
        panel.add(endTurn);
        panel.add(redoTurn);

        return panel;
    }

    /**
     * Setup keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // Plant selection (1-5)
        for (int i = 1; i <= 5; i++) {
            final int plantIndex = i - 1;
            String key = "selectPlant" + i;
            inputMap.put(KeyStroke.getKeyStroke(String.valueOf(i)), key);
            actionMap.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentScreen == ScreenState.GAME_SCREEN && plantCards != null && plantIndex < plantCards.length) {
                        if (plantCards[plantIndex].isAvailable()) {
                            selectPlant(plantIndex);
                            SoundManager.play(SoundManager.BUTTON_CLICK);
                        }
                    }
                }
            });
        }

        // End turn (Space)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "endTurn");
        actionMap.put("endTurn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentScreen == ScreenState.GAME_SCREEN && endTurn.isEnabled()) {
                    endTurn.doClick();
                }
            }
        });

        // Undo (U)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentScreen == ScreenState.GAME_SCREEN && undoTurn.isEnabled()) {
                    undoTurn.doClick();
                }
            }
        });

        // Redo (R)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentScreen == ScreenState.GAME_SCREEN && redoTurn.isEnabled()) {
                    redoTurn.doClick();
                }
            }
        });

        // Escape - clear selection
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearPlantSelection();
            }
        });

        // H - Help
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "help");
        actionMap.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentScreen == ScreenState.GAME_SCREEN && help.isEnabled()) {
                    help.doClick();
                }
            }
        });
    }

    /**
     * Show start screen.
     */
    public void showStartScreen() {
        currentScreen = ScreenState.START_SCREEN;
        getContentPane().removeAll();
        add(startScreen, BorderLayout.CENTER);
        animationManager.stop();
        revalidate();
        repaint();
    }

    /**
     * Show game screen.
     */
    public void showGameScreen() {
        currentScreen = ScreenState.GAME_SCREEN;
        getContentPane().removeAll();
        add(gamePanel, BorderLayout.CENTER);
        animationManager.start();
        revalidate();
        repaint();
    }

    /**
     * Set start screen listener.
     */
    public void setStartScreenListener(StartScreen.StartScreenListener listener) {
        this.startScreenListener = listener;
        startScreen.setListener(listener);
    }

    /**
     * Get start screen.
     */
    public StartScreen getStartScreen() {
        return startScreen;
    }

    /**
     * Update sun points display.
     */
    public void updateSunPoints(int points) {
        sunPointsLabel.setText(String.valueOf(points));
        coins.setText("       Sun Points: " + points);

        // Update plant card affordability
        updatePlantCards(points);
    }

    /**
     * Set level info text.
     */
    public void setLevelInfo(String info) {
        levelInfoLabel.setText(info);
    }

    /**
     * Get grid cell button at position.
     */
    public GridCellButton getGridButton(int row, int col) {
        if (row >= 0 && row < Board.GRID_HEIGHT && col >= 0 && col < Board.GRID_WIDTH) {
            return gridButtons[row][col];
        }
        return null;
    }

    /**
     * Update a grid cell with entity.
     */
    public void updateGridCell(int row, int col, GridObject entity) {
        GridCellButton button = getGridButton(row, col);
        if (button != null) {
            button.setEntity(entity);
            animationManager.registerEntity(button, entity);
        }
    }

    /**
     * Get animation manager.
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * Get plant cards array.
     */
    public PlantCardPanel[] getPlantCards() {
        return plantCards;
    }

    /**
     * Select a plant card by index.
     */
    public void selectPlantCard(int index) {
        selectPlant(index);
    }

    // ==================== LEVEL EDITOR ====================

    public void makeLevelEditor() {
        levelEditorFrame = new JDialog(this);
        levelEditorFrame.setTitle("Level Editor");
        levelEditorFrame.setResizable(false);
        levelEditorFrame.getContentPane().setBackground(GameTheme.PANEL_BACKGROUND);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(GameTheme.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel editorPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        editorPanel.setBackground(GameTheme.PANEL_BACKGROUND);

        genericZombiePanel = createZombieInputPanel("Generic Zombie:");
        frankTheTankPanel = createZombieInputPanel("Frank The Tank:");
        burrowingBaileyPanel = createZombieInputPanel("Burrowing Bailey:");

        setGenericZombieCB((JTextField) ((JPanel) genericZombiePanel).getComponent(1));
        setFrankTheTankCB((JTextField) ((JPanel) frankTheTankPanel).getComponent(1));
        setBurrowingBaileyCB((JTextField) ((JPanel) burrowingBaileyPanel).getComponent(1));

        availableZombies = new ArrayList<>();
        availableZombies.add(genericZombieCB);
        availableZombies.add(frankTheTankCB);
        availableZombies.add(burrowingBaileyCB);

        JLabel title = new JLabel("Create Custom Level");
        title.setFont(GameTheme.FONT_HEADING);
        title.setForeground(GameTheme.SUN_GOLD);

        panel.add(title, BorderLayout.NORTH);
        editorPanel.add(genericZombiePanel);
        editorPanel.add(frankTheTankPanel);
        editorPanel.add(burrowingBaileyPanel);

        confirm = new JButton("Confirm");
        styleButton(confirm);
        editorPanel.add(confirm);

        panel.add(editorPanel, BorderLayout.CENTER);

        levelEditorFrame.add(panel);
        levelEditorFrame.pack();
        levelEditorFrame.setLocationRelativeTo(this);
        levelEditorFrame.setSize(400, 220);
        levelEditorFrame.setVisible(true);
    }

    private JPanel createZombieInputPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(GameTheme.PANEL_BACKGROUND);

        JLabel label = new JLabel(labelText);
        label.setFont(GameTheme.FONT_NORMAL);
        label.setForeground(GameTheme.TEXT_LIGHT);
        label.setPreferredSize(new Dimension(150, 25));

        JTextField field = new JTextField("0", 5);
        field.setFont(GameTheme.FONT_NORMAL);

        panel.add(label);
        panel.add(field);
        return panel;
    }

    // ==================== INFO DIALOG ====================

    public void makeInfoFrame() {
        help.setEnabled(false);
        JDialog infoFrame = new JDialog(this);
        infoFrame.setTitle("Plant Information");
        infoFrame.setResizable(false);
        infoFrame.getContentPane().setBackground(GameTheme.PANEL_BACKGROUND);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(GameTheme.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBackground(GameTheme.PANEL_BACKGROUND);

        addPlantInfo(infoPanel, "Sunflower", "50 sun", "Generates 25 sun/turn");
        addPlantInfo(infoPanel, "Venus Flytrap", "150 sun", "Strong melee attacker");
        addPlantInfo(infoPanel, "Walnut", "50 sun", "High HP defensive wall");
        addPlantInfo(infoPanel, "Potato", "25 sun", "Cheap defense");
        addPlantInfo(infoPanel, "Pea Shooter", "100 sun", "Ranged row attacker");

        panel.add(infoPanel, BorderLayout.CENTER);

        JLabel title = new JLabel("Plant Costs & Abilities");
        title.setFont(GameTheme.FONT_HEADING);
        title.setForeground(GameTheme.SUN_GOLD);
        panel.add(title, BorderLayout.NORTH);

        infoFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                help.setEnabled(true);
            }
        });

        infoFrame.add(panel);
        infoFrame.pack();
        infoFrame.setLocationRelativeTo(this);
        infoFrame.setVisible(true);
    }

    private void addPlantInfo(JPanel panel, String name, String cost, String ability) {
        JLabel nameLabel = new JLabel(name + ":");
        nameLabel.setFont(GameTheme.FONT_NORMAL);
        nameLabel.setForeground(GameTheme.ACCENT_GREEN);
        panel.add(nameLabel);

        JLabel infoLabel = new JLabel(cost + " - " + ability);
        infoLabel.setFont(GameTheme.FONT_SMALL);
        infoLabel.setForeground(GameTheme.TEXT_LIGHT);
        panel.add(infoLabel);
    }

    // ==================== STATS DIALOGS ====================

    public void displayStats(GridObject o) {
        if (o instanceof Plant)
            displayPlantStats((Plant) o);
        else
            displayZombieStats((Zombie) o);
    }

    private void displayPlantStats(Plant plant) {
        JDialog dialog = new JDialog(this);
        dialog.setTitle("Plant Stats");
        dialog.getContentPane().setBackground(GameTheme.PANEL_BACKGROUND);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBackground(GameTheme.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addStatRow(panel, "Type:", plant.getObjectTitle());
        addStatRow(panel, "Health:", plant.getHealth() + "/" + plant.getFullHealth());
        addStatRow(panel, "Strength:", String.valueOf(plant.getStrength()));
        addStatRow(panel, "Cooldown:", plant.getCurrentTime() + "/" + plant.getFullTime() + " turns");

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void displayZombieStats(Zombie zombie) {
        JDialog dialog = new JDialog(this);
        dialog.setTitle("Zombie Stats");
        dialog.getContentPane().setBackground(GameTheme.PANEL_BACKGROUND);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBackground(GameTheme.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addStatRow(panel, "Type:", zombie.getObjectTitle());
        addStatRow(panel, "Health:", zombie.getHealth() + "/" + zombie.getFullHealth());
        addStatRow(panel, "Strength:", String.valueOf(zombie.getStrength()));

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameTheme.FONT_NORMAL);
        labelComp.setForeground(GameTheme.TEXT_MUTED);
        panel.add(labelComp);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(GameTheme.FONT_NORMAL);
        valueComp.setForeground(GameTheme.TEXT_LIGHT);
        panel.add(valueComp);
    }

    // ==================== LEGACY METHODS (for Controller compatibility) ====================

    public static JButton[][] buttons;

    public void updateButton(JButton button, GridObject o) {
        if (button instanceof GridCellButton) {
            ((GridCellButton) button).setEntity(o);
            animationManager.registerEntity((GridCellButton) button, o);
        } else {
            // Legacy fallback
            if (o instanceof NullSpace) {
                button.setIcon(null);
                return;
            }
            try {
                ImageIcon image = ResourceLoader.loadScaledIcon(o.getObjectTitle() + ".png", 80, 60);
                button.setIcon(image);
                button.setDisabledIcon(image);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    public void playAnimation(JButton button, GridObject o) {
        // Now handled by AnimationManager continuously
        if (button instanceof GridCellButton) {
            animationManager.registerEntity((GridCellButton) button, o);
        }
    }

    public JButton[][] getButtons() {
        return buttons;
    }

    public JList<JPanel> getPlants() {
        return menuList;
    }

    public JLabel getCoins() {
        return coins;
    }

    public JMenuItem getHelp() {
        return help;
    }

    public JButton getUndoTurn() {
        return undoTurn;
    }

    public JButton getEndTurn() {
        return endTurn;
    }

    public JButton getRedoTurn() {
        return redoTurn;
    }

    public JMenuItem getStart() {
        return start;
    }

    public JMenuItem getRestart() {
        return restart;
    }

    public JMenuItem getImportOption() {
        return importOption;
    }

    public JMenuItem getExportOption() {
        return exportOption;
    }

    public JPanel getGridLayoutButtons() {
        return gridLayoutButtons;
    }

    public JTextField getGenericZombieCB() {
        return genericZombieCB;
    }

    public void setGenericZombieCB(JTextField cb) {
        this.genericZombieCB = cb;
    }

    public JTextField getFrankTheTankCB() {
        return frankTheTankCB;
    }

    public void setFrankTheTankCB(JTextField cb) {
        this.frankTheTankCB = cb;
    }

    public JTextField getBurrowingBaileyCB() {
        return burrowingBaileyCB;
    }

    public void setBurrowingBaileyCB(JTextField cb) {
        this.burrowingBaileyCB = cb;
    }

    public ArrayList<JTextField> getAvailableZombies() {
        return availableZombies;
    }

    public JButton getConfirm() {
        return confirm;
    }

    public JDialog getLevelEditorFrame() {
        return levelEditorFrame;
    }

    public JMenu getMenu() {
        return menu;
    }

    public Thread getAnimationThread() {
        return null; // Legacy - no longer used
    }

    public void setAnimationThread(Thread t) {
        // Legacy - no longer used
    }

    public void setUndoTurn(JButton btn) {
        this.undoTurn = btn;
    }

    public void setEndTurn(JButton btn) {
        this.endTurn = btn;
    }

    public void setRedoTurn(JButton btn) {
        this.redoTurn = btn;
    }

    public JList<JPanel> getMenuList() {
        return menuList;
    }

    public void setMenuList(JList<JPanel> list) {
        this.menuList = list;
    }

    public void setCoins(JLabel c) {
        this.coins = c;
    }

    public void setHelp(JMenuItem h) {
        this.help = h;
    }

    public static void setButtons(JButton[][] b) {
        buttons = b;
    }

    public void setMenuBar(JMenuBar mb) {
        this.menuBar = mb;
    }

    public void setMenu(JMenu m) {
        this.menu = m;
    }

    public void setStart(JMenuItem s) {
        this.start = s;
    }

    public void setRestart(JMenuItem r) {
        this.restart = r;
    }

    public void setGridLayoutButtons(JPanel p) {
        this.gridLayoutButtons = p;
    }

    public void setAvailableZombies(ArrayList<JTextField> az) {
        this.availableZombies = az;
    }

    public JTextArea getNumOfZombies() {
        return numOfZombies;
    }

    public void setNumOfZombies(JTextArea n) {
        this.numOfZombies = n;
    }
}

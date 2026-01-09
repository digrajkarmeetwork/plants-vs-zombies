package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized theme constants for the Plants vs Zombies game.
 * Modern/sleek color palette with dark browns and greens.
 */
public class GameTheme {

    // Grass colors for game grid
    public static final Color GRASS_LIGHT = new Color(124, 179, 66);      // #7CB342
    public static final Color GRASS_DARK = new Color(85, 139, 47);        // #558B2F
    public static final Color GRASS_HIGHLIGHT = new Color(156, 204, 101); // #9CCD65
    public static final Color GRASS_HOVER = new Color(139, 195, 74);      // #8BC34A

    // Danger zone (zombie spawn column)
    public static final Color DANGER_ZONE_LIGHT = new Color(120, 100, 90);
    public static final Color DANGER_ZONE_DARK = new Color(100, 80, 70);

    // Panel backgrounds (dark brown wood/soil theme)
    public static final Color PANEL_BACKGROUND = new Color(62, 39, 35);   // #3E2723
    public static final Color PANEL_SECONDARY = new Color(93, 64, 55);    // #5D4037
    public static final Color PANEL_ACCENT = new Color(121, 85, 72);      // #795548
    public static final Color PANEL_BORDER = new Color(78, 52, 46);       // Dark brown border

    // Sun/coins gold
    public static final Color SUN_GOLD = new Color(255, 193, 7);          // #FFC107
    public static final Color SUN_GOLD_DARK = new Color(255, 160, 0);     // #FFA000

    // Health bar colors
    public static final Color HEALTH_FULL = new Color(76, 175, 80);       // #4CAF50
    public static final Color HEALTH_MEDIUM = new Color(255, 193, 7);     // #FFC107
    public static final Color HEALTH_LOW = new Color(244, 67, 54);        // #F44336
    public static final Color HEALTH_BACKGROUND = new Color(60, 60, 60);  // Dark gray
    public static final Color HEALTH_BORDER = new Color(40, 40, 40);      // Darker border

    // Text colors
    public static final Color TEXT_LIGHT = new Color(255, 248, 225);      // #FFF8E1 (cream)
    public static final Color TEXT_DARK = new Color(33, 33, 33);          // #212121
    public static final Color TEXT_GOLD = new Color(255, 215, 0);         // Gold text
    public static final Color TEXT_MUTED = new Color(158, 158, 158);      // Gray muted text

    // Accent colors
    public static final Color ACCENT_BLUE = new Color(33, 150, 243);      // #2196F3
    public static final Color ACCENT_GREEN = new Color(67, 160, 71);      // #43A047
    public static final Color DANGER_RED = new Color(229, 57, 53);        // #E53935

    // Button colors
    public static final Color BUTTON_NORMAL = new Color(76, 175, 80);     // Green
    public static final Color BUTTON_HOVER = new Color(102, 187, 106);    // Light green
    public static final Color BUTTON_PRESSED = new Color(56, 142, 60);    // Dark green
    public static final Color BUTTON_DISABLED = new Color(120, 120, 120); // Gray
    public static final Color BUTTON_TEXT = new Color(255, 255, 255);     // White

    // Plant card states
    public static final Color CARD_AVAILABLE = new Color(93, 64, 55);     // Normal brown
    public static final Color CARD_SELECTED = new Color(255, 193, 7);     // Gold border
    public static final Color CARD_COOLDOWN = new Color(80, 80, 80);      // Grayed out
    public static final Color CARD_CANT_AFFORD = new Color(120, 60, 60);  // Reddish

    // Overlay colors
    public static final Color OVERLAY_DARK = new Color(0, 0, 0, 150);     // Semi-transparent black
    public static final Color OVERLAY_LIGHT = new Color(255, 255, 255, 50); // Semi-transparent white
    public static final Color COOLDOWN_OVERLAY = new Color(0, 0, 0, 128); // Cooldown gray

    // Title screen
    public static final Color TITLE_GRADIENT_TOP = new Color(45, 80, 22);
    public static final Color TITLE_GRADIENT_BOTTOM = new Color(30, 50, 15);

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 48);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_CARD_NAME = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_CARD_INFO = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_SUN_POINTS = new Font("SansSerif", Font.BOLD, 20);

    // Dimensions
    public static final int GRID_CELL_WIDTH = 90;
    public static final int GRID_CELL_HEIGHT = 80;
    public static final int HEALTH_BAR_HEIGHT = 6;
    public static final int PLANT_CARD_WIDTH = 130;
    public static final int PLANT_CARD_HEIGHT = 100;
    public static final int CARD_ICON_SIZE = 50;
    public static final int GRID_ICON_WIDTH = 80;
    public static final int GRID_ICON_HEIGHT = 60;

    // Animation
    public static final int ANIMATION_FRAME_DELAY = 200; // ms between frames
    public static final int FLASH_DURATION = 150; // ms for flash effects

    /**
     * Get health bar color based on percentage
     */
    public static Color getHealthColor(int percentage) {
        if (percentage > 60) return HEALTH_FULL;
        if (percentage > 30) return HEALTH_MEDIUM;
        return HEALTH_LOW;
    }

    /**
     * Get grass color for grid cell (alternating pattern)
     */
    public static Color getGrassColor(int row, int col, boolean isHovered) {
        if (isHovered) {
            return GRASS_HOVER;
        }
        return ((row + col) % 2 == 0) ? GRASS_LIGHT : GRASS_DARK;
    }

    /**
     * Check if column is the danger zone (zombie spawn)
     */
    public static boolean isDangerZone(int col, int gridWidth) {
        return col == gridWidth - 1;
    }

    /**
     * Get color for danger zone cells
     */
    public static Color getDangerZoneColor(int row, int col) {
        return ((row + col) % 2 == 0) ? DANGER_ZONE_LIGHT : DANGER_ZONE_DARK;
    }
}

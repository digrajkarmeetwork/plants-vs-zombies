package view;

import model.Plant;
import util.ResourceLoader;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Plant selection card showing icon, name, cost, and cooldown.
 */
public class PlantCardPanel extends JPanel {

    public interface PlantCardListener {
        void onPlantSelected(PlantCardPanel card);
    }

    private Plant plant;
    private String plantName;
    private int cost;
    private ImageIcon icon;
    private boolean selected = false;
    private boolean hovering = false;
    private boolean available = true;
    private boolean affordable = true;
    private int cooldownCurrent = 0;
    private int cooldownMax = 0;
    private PlantCardListener listener;
    private String tooltip;

    // Plant descriptions for tooltips
    private static final String[] PLANT_DESCRIPTIONS = {
        "Sunflower: Generates 25 sun points each turn",
        "Venus Flytrap: Strong melee attacker",
        "Walnut: High health defensive wall",
        "Potato: Cheap defensive plant",
        "Pea Shooter: Ranged attacker, hits all zombies in row"
    };

    public PlantCardPanel(Plant plant, int index) {
        this.plant = plant;
        this.plantName = plant.getObjectTitle();
        this.cost = plant.getPrice();
        this.cooldownMax = plant.getFullTime();

        // Load icon
        this.icon = ResourceLoader.loadCardIcon(plantName);

        // Set tooltip
        if (index >= 0 && index < PLANT_DESCRIPTIONS.length) {
            this.tooltip = PLANT_DESCRIPTIONS[index];
        } else {
            this.tooltip = plantName + " - Cost: " + cost;
        }
        setToolTipText(tooltip);

        // Setup panel
        setOpaque(false);
        setPreferredSize(new Dimension(GameTheme.PLANT_CARD_WIDTH, GameTheme.PLANT_CARD_HEIGHT));
        setMinimumSize(new Dimension(GameTheme.PLANT_CARD_WIDTH, GameTheme.PLANT_CARD_HEIGHT));
        setMaximumSize(new Dimension(GameTheme.PLANT_CARD_WIDTH, GameTheme.PLANT_CARD_HEIGHT));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse interactions
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (available && affordable) {
                    SoundManager.play(SoundManager.BUTTON_CLICK);
                    if (listener != null) {
                        listener.onPlantSelected(PlantCardPanel.this);
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 12;

        // Determine background color based on state
        Color bgColor;
        if (!available) {
            bgColor = GameTheme.CARD_COOLDOWN;
        } else if (!affordable) {
            bgColor = GameTheme.CARD_CANT_AFFORD;
        } else if (selected) {
            bgColor = GameTheme.PANEL_ACCENT;
        } else if (hovering) {
            bgColor = GameTheme.PANEL_SECONDARY.brighter();
        } else {
            bgColor = GameTheme.PANEL_SECONDARY;
        }

        // Draw card background
        RoundRectangle2D.Float cardBg = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc);
        g2d.setColor(bgColor);
        g2d.fill(cardBg);

        // Draw selection border
        if (selected) {
            g2d.setColor(GameTheme.CARD_SELECTED);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(cardBg);
        } else {
            g2d.setColor(bgColor.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(cardBg);
        }

        // Draw plant icon
        int iconSize = GameTheme.CARD_ICON_SIZE;
        int iconX = 8;
        int iconY = (h - iconSize) / 2;

        if (icon != null) {
            // Draw shadow under icon
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fillOval(iconX + 3, iconY + iconSize - 5, iconSize - 6, 8);

            icon.paintIcon(this, g2d, iconX, iconY);

            // Gray overlay if not available
            if (!available || !affordable) {
                g2d.setColor(new Color(60, 60, 60, 150));
                g2d.fillRect(iconX, iconY, iconSize, iconSize);
            }
        }

        // Text area
        int textX = iconX + iconSize + 8;
        int textWidth = w - textX - 8;

        // Plant name
        g2d.setFont(GameTheme.FONT_CARD_NAME);
        g2d.setColor(GameTheme.TEXT_LIGHT);
        g2d.drawString(plantName, textX, 22);

        // Cost with sun icon
        g2d.setFont(GameTheme.FONT_CARD_INFO);
        String costText = "Cost: " + cost;
        if (affordable) {
            g2d.setColor(GameTheme.SUN_GOLD);
        } else {
            g2d.setColor(GameTheme.DANGER_RED);
        }
        g2d.drawString(costText, textX, 40);

        // Cooldown status
        g2d.setFont(GameTheme.FONT_CARD_INFO);
        String statusText;
        if (!available) {
            statusText = "Wait: " + cooldownCurrent + " turn" + (cooldownCurrent != 1 ? "s" : "");
            g2d.setColor(GameTheme.TEXT_MUTED);
        } else {
            statusText = "Ready!";
            g2d.setColor(GameTheme.ACCENT_GREEN);
        }
        g2d.drawString(statusText, textX, 56);

        // Cooldown progress bar (if on cooldown)
        if (!available && cooldownMax > 0) {
            int barWidth = textWidth - 4;
            int barHeight = 6;
            int barX = textX;
            int barY = h - barHeight - 10;

            // Background
            g2d.setColor(GameTheme.HEALTH_BACKGROUND);
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);

            // Progress (inverted - shows time remaining)
            int progress = cooldownMax - cooldownCurrent;
            int fillWidth = (int) (barWidth * ((double) progress / cooldownMax));
            g2d.setColor(GameTheme.ACCENT_BLUE);
            g2d.fillRoundRect(barX, barY, fillWidth, barHeight, 3, 3);

            // Border
            g2d.setColor(GameTheme.HEALTH_BORDER);
            g2d.drawRoundRect(barX, barY, barWidth, barHeight, 3, 3);
        }

        // Keyboard shortcut hint (small number in corner)
        // Will be set by parent

        g2d.dispose();
    }

    /**
     * Update the card state based on current game state.
     */
    public void updateState(int currentCoins) {
        this.cooldownCurrent = plant.getCurrentTime();
        this.available = plant.isAvailable();
        this.affordable = cost <= currentCoins;
        repaint();
    }

    /**
     * Set selected state.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    /**
     * Check if selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Get the plant associated with this card.
     */
    public Plant getPlant() {
        return plant;
    }

    /**
     * Get plant name.
     */
    public String getPlantName() {
        return plantName;
    }

    /**
     * Check if plant is available for selection.
     */
    public boolean isAvailable() {
        return available && affordable;
    }

    /**
     * Set listener for selection events.
     */
    public void setListener(PlantCardListener listener) {
        this.listener = listener;
    }

    /**
     * Draw a keyboard shortcut hint on the card.
     */
    public void drawShortcutHint(Graphics g, String shortcut) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(GameTheme.FONT_SMALL);
        g2d.setColor(GameTheme.TEXT_MUTED);
        g2d.drawString("[" + shortcut + "]", getWidth() - 25, 15);
    }
}

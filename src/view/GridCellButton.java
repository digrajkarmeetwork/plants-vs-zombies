package view;

import model.GridObject;
import model.NullSpace;
import model.Plant;
import model.Zombie;
import util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom grid cell button with health bar and hover effects.
 */
public class GridCellButton extends JButton {

    private GridObject entity;
    private int row;
    private int col;
    private int gridWidth;
    private boolean hovering = false;
    private boolean showHealthBar = true;
    private int healthPercentage = 100;
    private ImageIcon entityIcon;
    private Color baseColor;
    private boolean flashActive = false;
    private Color flashColor = null;

    public GridCellButton(int row, int col, int gridWidth) {
        super();
        this.row = row;
        this.col = col;
        this.gridWidth = gridWidth;

        // Calculate base color
        updateBaseColor();

        // Setup appearance
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(GameTheme.GRID_CELL_WIDTH, GameTheme.GRID_CELL_HEIGHT));

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovering = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    private void updateBaseColor() {
        if (GameTheme.isDangerZone(col, gridWidth)) {
            baseColor = GameTheme.getDangerZoneColor(row, col);
        } else {
            baseColor = GameTheme.getGrassColor(row, col, false);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        // Background color
        Color bgColor = baseColor;
        if (flashActive && flashColor != null) {
            bgColor = flashColor;
        } else if (hovering && isEnabled()) {
            bgColor = GameTheme.GRASS_HOVER;
        }

        // Draw cell background with rounded corners
        RoundRectangle2D.Float cellBg = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 8, 8);
        g2d.setColor(bgColor);
        g2d.fill(cellBg);

        // Draw subtle border
        g2d.setColor(bgColor.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(cellBg);

        // Draw inner highlight for 3D effect
        if (!flashActive) {
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.drawLine(3, 3, w - 4, 3);
            g2d.drawLine(3, 3, 3, h - 4);
        }

        // Draw entity icon
        if (entityIcon != null && !(entity instanceof NullSpace)) {
            int iconW = entityIcon.getIconWidth();
            int iconH = entityIcon.getIconHeight();
            int iconX = (w - iconW) / 2;
            int iconY = (h - iconH) / 2 - 5; // Offset up for health bar

            // Draw shadow under entity
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fillOval(iconX + 5, iconY + iconH - 5, iconW - 10, 10);

            // Draw the icon
            entityIcon.paintIcon(this, g2d, iconX, iconY);

            // Gray overlay if disabled
            if (!isEnabled()) {
                g2d.setColor(new Color(100, 100, 100, 150));
                g2d.fillRect(iconX, iconY, iconW, iconH);
            }
        }

        // Draw health bar for entities
        if (showHealthBar && entity != null && !(entity instanceof NullSpace) && healthPercentage < 100) {
            drawHealthBar(g2d, w, h);
        }

        // Draw danger zone indicator
        if (GameTheme.isDangerZone(col, gridWidth)) {
            g2d.setColor(new Color(255, 0, 0, 30));
            g2d.fill(cellBg);

            // Draw warning stripes
            g2d.setColor(new Color(255, 200, 0, 40));
            for (int i = -h; i < w; i += 20) {
                g2d.drawLine(i, h, i + h, 0);
            }
        }

        g2d.dispose();
    }

    private void drawHealthBar(Graphics2D g2d, int cellWidth, int cellHeight) {
        int barHeight = GameTheme.HEALTH_BAR_HEIGHT;
        int barWidth = cellWidth - 16;
        int barX = 8;
        int barY = cellHeight - barHeight - 6;

        // Background
        g2d.setColor(GameTheme.HEALTH_BACKGROUND);
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);

        // Health fill
        int fillWidth = (int) (barWidth * (healthPercentage / 100.0));
        g2d.setColor(GameTheme.getHealthColor(healthPercentage));
        g2d.fillRoundRect(barX, barY, fillWidth, barHeight, 3, 3);

        // Border
        g2d.setColor(GameTheme.HEALTH_BORDER);
        g2d.drawRoundRect(barX, barY, barWidth, barHeight, 3, 3);
    }

    /**
     * Set the entity displayed in this cell.
     */
    public void setEntity(GridObject entity) {
        this.entity = entity;

        if (entity == null || entity instanceof NullSpace) {
            this.entityIcon = null;
            this.healthPercentage = 100;
        } else {
            // Load icon
            this.entityIcon = ResourceLoader.loadScaledIcon(
                entity.getObjectTitle() + ".png",
                GameTheme.GRID_ICON_WIDTH,
                GameTheme.GRID_ICON_HEIGHT
            );

            // Calculate health percentage
            if (entity instanceof Plant) {
                Plant p = (Plant) entity;
                this.healthPercentage = (p.getHealth() * 100) / p.getFullHealth();
            } else if (entity instanceof Zombie) {
                Zombie z = (Zombie) entity;
                this.healthPercentage = (z.getHealth() * 100) / z.getFullHealth();
            }
        }

        repaint();
    }

    /**
     * Update the icon directly (for animation).
     */
    public void setEntityIcon(ImageIcon icon) {
        this.entityIcon = icon;
        repaint();
    }

    /**
     * Get current entity.
     */
    public GridObject getEntity() {
        return entity;
    }

    /**
     * Flash the cell with a color (for visual feedback).
     */
    public void flash(Color color, int durationMs) {
        flashActive = true;
        flashColor = color;
        repaint();

        Timer timer = new Timer(durationMs, e -> {
            flashActive = false;
            flashColor = null;
            repaint();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Flash green for plant placement.
     */
    public void flashPlantPlaced() {
        flash(new Color(100, 255, 100, 150), GameTheme.FLASH_DURATION);
    }

    /**
     * Flash red for zombie spawn or damage.
     */
    public void flashDanger() {
        flash(new Color(255, 100, 100, 150), GameTheme.FLASH_DURATION);
    }

    /**
     * Flash yellow for sun collection.
     */
    public void flashSun() {
        flash(new Color(255, 255, 100, 150), GameTheme.FLASH_DURATION);
    }

    /**
     * Enable/disable health bar display.
     */
    public void setShowHealthBar(boolean show) {
        this.showHealthBar = show;
        repaint();
    }

    /**
     * Get row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * Get column index.
     */
    public int getCol() {
        return col;
    }

    /**
     * Update health percentage manually.
     */
    public void setHealthPercentage(int percentage) {
        this.healthPercentage = Math.max(0, Math.min(100, percentage));
        repaint();
    }
}

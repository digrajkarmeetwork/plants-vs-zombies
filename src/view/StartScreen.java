package view;

import util.ResourceLoader;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Title/Start screen for the game.
 * Shows game title, play buttons, and level selection.
 */
public class StartScreen extends JPanel {

    public interface StartScreenListener {
        void onPlayGame();
        void onSelectLevel(int level);
        void onLevelEditor();
    }

    private StartScreenListener listener;
    private JButton playButton;
    private JButton[] levelButtons;
    private JButton editorButton;
    private Timer animationTimer;
    private int animationFrame = 0;
    private ImageIcon[] plantFrames;
    private ImageIcon[] zombieFrames;

    public StartScreen() {
        setLayout(new BorderLayout());
        setBackground(GameTheme.PANEL_BACKGROUND);
        setPreferredSize(new Dimension(1000, 600));

        // Load animation frames
        loadAnimationFrames();

        // Create main content panel
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintBackground(g);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Add spacing at top
        contentPanel.add(Box.createVerticalStrut(40));

        // Title panel with animated characters
        JPanel titlePanel = createTitlePanel();
        contentPanel.add(titlePanel);

        contentPanel.add(Box.createVerticalStrut(30));

        // Play button
        playButton = createStyledButton("PLAY GAME", GameTheme.BUTTON_NORMAL, 250, 60);
        playButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        JPanel playPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playPanel.setOpaque(false);
        playPanel.add(playButton);
        contentPanel.add(playPanel);

        contentPanel.add(Box.createVerticalStrut(30));

        // Level selection
        JPanel levelPanel = createLevelPanel();
        contentPanel.add(levelPanel);

        contentPanel.add(Box.createVerticalStrut(20));

        // Level editor button
        editorButton = createStyledButton("Level Editor", GameTheme.PANEL_SECONDARY, 180, 45);
        JPanel editorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        editorPanel.setOpaque(false);
        editorPanel.add(editorButton);
        contentPanel.add(editorPanel);

        contentPanel.add(Box.createVerticalGlue());

        // Version info
        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(GameTheme.FONT_SMALL);
        versionLabel.setForeground(GameTheme.TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        add(contentPanel, BorderLayout.CENTER);

        // Setup button actions
        setupButtonActions();

        // Start animation
        startAnimation();
    }

    private void loadAnimationFrames() {
        plantFrames = new ImageIcon[4];
        zombieFrames = new ImageIcon[4];

        for (int i = 0; i < 4; i++) {
            plantFrames[i] = ResourceLoader.loadAnimationFrame("SunFlower", i, 80, 80);
            zombieFrames[i] = ResourceLoader.loadAnimationFrame("GenericZombie", i, 80, 80);
        }
    }

    private void paintBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, GameTheme.TITLE_GRADIENT_TOP,
            0, getHeight(), GameTheme.TITLE_GRADIENT_BOTTOM
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw some grass at bottom
        g2d.setColor(GameTheme.GRASS_DARK);
        int grassHeight = 100;
        g2d.fillRect(0, getHeight() - grassHeight, getWidth(), grassHeight);

        // Grass highlights
        g2d.setColor(GameTheme.GRASS_LIGHT);
        for (int x = 0; x < getWidth(); x += 40) {
            int h = 20 + (int)(Math.random() * 30);
            g2d.fillRect(x, getHeight() - grassHeight - h + 20, 20, h);
        }

        g2d.dispose();
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw animated plant on left
                if (plantFrames[animationFrame % 4] != null) {
                    plantFrames[animationFrame % 4].paintIcon(this, g2d, 50, 30);
                }

                // Draw animated zombie on right
                if (zombieFrames[animationFrame % 4] != null) {
                    zombieFrames[animationFrame % 4].paintIcon(this, g2d, getWidth() - 130, 30);
                }

                // Draw title text with shadow
                String title1 = "PLANTS";
                String title2 = "vs";
                String title3 = "ZOMBIES";

                Font titleFont = new Font("SansSerif", Font.BOLD, 56);
                Font vsFont = new Font("SansSerif", Font.BOLD, 32);
                g2d.setFont(titleFont);

                FontMetrics fm = g2d.getFontMetrics();
                int centerX = getWidth() / 2;
                int y = 60;

                // "PLANTS" - Green
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(title1, centerX - fm.stringWidth(title1) / 2 + 3, y + 3);
                g2d.setColor(GameTheme.ACCENT_GREEN);
                g2d.drawString(title1, centerX - fm.stringWidth(title1) / 2, y);

                // "vs" - Gold
                g2d.setFont(vsFont);
                fm = g2d.getFontMetrics();
                y += 45;
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(title2, centerX - fm.stringWidth(title2) / 2 + 2, y + 2);
                g2d.setColor(GameTheme.SUN_GOLD);
                g2d.drawString(title2, centerX - fm.stringWidth(title2) / 2, y);

                // "ZOMBIES" - Red
                g2d.setFont(titleFont);
                fm = g2d.getFontMetrics();
                y += 55;
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(title3, centerX - fm.stringWidth(title3) / 2 + 3, y + 3);
                g2d.setColor(GameTheme.DANGER_RED);
                g2d.drawString(title3, centerX - fm.stringWidth(title3) / 2, y);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(600, 180));
        panel.setMaximumSize(new Dimension(800, 180));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private JPanel createLevelPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel("Select Level: ");
        label.setFont(GameTheme.FONT_HEADING);
        label.setForeground(GameTheme.TEXT_LIGHT);
        panel.add(label);

        levelButtons = new JButton[3];
        for (int i = 0; i < 3; i++) {
            levelButtons[i] = createStyledButton("Level " + (i + 1), GameTheme.PANEL_ACCENT, 120, 45);
            final int level = i + 1;
            levelButtons[i].addActionListener(e -> {
                SoundManager.play(SoundManager.BUTTON_CLICK);
                if (listener != null) listener.onSelectLevel(level);
            });
            panel.add(levelButtons[i]);
        }

        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor, int width, int height) {
        JButton button = new JButton(text) {
            private boolean hovering = false;
            private boolean pressing = false;

            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        pressing = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressing = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressing = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = baseColor;
                if (pressing) {
                    bgColor = bgColor.darker();
                } else if (hovering) {
                    bgColor = bgColor.brighter();
                }

                // Draw rounded button background
                RoundRectangle2D.Float bg = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 1, getHeight() - 1, 15, 15));

                // Background
                g2d.setColor(bgColor);
                g2d.fill(bg);

                // Border
                g2d.setColor(bgColor.darker());
                g2d.draw(bg);

                // Text
                g2d.setColor(GameTheme.BUTTON_TEXT);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(GameTheme.FONT_BUTTON);

        return button;
    }

    private void setupButtonActions() {
        playButton.addActionListener(e -> {
            SoundManager.play(SoundManager.BUTTON_CLICK);
            if (listener != null) listener.onPlayGame();
        });

        editorButton.addActionListener(e -> {
            SoundManager.play(SoundManager.BUTTON_CLICK);
            if (listener != null) listener.onLevelEditor();
        });
    }

    private void startAnimation() {
        animationTimer = new Timer(GameTheme.ANIMATION_FRAME_DELAY, e -> {
            animationFrame = (animationFrame + 1) % 4;
            repaint();
        });
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void setListener(StartScreenListener listener) {
        this.listener = listener;
    }

    /**
     * Enable/disable level buttons based on progress.
     */
    public void setLevelEnabled(int level, boolean enabled) {
        if (level >= 1 && level <= 3) {
            levelButtons[level - 1].setEnabled(enabled);
        }
    }
}

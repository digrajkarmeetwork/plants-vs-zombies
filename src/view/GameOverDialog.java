package view;

import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Styled game over dialog for win/lose states.
 */
public class GameOverDialog extends JDialog {

    public interface GameOverListener {
        void onNextLevel();
        void onTryAgain();
        void onMainMenu();
    }

    private GameOverListener listener;
    private boolean isVictory;

    public GameOverDialog(JFrame parent, boolean isVictory, int level) {
        super(parent, true);
        this.isVictory = isVictory;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Create main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintDialogBackground(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel(isVictory ? "VICTORY!" : "GAME OVER");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(isVictory ? GameTheme.ACCENT_GREEN : GameTheme.DANGER_RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        // Subtitle
        String subtitle = isVictory ?
            "Level " + level + " Complete!" :
            "The zombies got through...";
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(GameTheme.FONT_HEADING);
        subtitleLabel.setForeground(GameTheme.TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        if (isVictory) {
            // Next Level button (if not last level)
            if (level < 3) {
                JButton nextButton = createStyledButton("Next Level", GameTheme.ACCENT_GREEN);
                nextButton.addActionListener(e -> {
                    SoundManager.play(SoundManager.BUTTON_CLICK);
                    dispose();
                    if (listener != null) listener.onNextLevel();
                });
                buttonPanel.add(nextButton);
            }
        }

        // Try Again button
        JButton retryButton = createStyledButton("Try Again", GameTheme.ACCENT_BLUE);
        retryButton.addActionListener(e -> {
            SoundManager.play(SoundManager.BUTTON_CLICK);
            dispose();
            if (listener != null) listener.onTryAgain();
        });
        buttonPanel.add(retryButton);

        // Main Menu button
        JButton menuButton = createStyledButton("Main Menu", GameTheme.PANEL_ACCENT);
        menuButton.addActionListener(e -> {
            SoundManager.play(SoundManager.BUTTON_CLICK);
            dispose();
            if (listener != null) listener.onMainMenu();
        });
        buttonPanel.add(menuButton);

        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);

        // Play appropriate sound
        if (isVictory) {
            SoundManager.play(SoundManager.VICTORY);
        } else {
            SoundManager.play(SoundManager.DEFEAT);
        }
    }

    private void paintDialogBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        int arc = 20;

        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fill(new RoundRectangle2D.Float(5, 5, w - 1, h - 1, arc, arc));

        // Draw background
        RoundRectangle2D.Float bg = new RoundRectangle2D.Float(0, 0, w - 6, h - 6, arc, arc);

        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, GameTheme.PANEL_BACKGROUND,
            0, h, GameTheme.PANEL_SECONDARY
        );
        g2d.setPaint(gradient);
        g2d.fill(bg);

        // Border
        g2d.setColor(isVictory ? GameTheme.ACCENT_GREEN : GameTheme.DANGER_RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(bg);

        // Decorative line under title
        g2d.setColor(isVictory ? GameTheme.ACCENT_GREEN.darker() : GameTheme.DANGER_RED.darker());
        g2d.setStroke(new BasicStroke(2));
        int lineY = 80;
        int lineMargin = 50;
        g2d.drawLine(lineMargin, lineY, w - lineMargin - 6, lineY);

        g2d.dispose();
    }

    private JButton createStyledButton(String text, Color baseColor) {
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
                RoundRectangle2D.Float bg = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 1, getHeight() - 1, 10, 10));

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

        button.setPreferredSize(new Dimension(130, 40));
        button.setFont(GameTheme.FONT_BUTTON);

        return button;
    }

    public void setListener(GameOverListener listener) {
        this.listener = listener;
    }

    /**
     * Show a victory dialog.
     */
    public static void showVictory(JFrame parent, int level, GameOverListener listener) {
        GameOverDialog dialog = new GameOverDialog(parent, true, level);
        dialog.setListener(listener);
        dialog.setVisible(true);
    }

    /**
     * Show a game over dialog.
     */
    public static void showDefeat(JFrame parent, int level, GameOverListener listener) {
        GameOverDialog dialog = new GameOverDialog(parent, false, level);
        dialog.setListener(listener);
        dialog.setVisible(true);
    }
}

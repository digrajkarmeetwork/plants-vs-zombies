package view;

import model.GridObject;
import model.NullSpace;
import util.ResourceLoader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Thread-safe animation manager for entity sprites.
 * Handles continuous idle animations for all entities on the board.
 */
public class AnimationManager {

    // Map of buttons to their animation data
    private final Map<GridCellButton, AnimationData> animations = new ConcurrentHashMap<>();

    // Shared scheduler for all animations
    private ScheduledExecutorService scheduler;

    // Animation state
    private boolean running = false;
    private int globalFrame = 0;

    // Reference to the view for UI updates
    private final View view;

    /**
     * Animation data for a single entity.
     */
    private static class AnimationData {
        ImageIcon[] frames;
        String entityName;
        int currentFrame;

        AnimationData(String entityName) {
            this.entityName = entityName;
            this.currentFrame = 0;
            this.frames = new ImageIcon[4];

            // Load all animation frames
            for (int i = 0; i < 4; i++) {
                frames[i] = ResourceLoader.loadAnimationFrame(
                    entityName, i,
                    GameTheme.GRID_ICON_WIDTH,
                    GameTheme.GRID_ICON_HEIGHT
                );
            }
        }

        ImageIcon getNextFrame() {
            currentFrame = (currentFrame + 1) % 4;
            return frames[currentFrame];
        }

        ImageIcon getCurrentFrame() {
            return frames[currentFrame];
        }

        void sync(int globalFrame) {
            currentFrame = globalFrame % 4;
        }
    }

    public AnimationManager(View view) {
        this.view = view;
    }

    /**
     * No-arg constructor for standalone use.
     */
    public AnimationManager() {
        this.view = null;
    }

    /**
     * Stop all animations (alias for stop).
     */
    public void stopAllAnimations() {
        stop();
        clearAll();
    }

    /**
     * Register an entity for animation.
     */
    public void registerEntity(GridCellButton button, GridObject entity) {
        if (entity == null || entity instanceof NullSpace) {
            animations.remove(button);
            return;
        }

        String entityName = entity.getObjectTitle();

        // Check if already registered with same entity
        AnimationData existing = animations.get(button);
        if (existing != null && existing.entityName.equals(entityName)) {
            return; // Already registered
        }

        // Create new animation data
        AnimationData data = new AnimationData(entityName);
        data.sync(globalFrame); // Sync with global frame for smooth animation
        animations.put(button, data);

        // Set initial frame
        if (data.getCurrentFrame() != null) {
            SwingUtilities.invokeLater(() -> {
                button.setEntityIcon(data.getCurrentFrame());
            });
        }
    }

    /**
     * Unregister an entity from animation.
     */
    public void unregisterEntity(GridCellButton button) {
        animations.remove(button);
    }

    /**
     * Clear all animations.
     */
    public void clearAll() {
        animations.clear();
    }

    /**
     * Start the animation loop.
     */
    public void start() {
        if (running) return;

        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AnimationManager");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::advanceFrame,
            0, GameTheme.ANIMATION_FRAME_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop the animation loop.
     */
    public void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Advance all animations by one frame.
     */
    private void advanceFrame() {
        if (!running) return;

        globalFrame++;

        // Update all registered buttons
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<GridCellButton, AnimationData> entry : animations.entrySet()) {
                GridCellButton button = entry.getKey();
                AnimationData data = entry.getValue();

                ImageIcon nextFrame = data.getNextFrame();
                if (nextFrame != null && button.isVisible()) {
                    button.setEntityIcon(nextFrame);
                }
            }
        });
    }

    /**
     * Play a one-shot animation for an entity action (attack, damage, etc).
     */
    public void playActionAnimation(GridCellButton button, ActionType action) {
        if (button == null) return;

        switch (action) {
            case ATTACK:
                playAttackAnimation(button);
                break;
            case DAMAGE:
                playDamageAnimation(button);
                break;
            case SPAWN:
                playSpawnAnimation(button);
                break;
            case PLACE:
                playPlaceAnimation(button);
                break;
        }
    }

    private void playAttackAnimation(GridCellButton button) {
        // Quick flash and scale effect
        button.flash(GameTheme.SUN_GOLD, 100);

        // Could add more complex animation here
    }

    private void playDamageAnimation(GridCellButton button) {
        // Red flash
        button.flashDanger();
    }

    private void playSpawnAnimation(GridCellButton button) {
        // Danger flash for zombie spawn
        button.flashDanger();
    }

    private void playPlaceAnimation(GridCellButton button) {
        // Green flash for plant placement
        button.flashPlantPlaced();
    }

    /**
     * Check if animations are running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get number of animated entities.
     */
    public int getAnimatedCount() {
        return animations.size();
    }

    /**
     * Action types for one-shot animations.
     */
    public enum ActionType {
        ATTACK,
        DAMAGE,
        SPAWN,
        PLACE
    }
}

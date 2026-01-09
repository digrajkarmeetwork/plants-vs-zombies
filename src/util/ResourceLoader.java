package util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * JAR-compatible resource loader with caching.
 * Uses ClassLoader to load resources that work both in IDE and packaged JAR.
 */
public class ResourceLoader {

    // Cache for loaded and scaled images
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    /**
     * Load an image icon from resources folder.
     * Works both in IDE and when packaged as JAR.
     *
     * @param filename Name of the file (e.g., "SunFlower.png")
     * @return ImageIcon or null if not found
     */
    public static ImageIcon loadIcon(String filename) {
        String key = filename;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }

        try {
            URL url = ResourceLoader.class.getResource("/resources/" + filename);
            if (url == null) {
                // Fallback: try without leading slash
                url = ResourceLoader.class.getClassLoader().getResource("resources/" + filename);
            }
            if (url == null) {
                // Fallback: try relative path (for IDE)
                java.io.File file = new java.io.File("resources/" + filename);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            }
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                imageCache.put(key, icon);
                return icon;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + filename + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Load and scale an image icon.
     *
     * @param filename Name of the file
     * @param width    Desired width
     * @param height   Desired height
     * @return Scaled ImageIcon or null if not found
     */
    public static ImageIcon loadScaledIcon(String filename, int width, int height) {
        String key = filename + "_" + width + "x" + height;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }

        ImageIcon original = loadIcon(filename);
        if (original != null) {
            Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);
            imageCache.put(key, scaledIcon);
            return scaledIcon;
        }
        return null;
    }

    /**
     * Load entity image (plant or zombie) at grid size.
     *
     * @param entityName Name of entity (e.g., "SunFlower", "GenericZombie")
     * @return Scaled ImageIcon for grid display
     */
    public static ImageIcon loadEntityIcon(String entityName) {
        return loadScaledIcon(entityName + ".png", 80, 60);
    }

    /**
     * Load entity image at card size (for plant selection).
     *
     * @param entityName Name of entity
     * @return Scaled ImageIcon for card display
     */
    public static ImageIcon loadCardIcon(String entityName) {
        return loadScaledIcon(entityName + ".png", 50, 50);
    }

    /**
     * Load animation frame for an entity.
     *
     * @param entityName Name of entity
     * @param frameIndex Frame index (0 = static, 1-3 = animated)
     * @param width      Desired width
     * @param height     Desired height
     * @return Scaled ImageIcon for animation frame
     */
    public static ImageIcon loadAnimationFrame(String entityName, int frameIndex, int width, int height) {
        String filename;
        if (frameIndex == 0) {
            filename = entityName + ".png";
        } else {
            filename = entityName + "Animated" + frameIndex + ".png";
        }
        return loadScaledIcon(filename, width, height);
    }

    /**
     * Preload all images for an entity (static + 3 animation frames).
     *
     * @param entityName Name of entity
     * @param width      Desired width
     * @param height     Desired height
     */
    public static void preloadEntity(String entityName, int width, int height) {
        for (int i = 0; i <= 3; i++) {
            loadAnimationFrame(entityName, i, width, height);
        }
    }

    /**
     * Preload all game entities.
     */
    public static void preloadAllEntities() {
        String[] plants = {"SunFlower", "VenusFlyTrap", "Walnut", "Potatoe", "PeaShooter"};
        String[] zombies = {"GenericZombie", "FrankTheTank", "BurrowingBailey"};

        for (String plant : plants) {
            preloadEntity(plant, 80, 60);
            preloadEntity(plant, 50, 50);
        }
        for (String zombie : zombies) {
            preloadEntity(zombie, 80, 60);
        }
    }

    /**
     * Clear the image cache (for memory management).
     */
    public static void clearCache() {
        imageCache.clear();
    }

    /**
     * Get URL for a resource file.
     *
     * @param path Path relative to resources folder
     * @return URL or null if not found
     */
    public static URL getResourceURL(String path) {
        URL url = ResourceLoader.class.getResource("/resources/" + path);
        if (url == null) {
            url = ResourceLoader.class.getClassLoader().getResource("resources/" + path);
        }
        if (url == null) {
            try {
                java.io.File file = new java.io.File("resources/" + path);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return url;
    }
}

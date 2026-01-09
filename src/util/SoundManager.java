package util;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Sound effect manager for the game.
 * Handles loading and playing WAV audio files.
 */
public class SoundManager {

    // Sound effect names
    public static final String PLANT_PLACE = "plant_place.wav";
    public static final String ZOMBIE_GROAN = "zombie_groan.wav";
    public static final String CHOMP = "chomp.wav";
    public static final String SUN_COLLECT = "sun_collect.wav";
    public static final String VICTORY = "victory.wav";
    public static final String DEFEAT = "defeat.wav";
    public static final String BUTTON_CLICK = "button_click.wav";
    public static final String PLANT_ATTACK = "plant_attack.wav";

    // Cache for loaded audio clips
    private static final Map<String, Clip> clipCache = new HashMap<>();

    // Sound enabled flag
    private static boolean soundEnabled = true;

    // Volume (0.0 to 1.0)
    private static float volume = 0.7f;

    /**
     * Play a sound effect.
     *
     * @param soundName Name of the sound file (e.g., "plant_place.wav")
     */
    public static void play(String soundName) {
        if (!soundEnabled) return;

        try {
            // Try to get from cache first
            Clip clip = getClip(soundName);
            if (clip != null) {
                // Reset to beginning
                clip.setFramePosition(0);

                // Set volume
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(Math.max(dB, gainControl.getMinimum()));
                }

                clip.start();
            }
        } catch (Exception e) {
            // Silent fail - don't crash game for audio issues
            System.err.println("Failed to play sound: " + soundName + " - " + e.getMessage());
        }
    }

    /**
     * Play a sound effect asynchronously (doesn't block).
     *
     * @param soundName Name of the sound file
     */
    public static void playAsync(String soundName) {
        if (!soundEnabled) return;

        new Thread(() -> {
            try {
                URL url = getSoundURL(soundName);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);

                    // Set volume
                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                        gainControl.setValue(Math.max(dB, gainControl.getMinimum()));
                    }

                    clip.start();

                    // Clean up when done
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (Exception e) {
                // Silent fail
            }
        }).start();
    }

    /**
     * Get or load a clip from cache.
     */
    private static Clip getClip(String soundName) {
        if (clipCache.containsKey(soundName)) {
            Clip cached = clipCache.get(soundName);
            if (cached != null && cached.isOpen()) {
                return cached;
            }
        }

        try {
            URL url = getSoundURL(soundName);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clipCache.put(soundName, clip);
                return clip;
            }
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + soundName + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Get URL for a sound file.
     */
    private static URL getSoundURL(String soundName) {
        // Try multiple paths for compatibility
        URL url = SoundManager.class.getResource("/sounds/" + soundName);
        if (url == null) {
            url = SoundManager.class.getClassLoader().getResource("sounds/" + soundName);
        }
        if (url == null) {
            try {
                java.io.File file = new java.io.File("sounds/" + soundName);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return url;
    }

    /**
     * Preload all game sounds into cache.
     */
    public static void preloadSounds() {
        String[] sounds = {
            PLANT_PLACE, ZOMBIE_GROAN, CHOMP, SUN_COLLECT,
            VICTORY, DEFEAT, BUTTON_CLICK, PLANT_ATTACK
        };
        for (String sound : sounds) {
            getClip(sound);
        }
    }

    /**
     * Enable or disable sound effects.
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    /**
     * Check if sound is enabled.
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Set the volume level.
     *
     * @param vol Volume from 0.0 (silent) to 1.0 (full)
     */
    public static void setVolume(float vol) {
        volume = Math.max(0.0f, Math.min(1.0f, vol));
    }

    /**
     * Get current volume level.
     */
    public static float getVolume() {
        return volume;
    }

    /**
     * Toggle sound on/off.
     */
    public static void toggleSound() {
        soundEnabled = !soundEnabled;
    }

    /**
     * Stop all currently playing sounds.
     */
    public static void stopAll() {
        for (Clip clip : clipCache.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    /**
     * Clean up resources.
     */
    public static void cleanup() {
        for (Clip clip : clipCache.values()) {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
        clipCache.clear();
    }
}

package net.treasure.particles.gui.config;

import net.treasure.particles.constants.Patterns;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.entity.Player;

public class GUISounds {

    public static Pair<String, float[]> NEXT_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> PREVIOUS_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> RESET = new Pair<>("", new float[]{1, 1});
    public static Pair<String, float[]> FILTER = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});
    // Effects GUI
    public static Pair<String, float[]> SELECT_EFFECT = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> RANDOM_EFFECT = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> OPEN_MIXER_GUI = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});
    // Colors GUI
    public static Pair<String, float[]> SELECT_COLOR = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> RANDOM_COLOR = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> BACK = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    // Mixer GUI
    public static Pair<String, float[]> MIXER_SELECT_EFFECT = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});
    public static Pair<String, float[]> MIXER_UNSELECT_EFFECT = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});
    public static Pair<String, float[]> CONFIRM = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    // Handlers GUI
    public static Pair<String, float[]> SELECT_HANDLER = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});
    public static Pair<String, float[]> UNSELECT_HANDLER = new Pair<>("minecraft:ui.button.click", new float[]{0.1f, 1.5f});

    public static void play(Player player, Pair<String, float[]> sound) {
        if (sound == null) return;
        player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
    }

    private GUIManager manager;

    public void initialize(GUIManager manager) {
        this.manager = manager;

        NEXT_PAGE = getSound("next-page", NEXT_PAGE);
        PREVIOUS_PAGE = getSound("previous-page", PREVIOUS_PAGE);
        RESET = getSound("reset", RESET);
        FILTER = getSound("filter", FILTER);
        // Effects GUI
        SELECT_EFFECT = getSound("effects-gui.select-effect", SELECT_EFFECT);
        RANDOM_EFFECT = getSound("effects-gui.random-effect", RANDOM_EFFECT);
        OPEN_MIXER_GUI = getSound("effects-gui.open-mixer-gui", OPEN_MIXER_GUI);
        // Colors GUI
        SELECT_COLOR = getSound("colors-gui.select-color", SELECT_COLOR);
        RANDOM_COLOR = getSound("colors-gui.random-color", RANDOM_COLOR);
        BACK = getSound("colors-gui.back", BACK);
        // Mixer GUI
        MIXER_SELECT_EFFECT = getSound("mixer-gui.select-effect", MIXER_SELECT_EFFECT);
        MIXER_UNSELECT_EFFECT = getSound("mixer-gui.unselect-effect", MIXER_UNSELECT_EFFECT);
        CONFIRM = getSound("mixer-gui.confirm", CONFIRM);
        // Handlers GUI
        SELECT_HANDLER = getSound("handlers-gui.select-handler", SELECT_HANDLER);
        UNSELECT_HANDLER = getSound("handlers-gui.unselect-handler", UNSELECT_HANDLER);
    }

    private Pair<String, float[]> getSound(String key, Pair<String, float[]> defaultValue) {
        var raw = manager.getConfig().getString("sounds." + key);
        if (raw == null) {
            ComponentLogger.error(manager.getGenerator(), "There is no sound for \"" + key + "\"");
            return defaultValue;
        }
        try {
            var args = Patterns.SPACE.split(raw);
            var sound = args[0];
            var volume = Float.parseFloat(args[1]);
            var pitch = Float.parseFloat(args[2]);
            return new Pair<>(sound, new float[]{volume, pitch});
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
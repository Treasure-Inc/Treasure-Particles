package net.treasure.core.gui.config;

import net.treasure.common.Patterns;
import net.treasure.core.gui.GUIManager;
import net.treasure.util.tuples.Pair;

public class GUISounds {

    public static Pair<String, float[]> NEXT_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> PREVIOUS_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> RESET = new Pair<>("", new float[]{1, 1});
    public static Pair<String, float[]> SELECT_EFFECT = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> RANDOM_EFFECT = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    // Colors GUI
    public static Pair<String, float[]> SELECT_COLOR = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});
    public static Pair<String, float[]> RANDOM_COLOR = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});

    private GUIManager manager;

    public void initialize(GUIManager manager) {
        this.manager = manager;

        NEXT_PAGE = getSound("next-page", NEXT_PAGE);
        PREVIOUS_PAGE = getSound("previous-page", PREVIOUS_PAGE);
        RESET = getSound("reset", RESET);
        SELECT_EFFECT = getSound("select-effect", SELECT_EFFECT);
        RANDOM_EFFECT = getSound("random-effect", RANDOM_EFFECT);
        // Colors GUI
        SELECT_COLOR = getSound("select-color", SELECT_COLOR);
        RANDOM_COLOR = getSound("random-color", RANDOM_COLOR);
    }

    private Pair<String, float[]> getSound(String key, Pair<String, float[]> defaultValue) {
        try {
            var args = Patterns.SPACE.split(manager.getConfig().getString("sounds." + key));
            var sound = args[0];
            var volume = Float.parseFloat(args[1]);
            var pitch = Float.parseFloat(args[2]);
            return new Pair<>(sound, new float[]{volume, pitch});
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
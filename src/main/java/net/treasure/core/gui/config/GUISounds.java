package net.treasure.core.gui.config;

import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.Pair;
import org.bukkit.configuration.file.FileConfiguration;

public class GUISounds implements DataHolder {

    public static Pair<String, float[]> NEXT_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> PREVIOUS_PAGE = new Pair<>("minecraft:item.book.page_turn", new float[]{1, 1});
    public static Pair<String, float[]> RESET = new Pair<>("", new float[]{1, 1});
    public static Pair<String, float[]> SELECT = new Pair<>("minecraft:block.note_block.pling", new float[]{1, 2});

    FileConfiguration config;

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        config = TreasurePlugin.getInstance().getConfig();

        NEXT_PAGE = getSound("next-page", NEXT_PAGE);
        PREVIOUS_PAGE = getSound("previous-page", PREVIOUS_PAGE);
        RESET = getSound("reset", RESET);
        SELECT = getSound("select", SELECT);
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    private Pair<String, float[]> getSound(String key, Pair<String, float[]> defaultValue) {
        try {
            var args = Patterns.SPACE.split(config.getString("gui.sounds." + key));
            var sound = args[0];
            var volume = Float.parseFloat(args[1]);
            var pitch = Float.parseFloat(args[2]);
            return new Pair<>(sound, new float[]{volume, pitch});
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
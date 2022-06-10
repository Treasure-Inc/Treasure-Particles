package net.treasure.core.command.gui;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.Pair;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class GUIElements implements DataHolder {

    public static Material BORDERS = Material.BLACK_STAINED_GLASS_PANE;
    public static Material DEFAULT_ICON = Material.LEATHER_BOOTS;
    public static Pair<Integer, Material> NEXT_PAGE = new Pair<>(53, Material.ENDER_PEARL);
    public static Pair<Integer, Material> PREVIOUS_PAGE = new Pair<>(45, Material.ENDER_EYE);
    public static Pair<Integer, Material> CLOSE = new Pair<>(49, Material.BARRIER);
    public static Pair<Integer, Material> RESET = new Pair<>(50, Material.RED_STAINED_GLASS_PANE);

    FileConfiguration config;

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        config = TreasurePlugin.getInstance().getConfig();

        BORDERS = getMaterial("borders", BORDERS);
        DEFAULT_ICON = getMaterial("default-icon", DEFAULT_ICON);

        NEXT_PAGE = new Pair<>(getSlot("next-page.slot", NEXT_PAGE.getKey()), getMaterial("next-page.item", NEXT_PAGE.getValue()));
        PREVIOUS_PAGE = new Pair<>(getSlot("previous-page.slot", PREVIOUS_PAGE.getKey()), getMaterial("previous-page.item", PREVIOUS_PAGE.getValue()));
        CLOSE = new Pair<>(getSlot("close.slot", CLOSE.getKey()), getMaterial("close.item", CLOSE.getValue()));
        RESET = new Pair<>(getSlot("reset.slot", RESET.getKey()), getMaterial("reset.item", RESET.getValue()));
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    private Material getMaterial(String key, Material defaultValue) {
        try {
            //noinspection ConstantConditions
            return Material.valueOf(config.getString("gui." + key).toUpperCase(Locale.ENGLISH));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    private int getSlot(String key, int defaultValue) {
        try {
            return config.getInt("gui." + key);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
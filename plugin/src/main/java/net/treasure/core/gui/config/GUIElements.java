package net.treasure.core.gui.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.GUIManager;
import net.treasure.core.gui.type.GUI;
import net.treasure.util.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GUIElements {

    private static GUIManager manager;
    private static String path;

    public void initialize(GUIManager manager) {
        GUIElements.manager = manager;
        path = "styles." + manager.getStyle().id + ".";
    }

    public static Info info(GUI gui, ElementType type, char key, ItemStack item) {
        return new Info(getKey(gui, type, key), getItemStack(gui, type, item)).slots(manager.getStyle().layouts.get(gui));
    }

    private static char getKey(GUI gui, ElementType type, char defaultValue) {
        try {
            return manager.getConfig().getString(path + gui.id() + ".elements." + type.id + ".key").charAt(0);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    private static ItemStack getItemStack(GUI gui, ElementType buttonType, ItemStack defaultValue) {
        return getItemStack(manager.getConfig(), path + gui.id() + ".elements." + buttonType.id() + ".item", defaultValue);
    }

    public static ItemStack getItemStack(FileConfiguration config, String path, ItemStack defaultValue) {
        try {
            var section = config.getConfigurationSection(path);
            if (section == null) return defaultValue;
            //noinspection ConstantConditions
            var material = Material.getMaterial(section.getString("material", "AIR").toUpperCase(Locale.ENGLISH));
            var customModelData = section.getInt("custom-model-data", 0);
            var amount = section.getInt("amount", 1);
            var glow = section.getBoolean("glow", false);
            return new CustomItem(material)
                    .setAmount(amount)
                    .setCustomModelData(customModelData)
                    .glow(glow)
                    .build();
        } catch (Exception e) {
            if (TreasurePlugin.getInstance().isDebugModeEnabled())
                e.printStackTrace();
            return defaultValue;
        }
    }

    private static int[] layout(String[] array, char c) {
        List<Integer> borders = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            var s = array[i];
            var chars = s.toCharArray();
            for (int j = 0; j < chars.length; j++)
                if (chars[j] == c)
                    borders.add(i * 9 + j);
        }
        return borders.stream().mapToInt(value -> value).toArray();
    }

    @Getter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    public static class Info {
        final char key;
        final ItemStack item;
        int[] slots;

        public Info slots(String[] layout) {
            this.slots = layout(layout, key);
            return this;
        }

        public boolean isEnabled() {
            return slots != null && slots.length > 0;
        }
    }
}
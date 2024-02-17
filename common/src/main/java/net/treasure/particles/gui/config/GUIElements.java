package net.treasure.particles.gui.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.gui.type.GUIType;
import net.treasure.particles.util.item.CustomItem;
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
        path = "styles." + manager.getStyle().getId() + ".";
    }

    public static ElementInfo element(GUIType gui, ElementType type, char key, ItemStack item) {
        return new ElementInfo(getKey(gui, type, key), getItemStack(gui, type, item)).slots(manager.getStyle().getLayouts().get(gui).getRows());
    }

    private static char getKey(GUIType gui, ElementType type, char defaultValue) {
        try {
            return manager.getConfig().getString(path + gui.id() + ".elements." + type.id() + ".key").charAt(0);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static ItemStack getItemStack(GUIType gui, ElementType buttonType, ItemStack defaultValue) {
        return getItemStack(manager.getConfig(), path + gui.id() + ".elements." + buttonType.id() + ".item", defaultValue);
    }

    public static ItemStack getItemStack(GUIType gui, String buttonId, ItemStack defaultValue) {
        return getItemStack(manager.getConfig(), path + gui.id() + ".elements." + buttonId + ".item", defaultValue);
    }

    public static ItemStack getItemStack(FileConfiguration config, String path, ItemStack defaultValue) {
        try {
            var section = config.getConfigurationSection(path);
            if (section == null) return defaultValue;
            //noinspection ConstantConditions
            var material = Material.getMaterial(section.getString("material", "AIR").toUpperCase(Locale.ENGLISH));
            var playerHeadName = section.getString("player-head-name");
            var playerHeadTexture = section.getString("player-head-texture");
            var customModelData = section.getInt("custom-model-data", 0);
            var amount = section.getInt("amount", 1);
            var glow = section.getBoolean("glow", false);
            return new CustomItem(material)
                    .setAmount(amount)
                    .setPlayerHeadName(playerHeadName)
                    .setPlayerHeadTexture(playerHeadTexture)
                    .setCustomModelData(customModelData)
                    .glow(glow)
                    .build();
        } catch (Exception e) {
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
    public static class ElementInfo {
        private final char key;
        private final ItemStack item;
        private int[] slots;

        public ElementInfo slots(String[] layout) {
            this.slots = layout(layout, key);
            return this;
        }

        public boolean isEnabled() {
            return slots != null && slots.length > 0;
        }
    }
}
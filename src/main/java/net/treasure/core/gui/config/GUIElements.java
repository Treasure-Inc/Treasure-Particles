package net.treasure.core.gui.config;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.Pair;
import net.treasure.util.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class GUIElements implements DataHolder {

    public static ItemStack BORDERS = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    public static ItemStack DEFAULT_ICON = new ItemStack(Material.LEATHER_BOOTS);
    public static Pair<Integer, ItemStack> NEXT_PAGE = new Pair<>(53, new ItemStack(Material.ENDER_PEARL));
    public static Pair<Integer, ItemStack> PREVIOUS_PAGE = new Pair<>(45, new ItemStack(Material.ENDER_EYE));
    public static Pair<Integer, ItemStack> CLOSE = new Pair<>(49, new ItemStack(Material.BARRIER));
    public static Pair<Integer, ItemStack> RESET = new Pair<>(50, new ItemStack(Material.RED_STAINED_GLASS_PANE));

    private FileConfiguration config;

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        config = TreasurePlugin.getInstance().getConfig();

        BORDERS = getItemStack("borders", BORDERS);
        DEFAULT_ICON = getItemStack("default-icon", DEFAULT_ICON);

        NEXT_PAGE = new Pair<>(getSlot("next-page.slot", NEXT_PAGE.getKey()), getItemStack("next-page.item", NEXT_PAGE.getValue()));
        PREVIOUS_PAGE = new Pair<>(getSlot("previous-page.slot", PREVIOUS_PAGE.getKey()), getItemStack("previous-page.item", PREVIOUS_PAGE.getValue()));
        CLOSE = new Pair<>(getSlot("close.slot", CLOSE.getKey()), getItemStack("close.item", CLOSE.getValue()));
        RESET = new Pair<>(getSlot("reset.slot", RESET.getKey()), getItemStack("reset.item", RESET.getValue()));
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    private ItemStack getItemStack(String path, ItemStack defaultValue) {
        return getItemStack(config, "gui.elements." + path, defaultValue);
    }

    public static ItemStack getItemStack(FileConfiguration config, String path, ItemStack defaultValue) {
        try {
            var section = config.getConfigurationSection(path);
            if (section == null) return defaultValue;
            //noinspection ConstantConditions
            var material = Material.getMaterial(section.getString("material").toUpperCase(Locale.ENGLISH));
            var customModelData = section.getInt("customModelData", 0);
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

    private int getSlot(String path, int defaultValue) {
        try {
            return config.getInt("gui.elements." + path);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
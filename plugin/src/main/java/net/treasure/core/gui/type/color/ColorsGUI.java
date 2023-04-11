package net.treasure.core.gui.type.color;

import net.treasure.color.data.RGBColorData;
import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.GUIManager;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.config.GUIElements;
import net.treasure.core.gui.task.GUITask;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static net.treasure.core.gui.type.GUI.COLORS;

public class ColorsGUI {

    private static GUIManager manager;
    private static PlayerManager playerManager;

    public static GUIElements.Info BORDERS;
    public static GUIElements.Info COLOR_ICON;
    public static GUIElements.Info NEXT_PAGE;
    public static GUIElements.Info PREVIOUS_PAGE;
    public static GUIElements.Info RANDOM_COLOR;
    public static GUIElements.Info BACK;

    public static void configure(GUIManager manager) {
        ColorsGUI.manager = manager;
        playerManager = TreasurePlugin.getInstance().getPlayerManager();
    }

    public static void setItems() {
        BORDERS = GUIElements.info(COLORS, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        COLOR_ICON = GUIElements.info(COLORS, ElementType.COLOR_ICON, 'C', new ItemStack(Material.LEATHER_HORSE_ARMOR));

        NEXT_PAGE = GUIElements.info(COLORS, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.info(COLORS, ElementType.PREVIOUS_PAGE, 'P', new ItemStack(Material.ENDER_EYE));
        RANDOM_COLOR = GUIElements.info(COLORS, ElementType.RANDOM_COLOR, 'R', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
        BACK = GUIElements.info(COLORS, ElementType.BACK, 'b', new ItemStack(Material.STRUCTURE_VOID));
    }

    public static void open(Player player, Effect effect, int page) {
        // Variables
        var data = playerManager.getEffectData(player);
        var preference = data.getColorPreferences().get(effect.getKey());
        var style = manager.getStyle();

        // Create inventory
        var holder = new ColorsGUIHolder(effect);
        var inventory = Bukkit.createInventory(holder, style.getSize(), MessageUtils.parseLegacy(Translations.COLORS_GUI_TITLE));
        holder.setInventory(inventory);
        holder.setPage(page);

        // Style
        style.getType().getDecoration().accept(style, inventory);

        // Colors
        var colors = effect.getColorGroup().getAvailableOptions();

        // Back button
        if (BACK.isEnabled())
            for (int slot : BACK.slots())
                inventory.setItem(slot, new CustomItem(BACK.item())
                        .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_BACK))
                        .addData(Keys.BUTTON_TYPE, ElementType.BACK.name())
                        .build());

        // Previous page button
        if (page > 0) {
            if (PREVIOUS_PAGE.isEnabled())
                for (int slot : PREVIOUS_PAGE.slots())
                    inventory.setItem(slot, new CustomItem(PREVIOUS_PAGE.item())
                            .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_PREVIOUS_PAGE))
                            .addData(Keys.BUTTON_TYPE, ElementType.PREVIOUS_PAGE.name())
                            .build());
        } else if (BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        var colorSlots = COLOR_ICON.slots();
        var maxColors = colorSlots.length;

        // Next page button
        if ((page + 1) * maxColors < colors.size()) {
            if (NEXT_PAGE.isEnabled())
                for (int slot : NEXT_PAGE.slots())
                    inventory.setItem(slot, new CustomItem(NEXT_PAGE.item())
                            .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_NEXT_PAGE))
                            .addData(Keys.BUTTON_TYPE, ElementType.NEXT_PAGE.name())
                            .build());
        } else if (BORDERS.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        HashMap<Integer, RGBColorData> animatedSlots = new HashMap<>();

        int index = 0;
        for (int i = page * maxColors; i < (page + 1) * maxColors; i++) {
            if (colors.size() <= i) break;
            int where = colorSlots[index];

            var option = colors.get(i);
            var scheme = option.colorScheme();

            var colorData = new RGBColorData(scheme, manager.getColorCycleSpeed(), true, false);
            animatedSlots.put(where, colorData);
            var color = colorData.next(null);

            inventory.setItem(where, new CustomItem(COLOR_ICON.item())
                    .setDisplayName(ChatColor.WHITE + MessageUtils.parseLegacy(scheme.getDisplayName()))
                    .addLore(MessageUtils.parseLegacy(scheme.equals(preference) ? Translations.COLORS_GUI_SCHEME_SELECTED : Translations.COLORS_GUI_SELECT_SCHEME))
                    .addLore(scheme.equals(preference) ? null : MessageUtils.parseLegacy(Translations.COLORS_GUI_SAVE_SCHEME))
                    .changeArmorColor(color)
                    .addData(Keys.COLOR, scheme.getKey())
                    .addItemFlags(ItemFlag.values())
                    .build());
            index += 1;
        }

        holder.setAnimatedSlots(animatedSlots);
        GUITask.getPlayers().add(player.getUniqueId());

        player.openInventory(inventory);
    }
}

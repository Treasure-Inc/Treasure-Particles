package net.treasure.core.gui.type.effects;

import net.treasure.color.ColorManager;
import net.treasure.color.data.RGBColorData;
import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.GUIManager;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.config.GUIElements;
import net.treasure.core.gui.task.GUITask;
import net.treasure.core.gui.type.effects.listener.EffectsGUIListener;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.EffectManager;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

import static net.treasure.core.gui.type.GUI.EFFECTS;

public class EffectsGUI {

    private static GUIManager manager;
    private static EffectManager effectManager;
    private static PlayerManager playerManager;
    private static ColorManager colorManager;
    private static Translations translations;

    public static GUIElements.Info BORDERS;
    public static GUIElements.Info NEXT_PAGE;
    public static GUIElements.Info PREVIOUS_PAGE;
    public static GUIElements.Info DEFAULT_ICON;
    public static GUIElements.Info RANDOM_EFFECT;
    public static GUIElements.Info RESET;
    public static GUIElements.Info CLOSE;
    public static GUIElements.Info FILTER;

    public static void configure(GUIManager manager) {
        EffectsGUI.manager = manager;

        var inst = TreasurePlugin.getInstance();
        effectManager = inst.getEffectManager();
        playerManager = inst.getPlayerManager();
        colorManager = inst.getColorManager();
        translations = inst.getTranslations();

        Bukkit.getPluginManager().registerEvents(new EffectsGUIListener(), inst);
    }

    public static void setItems() {
        BORDERS = GUIElements.info(EFFECTS, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        NEXT_PAGE = GUIElements.info(EFFECTS, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.info(EFFECTS, ElementType.PREVIOUS_PAGE, 'N', new ItemStack(Material.ENDER_EYE));

        DEFAULT_ICON = GUIElements.info(EFFECTS, ElementType.DEFAULT_ICON, 'E', new ItemStack(Material.LEATHER_BOOTS));
        RANDOM_EFFECT = GUIElements.info(EFFECTS, ElementType.RANDOM_EFFECT, 'r', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));

        RESET = GUIElements.info(EFFECTS, ElementType.RESET, 'R', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        CLOSE = GUIElements.info(EFFECTS, ElementType.CLOSE, 'C', new ItemStack(Material.BARRIER));
        FILTER = GUIElements.info(EFFECTS, ElementType.FILTER, 'F', new ItemStack(Material.HOPPER));
    }

    public static void open(Player player, HandlerEvent filter, int page) {
        // Variables
        var data = playerManager.getEffectData(player);
        var style = manager.getStyle();

        // Create inventory
        var holder = new EffectsGUIHolder(filter);
        var inventory = Bukkit.createInventory(holder, style.getSize(), MessageUtils.parseLegacy(manager.getStyle().getTitle()));
        holder.setInventory(inventory);
        holder.setPage(page);

        // Borders
        if (BORDERS.isEnabled())
            for (int slot : BORDERS.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        // Effects
        var effects = effectManager.getEffects().stream().filter(effect -> (filter == null || effect.getEvents().contains(filter)) && effect.canUse(player)).toList();

        // Filter button
        if (FILTER.isEnabled())
            for (int slot : FILTER.slots())
                inventory.setItem(slot, new CustomItem(FILTER.item())
                        .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_FILTER))
                        .setLore(Arrays.stream(HandlerEvent.values()).map(event -> MessageUtils.parseLegacy("<dark_gray> • <" + (event.equals(filter) ? "green" : "gray") + ">" + translations.get("events." + event.translationKey()))).toList())
                        .addData(Keys.BUTTON_TYPE, ElementType.FILTER.name())
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

        // Reset effect button
        if (data.getCurrentEffect() != null) {
            if (RESET.isEnabled())
                for (int slot : RESET.slots())
                    inventory.setItem(slot, new CustomItem(RESET.item())
                            .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_RESET_EFFECT))
                            .addLore(MessageUtils.parseLegacy(Translations.GUI_RESET_EFFECT_CURRENT, data.getCurrentEffect().getDisplayName()))
                            .addData(Keys.BUTTON_TYPE, ElementType.RESET.name())
                            .build());
        } else if (RESET.isEnabled() && BORDERS.isEnabled())
            for (int slot : RESET.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        // Random effect button
        if (!effects.isEmpty()) {
            if (RANDOM_EFFECT.isEnabled())
                for (int slot : RANDOM_EFFECT.slots())
                    inventory.setItem(slot, new CustomItem(RANDOM_EFFECT.item())
                            .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_RANDOM_EFFECT))
                            .addData(Keys.BUTTON_TYPE, ElementType.RANDOM_EFFECT.name())
                            .build());
        } else if (RANDOM_EFFECT.isEnabled() && BORDERS.isEnabled())
            for (int slot : RANDOM_EFFECT.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        // Next page button
        if ((page + 1) * maxEffects < effects.size()) {
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

        var colorCycleSpeed = manager.getColorCycleSpeed();
        HashMap<Integer, RGBColorData> animatedSlots = null;

        int index = 0;
        for (int i = page * maxEffects; i < (page + 1) * maxEffects; i++) {
            if (effects.size() <= i) break;

            int where = effectSlots[index];

            var effect = effects.get(i);
            Color color = null;
            var colorGroup = effect.getColorGroup();

            if (effect.getArmorColor() != null) {
                if (animatedSlots == null) animatedSlots = new HashMap<>();

                var scheme = colorManager.getColorScheme(effect.getArmorColor());
                if (scheme != null) {
                    var colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                    animatedSlots.put(where, colorData);
                    color = colorData.next(null);
                } else {
                    try {
                        int hex = Integer.decode(effect.getArmorColor());
                        color = Color.fromRGB((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
                    } catch (Exception ignored) {
                        TreasurePlugin.logger().warning(effect.getPrefix() + "Unknown armor color value: " + effect.getArmorColor());
                    }
                }
            } else if (colorGroup != null) {
                var preference = data.getColorPreference(effect);
                var scheme = preference == null ? colorGroup.getAvailableOptions().get(0).colorScheme() : preference;
                var colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                color = colorData.next(null);
            }

            inventory.setItem(where, new CustomItem(effect.getIcon())
                    .setDisplayName(ChatColor.WHITE + MessageUtils.parseLegacy(effect.getDisplayName()))
                    .addLore(effect.getDescription())
                    .addLore(effect.getDescription() != null ? ChatColor.AQUA.toString() : null)
                    .addLore(MessageUtils.parseLegacy(effect.equals(data.getCurrentEffect()) ? Translations.GUI_EFFECT_SELECTED : Translations.GUI_SELECT_EFFECT))
                    .addLore(colorGroup != null ? MessageUtils.parseLegacy(Translations.COLOR_SELECTION_AVAILABLE) : null)
                    .changeArmorColor(color)
                    .addData(Keys.EFFECT, effect.getKey())
                    .glow(effect.equals(data.getCurrentEffect()))
                    .addItemFlags(ItemFlag.values())
                    .build());
            index += 1;
        }

        player.openInventory(inventory);

        if (animatedSlots != null) {
            holder.setAnimatedSlots(animatedSlots);
            GUITask.getPlayers().add(player);
        }
    }
}
package net.treasure.core.gui.type.admin;

import net.treasure.color.ColorManager;
import net.treasure.color.data.RGBColorData;
import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.GUIManager;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.task.GUITask;
import net.treasure.core.gui.type.admin.listener.AdminGUIListener;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.EffectManager;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static net.treasure.core.gui.type.effects.EffectsGUI.BORDERS;
import static net.treasure.core.gui.type.effects.EffectsGUI.DEFAULT_ICON;
import static net.treasure.core.gui.type.effects.EffectsGUI.FILTER;
import static net.treasure.core.gui.type.effects.EffectsGUI.NEXT_PAGE;
import static net.treasure.core.gui.type.effects.EffectsGUI.PREVIOUS_PAGE;

public class AdminGUI {

    private static GUIManager manager;
    private static EffectManager effectManager;
    private static PlayerManager playerManager;
    private static ColorManager colorManager;
    private static Translations translations;

    public static void configure(GUIManager manager) {
        AdminGUI.manager = manager;

        var inst = TreasurePlugin.getInstance();
        effectManager = inst.getEffectManager();
        playerManager = inst.getPlayerManager();
        colorManager = inst.getColorManager();
        translations = inst.getTranslations();

        Bukkit.getPluginManager().registerEvents(new AdminGUIListener(), inst);
    }

    public static void open(Player player, FilterCategory filterCategory, HandlerEvent filterEvent, int page) {
        // Variables
        var data = playerManager.getEffectData(player);
        var style = manager.getStyle();

        // Create inventory
        var holder = new AdminGUIHolder(filterCategory, filterEvent);
        var inventory = Bukkit.createInventory(holder, style.getSize(), MessageUtils.parseLegacy("<red>TreasureParticles [ADMIN]"));
        holder.setInventory(inventory);
        holder.setPage(page);

        // Borders
        if (BORDERS.isEnabled())
            for (int slot : BORDERS.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        // Effects
        var effects = effectManager.getEffects().stream().filter(effect -> (filterEvent == null || effect.getEvents().contains(filterEvent)) && ((filterCategory == null || filterCategory == FilterCategory.SUPPORTED_EVENTS) || (filterCategory == FilterCategory.HAS_PERMISSION && effect.getPermission() != null) || (filterCategory == FilterCategory.NO_PERMISSION && effect.getPermission() == null))).toList();

        // Filter button
        for (int slot : FILTER.slots())
            inventory.setItem(slot, new CustomItem(FILTER.item())
                    .setDisplayName(MessageUtils.parseLegacy("<red>Filter"))
                    .addLore(MessageUtils.parseLegacy("<dark_gray>" + (filterCategory == null ? "None" : filterCategory.translation())))
                    .addLore(filterCategory != FilterCategory.SUPPORTED_EVENTS ? null : Arrays.stream(HandlerEvent.values()).map(event -> MessageUtils.parseLegacy("<dark_gray> • <" + (event == filterEvent ? "green" : "gray") + ">" + translations.get("events." + event.translationKey()))).toList())
                    .addLore("",
                            filterCategory != FilterCategory.SUPPORTED_EVENTS ? null : MessageUtils.parseLegacy("<yellow>Left/Right click to change filter"),
                            MessageUtils.parseLegacy("<gold>Middle click to change filter category")
                    )
                    .addData(Keys.BUTTON_TYPE, ElementType.FILTER.name())
                    .build());

        // Previous page button
        if (page > 0)
            for (int slot : PREVIOUS_PAGE.slots())
                inventory.setItem(slot, new CustomItem(PREVIOUS_PAGE.item())
                        .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_PREVIOUS_PAGE))
                        .addData(Keys.BUTTON_TYPE, ElementType.PREVIOUS_PAGE.name())
                        .build());
        else if (BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        // Next page button
        if ((page + 1) * maxEffects < effects.size())
            for (int slot : NEXT_PAGE.slots())
                inventory.setItem(slot, new CustomItem(NEXT_PAGE.item())
                        .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_NEXT_PAGE))
                        .addData(Keys.BUTTON_TYPE, ElementType.NEXT_PAGE.name())
                        .build());
        else if (BORDERS.isEnabled())
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
                    .addLore(MessageUtils.parseLegacy("<dark_gray>" + effect.getKey()), "")
                    .addLore(effect.getDescription())
                    .addLore(effect.getDescription() != null ? ChatColor.AQUA.toString() : null)
                    .addLore(MessageUtils.parseLegacy("<gray>Permission: <yellow>" + (effect.getPermission() == null ? "None" : effect.getPermission())))
                    .addLore(MessageUtils.parseLegacy("<gray>Caching: <yellow>" + (effect.isCachingEnabled() ? "Enabled" : "Disabled")))
                    .addLore(MessageUtils.parseLegacy("<gray>Interval: <yellow>" + effect.getInterval()))
                    .addLore(colorGroup != null ? MessageUtils.parseLegacy("<gray>Color Group: <yellow>" + colorGroup.getKey()) : null)
                    .addLore(MessageUtils.parseLegacy("<gray>Events: <yellow>" + effect.getEvents().stream().map(event -> translations.get("events." + event.translationKey())).collect(Collectors.joining(", "))))
                    .changeArmorColor(color)
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
package net.treasure.core.gui;

import net.treasure.color.data.RGBColorData;
import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.task.GUIUpdater;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;

public class EffectsGUI {

    public static void open(Player player, int page) {
        var inst = TreasurePlugin.getInstance();
        var effectManager = inst.getEffectManager();
        var data = inst.getPlayerManager().getPlayerData(player);

        var holder = new GUIHolder();
        var inventory = Bukkit.createInventory(holder, 54, MessageUtils.parseLegacy(Translations.GUI_TITLE));
        holder.setInventory(inventory);
        holder.setPage(page);

        for (int i = 0; i < 9; i++)
            inventory.setItem(i, new CustomItem(GUIElements.BORDERS).setDisplayName("§b").build());

        for (int i = 45; i < 54; i++)
            inventory.setItem(i, new CustomItem(GUIElements.BORDERS).setDisplayName("§b").build());

        for (int i = 9; i < 45; i += 9)
            inventory.setItem(i, new CustomItem(GUIElements.BORDERS).setDisplayName("§b").build());

        for (int i = 17; i < 54; i += 9)
            inventory.setItem(i, new CustomItem(GUIElements.BORDERS).setDisplayName("§b").build());

        var close = GUIElements.CLOSE;
        inventory.setItem(close.getKey(), new CustomItem(close.getValue())
                .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_CLOSE))
                .addData(Keys.BUTTON_TYPE, "CLOSE")
                .build());

        if (page > 0) {
            var previous = GUIElements.PREVIOUS_PAGE;
            inventory.setItem(previous.getKey(), new CustomItem(previous.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_PREVIOUS_PAGE))
                    .addData(Keys.BUTTON_TYPE, "PREVIOUS_PAGE")
                    .build());
        }

        if (data.getCurrentEffect() != null) {
            var reset = GUIElements.RESET;
            inventory.setItem(reset.getKey(), new CustomItem(reset.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_RESET_EFFECT))
                    .addLore(MessageUtils.parseLegacy(String.format(Translations.GUI_RESET_EFFECT_CURRENT, data.getCurrentEffect().getDisplayName())))
                    .addData(Keys.BUTTON_TYPE, "RESET")
                    .build());
        }

        var effects = effectManager.getEffects().stream().filter(effect -> effect.getPermission() == null || player.hasPermission(effect.getPermission())).toList();

        if ((page + 1) * 28 < effects.size()) {
            var next = GUIElements.NEXT_PAGE;
            inventory.setItem(next.getKey(), new CustomItem(next.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Translations.GUI_NEXT_PAGE))
                    .addData(Keys.BUTTON_TYPE, "NEXT_PAGE")
                    .build());
        }

        HashMap<Integer, RGBColorData> animatedSlots = null;

        int index = 0;
        for (int i = page * 28; i < (page + 1) * 28; i++) {
            if (effects.size() <= i) break;

            int where = (index / 7 + 1) * 9 + (index % 7) + 1;

            var effect = effects.get(i);
            Color color = null;

            if (effect.getArmorColor() != null) {
                if (animatedSlots == null) animatedSlots = new HashMap<>();

                var tempColor = inst.getColorManager().get(effect.getArmorColor());
                if (tempColor != null) {
                    var colorData = new RGBColorData(tempColor, inst.guiColorCycleSpeed(), true);
                    animatedSlots.put(where, colorData);
                    color = colorData.nextBukkit();
                } else {
                    var c = java.awt.Color.decode(effect.getArmorColor());
                    color = Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
                }
            }

            inventory.setItem(where, new CustomItem(effect.getIcon())
                    .setDisplayName("§f" + MessageUtils.parseLegacy(effect.getDisplayName()))
                    .setLore(MessageUtils.parseLegacy(effect.equals(data.getCurrentEffect()) ? Translations.GUI_EFFECT_SELECTED : Translations.GUI_SELECT_EFFECT))
                    .addLore(effect.getDescription() != null ? "§b" : null)
                    .addLore(effect.getDescription())
                    .changeArmorColor(color)
                    .addData(Keys.EFFECT, effect.getKey())
                    .glow(data.getCurrentEffect() != null && data.getCurrentEffect().equals(effect))
                    .addItemFlags(ItemFlag.values())
                    .build());
            index += 1;
        }

        if (animatedSlots != null) {
            holder.setAnimatedSlots(animatedSlots);
            GUIUpdater.getPlayers().add(player.getUniqueId());
        } else
            GUIUpdater.getPlayers().remove(player.getUniqueId());

        player.openInventory(inventory);
    }
}
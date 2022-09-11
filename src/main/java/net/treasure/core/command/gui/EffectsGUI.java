package net.treasure.core.command.gui;

import net.treasure.color.data.RGBColorData;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.task.GUIUpdater;
import net.treasure.effect.Effect;
import net.treasure.locale.Messages;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.List;

public class EffectsGUI {

    public void open(Player player, int page) {
        var inst = TreasurePlugin.getInstance();
        var effectManager = inst.getEffectManager();
        var data = inst.getPlayerManager().getPlayerData(player);

        GUIHolder holder = new GUIHolder();
        var inventory = Bukkit.createInventory(holder, 54, MessageUtils.parseLegacy(Messages.GUI_TITLE));
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
                .setDisplayName(MessageUtils.parseLegacy(Messages.GUI_CLOSE))
                .addData("button_type", "CLOSE")
                .build());

        if (page > 0) {
            var previous = GUIElements.PREVIOUS_PAGE;
            inventory.setItem(previous.getKey(), new CustomItem(previous.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Messages.GUI_PREVIOUS_PAGE))
                    .addData("button_type", "PREVIOUS_PAGE")
                    .build());
        }

        if (data.getCurrentEffect() != null) {
            var reset = GUIElements.RESET;
            inventory.setItem(reset.getKey(), new CustomItem(reset.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Messages.GUI_RESET_EFFECT))
                    .addLore(MessageUtils.parseLegacy(String.format(Messages.GUI_RESET_EFFECT_CURRENT, data.getCurrentEffect().getDisplayName())))
                    .addData("button_type", "RESET")
                    .build());
        }

        List<Effect> effects = effectManager.getEffects().stream().filter(effect -> effect.getPermission() == null || player.hasPermission(effect.getPermission())).toList();

        if ((page + 1) * 28 < effects.size()) {
            var next = GUIElements.NEXT_PAGE;
            inventory.setItem(next.getKey(), new CustomItem(next.getValue())
                    .setDisplayName(MessageUtils.parseLegacy(Messages.GUI_NEXT_PAGE))
                    .addData("button_type", "NEXT_PAGE")
                    .build());
        }

        boolean hasAnimation = false;
        HashMap<Integer, RGBColorData> updateSlots = null;

        int index = 0;
        for (int i = page * 28; i < (page + 1) * 28; i++) {
            if (effects.size() <= i) {
                break;
            }
            int where = (index / 7 + 1) * 9 + (index % 7) + 1;
            Effect effect = effects.get(i);
            Color color = null;
            if (effect.getArmorColor() != null) {
                hasAnimation = true;
                if (updateSlots == null)
                    updateSlots = new HashMap<>();
                var _color = TreasurePlugin.getInstance().getColorManager().get(effect.getArmorColor());
                if (_color != null) {
                    var _data = new RGBColorData(_color, 0.75f, true);
                    updateSlots.put(where, _data);
                    color = _data.nextBukkit();
                } else {
                    var _color_ = java.awt.Color.decode(effect.getArmorColor());
                    color = Color.fromRGB(_color_.getRed(), _color_.getGreen(), _color_.getBlue());
                }
            }
            inventory.setItem(where, new CustomItem(effect.getIcon())
                    .setDisplayName("§f" + MessageUtils.parseLegacy(effect.getDisplayName()))
                    .setLore(MessageUtils.parseLegacy(data.getCurrentEffect() != null && data.getCurrentEffect().equals(effect) ? Messages.GUI_EFFECT_SELECTED : Messages.GUI_SELECT_EFFECT))
                    .addLore(effect.getDescription() != null ? "§b" : null)
                    .addLore(effect.getDescription() != null ? effect.getDescription() : null)
                    .changeArmorColor(color)
                    .addData("effect", effect.getKey())
                    .glow(data.getCurrentEffect() != null && data.getCurrentEffect().equals(effect))
                    .addItemFlags(ItemFlag.values())
                    .build());
            index += 1;
        }

        if (hasAnimation) {
            holder.setUpdateSlots(updateSlots);
            GUIUpdater.getPlayers().add(player.getUniqueId());
        } else
            GUIUpdater.getPlayers().remove(player.getUniqueId());

        player.openInventory(inventory);
    }
}
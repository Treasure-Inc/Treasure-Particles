package net.treasure.core.command.gui;

import net.treasure.color.player.ColorData;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.task.GUIUpdater;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.effect.player.EffectData;
import net.treasure.util.CustomItem;
import net.treasure.locale.Messages;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.List;

public class EffectsGUI {

    public void open(Player player, int page) {
        EffectManager effectManager = TreasurePlugin.getInstance().getEffectManager();
        EffectData data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);

        GUIHolder holder = new GUIHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, MessageUtils.parseLegacy(Messages.GUI_TITLE));
        holder.setInventory(inventory);
        holder.setPage(page);

        for (int i = 0; i < 9; i++)
            inventory.setItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§b").build());

        for (int i = 45; i < 54; i++)
            inventory.setItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§b").build());

        for (int i = 9; i < 45; i += 9)
            inventory.setItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§b").build());

        for (int i = 17; i < 54; i += 9)
            inventory.setItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§b").build());


        inventory.setItem(49, new CustomItem(Material.BARRIER).setDisplayName(MessageUtils.parseLegacy(Messages.GUI_CLOSE)).build());

        if (page > 0)
            inventory.setItem(46, new CustomItem(Material.ENDER_EYE).setDisplayName(MessageUtils.parseLegacy(Messages.GUI_PREVIOUS_PAGE)).build());

        if (data.getCurrentEffect() != null)
            inventory.setItem(50, new CustomItem(Material.RED_STAINED_GLASS_PANE).setDisplayName(MessageUtils.parseLegacy(Messages.GUI_RESET_EFFECT)).build());

        List<Effect> effects = effectManager.getEffects().stream().filter(effect -> effect.getPermission() == null || player.hasPermission(effect.getPermission())).toList();

        if ((page + 1) * 28 < effects.size())
            inventory.setItem(52, new CustomItem(Material.ENDER_PEARL).setDisplayName(MessageUtils.parseLegacy(Messages.GUI_NEXT_PAGE)).build());

        boolean hasAnimation = false;
        HashMap<Integer, ColorData> updateSlots = null;

        for (int i = page * 28; i < (page + 1) * 28; i++) {
            if (effects.size() <= i) {
                break;
            }
            int where = (i / 7 + 1) * 9 + (i % 7) + 1;
            Effect effect = effects.get(i);
            Color color = null;
            if (effect.getArmorColor() != null) {
                hasAnimation = true;
                if (updateSlots == null)
                    updateSlots = new HashMap<>();
                net.treasure.color.Color _color = TreasurePlugin.getInstance().getColorManager().get(effect.getArmorColor());
                if (_color != null) {
                    ColorData _data = new ColorData(_color, 1f, true);
                    updateSlots.put(where, _data);
                    color = _data.nextBukkit();
                } else {
                    var _color_ = java.awt.Color.decode(effect.getArmorColor());
                    color = Color.fromRGB(_color_.getRed(), _color_.getGreen(), _color_.getBlue());
                }
            }
            inventory.setItem(where, new CustomItem(Material.LEATHER_BOOTS)
                    .setDisplayName("§f" + MessageUtils.parseLegacy(effect.getDisplayName()))
                    .setLore(MessageUtils.parseLegacy(data.getCurrentEffect() != null && data.getCurrentEffect().equals(effect) ? Messages.GUI_EFFECT_SELECTED : Messages.GUI_SELECT_EFFECT))
                    .changeArmorColor(color)
                    .addData("effect", effect.getKey())
                    .glow(data.getCurrentEffect() != null && data.getCurrentEffect().equals(effect))
                    .addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
                    .build());
        }

        if (hasAnimation) {
            holder.setUpdateSlots(updateSlots);
            GUIUpdater.getPlayers().add(player.getUniqueId());
        } else
            GUIUpdater.getPlayers().remove(player.getUniqueId());

        player.openInventory(inventory);
    }
}
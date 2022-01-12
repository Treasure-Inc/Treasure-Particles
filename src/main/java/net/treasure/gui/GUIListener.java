package net.treasure.gui;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.player.EffectData;
import net.treasure.gui.task.GUIUpdater;
import net.treasure.util.locale.Messages;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GUIHolder)
            event.setCancelled(true);
        else return;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        GUIHolder holder = (GUIHolder) event.getView().getTopInventory().getHolder();
        Player player = (Player) event.getWhoClicked();
        EffectData data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);

        if (event.getSlot() == 46 && item.getType().equals(Material.ENDER_EYE)) {
            new EffectsGUI().open(player, holder.getPage() - 1);
            return;
        }

        if (event.getSlot() == 52 && item.getType().equals(Material.ENDER_PEARL)) {
            new EffectsGUI().open(player, holder.getPage() + 1);
            return;
        }

        if (event.getSlot() == 49 && item.getType().equals(Material.BARRIER)) {
            player.closeInventory();
            return;
        }

        if (event.getSlot() == 50 && item.getType().equals(Material.RED_STAINED_GLASS_PANE)) {
            data.setCurrentEffect(player, null);
            new EffectsGUI().open(player, holder.getPage());
            return;
        }

        NamespacedKey key = new NamespacedKey(TreasurePlugin.getInstance(), "effect");
        if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String effectKey = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            Effect effect = TreasurePlugin.getInstance().getEffectManager().get(effectKey);
            data.setCurrentEffect(player, effect);
            player.sendMessage(String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
            player.closeInventory();
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GUIHolder) {
            GUIUpdater.getPlayers().remove(event.getPlayer().getUniqueId());
        }
    }
}
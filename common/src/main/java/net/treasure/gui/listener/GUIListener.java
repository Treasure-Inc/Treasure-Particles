package net.treasure.gui.listener;

import net.treasure.TreasureParticles;
import net.treasure.gui.GUIHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GUIHolder holder)) return;

        event.setCancelled(true);
        var item = event.getCurrentItem();
        if (item == null) return;
        holder.performClick(event);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GUIHolder holder)) return;

        var closeListener = holder.closeListener();
        if (closeListener == null) return;
        Bukkit.getScheduler().runTask(TreasureParticles.getPlugin(), () -> closeListener.accept((Player) event.getPlayer()));
    }
}
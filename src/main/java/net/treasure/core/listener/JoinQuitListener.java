package net.treasure.core.listener;

import net.treasure.core.TreasurePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        TreasurePlugin.getInstance().getPlayerManager().initializePlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        TreasurePlugin.getInstance().getPlayerManager().remove(event.getPlayer());
    }
}
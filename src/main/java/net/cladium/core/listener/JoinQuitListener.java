package net.cladium.core.listener;

import net.cladium.core.CladiumPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        CladiumPlugin.getInstance().getPlayerManager().initializePlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        CladiumPlugin.getInstance().getPlayerManager().remove(event.getPlayer());
    }
}
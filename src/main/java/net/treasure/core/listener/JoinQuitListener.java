package net.treasure.core.listener;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    PlayerManager playerManager;

    public JoinQuitListener(TreasurePlugin plugin) {
        this.playerManager = plugin.getPlayerManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        playerManager.initializePlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        playerManager.remove(event.getPlayer());
    }
}
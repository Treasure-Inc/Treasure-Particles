package net.treasure.player.listener;

import lombok.AllArgsConstructor;
import net.treasure.TreasureParticles;
import net.treasure.locale.Translations;
import net.treasure.permission.Permissions;
import net.treasure.player.PlayerManager;
import net.treasure.util.message.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class JoinQuitListener implements Listener {

    final PlayerManager playerManager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        var player = event.getPlayer();
        playerManager.initializePlayer(player, data -> {
            if (!player.isOnline()) {
                playerManager.remove(player);
                return;
            }
            if (player.hasPermission(Permissions.ADMIN) && TreasureParticles.isNotificationsEnabled() && data.isNotificationsEnabled())
                MessageUtils.sendParsed(player, Translations.NOTIFICATION);
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        playerManager.remove(event.getPlayer());
    }
}
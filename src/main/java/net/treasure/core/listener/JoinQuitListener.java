package net.treasure.core.listener;

import lombok.AllArgsConstructor;
import net.treasure.common.Permissions;
import net.treasure.core.TreasurePlugin;
import net.treasure.locale.Translations;
import net.treasure.util.message.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class JoinQuitListener implements Listener {

    final TreasurePlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        var player = event.getPlayer();
        plugin.getPlayerManager().initializePlayer(player, data -> {
            if (!player.isOnline()) {
                plugin.getPlayerManager().remove(player);
                return;
            }
            if (player.hasPermission(Permissions.ADMIN) && plugin.getNotificationManager().isEnabled() && data.isNotificationsEnabled()) {
                MessageUtils.sendParsed(player, Translations.NOTIFICATION);
                if (plugin.getUpdateChecker().isUpdateAvailable())
                    MessageUtils.sendParsed(player, "<prefix> <green>New version of TreasureElytra available!");
            }
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        plugin.getPlayerManager().remove(event.getPlayer());
    }
}
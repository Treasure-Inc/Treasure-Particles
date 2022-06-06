package net.treasure.core.listener;

import net.treasure.common.Permissions;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.player.PlayerManager;
import net.treasure.locale.Messages;
import net.treasure.util.message.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    TreasurePlugin plugin;

    public JoinQuitListener(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        var player = event.getPlayer();
        plugin.getPlayerManager().initializePlayer(player).thenAccept(data -> {
            if (player.hasPermission(Permissions.ADMIN) && plugin.getNotificationManager().isEnabled() && data.isNotificationsEnabled()) {
                MessageUtils.sendParsed(player, Messages.PREFIX + "<aqua><b><click:suggest_command:'/trelytra changelog'><hover:show_text:'<aqua>Click!'>Changelog</click></b> <dark_gray>|</dark_gray> " +
                        "<b><click:open_url:'https://www.spigotmc.org/resources/trelytra-let-your-elytra-create-wonderful-particles.99860/'><hover:show_text:'<aqua>Click'>Spigot Page</b> <dark_gray>|</dark_gray> " +
                        "<b><click:open_url:'https://github.com/ItsZypec/Treasure-Elytra/wiki/'><hover:show_text:'<aqua>Click!'>Wiki Page");
            }
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        plugin.getPlayerManager().remove(event.getPlayer());
    }
}
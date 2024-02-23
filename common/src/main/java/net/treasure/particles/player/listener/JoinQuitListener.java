package net.treasure.particles.player.listener;

import lombok.AllArgsConstructor;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.EffectManager;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class JoinQuitListener implements Listener {

    private final PlayerManager playerManager;
    private final EffectManager effectManager;

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
        effectManager.getData().remove(event.getPlayer().getUniqueId().toString());
    }
}
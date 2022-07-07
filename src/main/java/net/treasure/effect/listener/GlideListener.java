package net.treasure.effect.listener;

import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

@AllArgsConstructor
public class GlideListener implements Listener {

    PlayerManager playerManager;

    @EventHandler
    public void on(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        var data = playerManager.getPlayerData(player);
        if (data == null) {
            playerManager.initializePlayer(player);
            data = playerManager.getPlayerData(player);
        }
        if (data == null) {
            TreasurePlugin.logger().warning("Couldn't initialize " + player.getName() + "'s data");
            return;
        }
        if (data.getCurrentEffect() != null && !data.getCurrentEffect().canUse(player))
            data.setCurrentEffect(player, null);
        data.setEnabled(event.isGliding());
    }
}
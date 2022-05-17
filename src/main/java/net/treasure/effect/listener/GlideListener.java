package net.treasure.effect.listener;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.player.EffectData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class GlideListener implements Listener {

    @EventHandler
    public void on(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        EffectData data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);
        if (data == null) {
            TreasurePlugin.getInstance().getPlayerManager().initializePlayer(player);
            data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);
        }
        data.setEnabled(event.isGliding());
        data.setStartedGliding(event.isGliding() ? System.currentTimeMillis() : -1);
    }
}
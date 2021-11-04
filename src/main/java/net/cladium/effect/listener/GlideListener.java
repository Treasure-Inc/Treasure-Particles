package net.cladium.effect.listener;

import net.cladium.core.CladiumPlugin;
import net.cladium.effect.player.EffectData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class GlideListener implements Listener {

    @EventHandler
    public void on(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        EffectData data = CladiumPlugin.getInstance().getPlayerManager().getPlayerData(player);
        if (data == null) {
            CladiumPlugin.getInstance().getPlayerManager().initializePlayer(player);
            data = CladiumPlugin.getInstance().getPlayerManager().getPlayerData(player);
        }
        data.setEnabled(event.isGliding());
    }
}
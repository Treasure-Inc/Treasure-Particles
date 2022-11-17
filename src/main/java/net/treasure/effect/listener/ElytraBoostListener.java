package net.treasure.effect.listener;

import lombok.AllArgsConstructor;
import net.treasure.core.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class ElytraBoostListener implements Listener {

    PlayerManager manager;

    @EventHandler(ignoreCancelled = true)
    public void on(com.destroystokyo.paper.event.player.PlayerElytraBoostEvent event) {
        var data = manager.getEffectData(event.getPlayer());
        if (data == null) return;

        data.setLastBoostMillis(System.currentTimeMillis());
    }
}
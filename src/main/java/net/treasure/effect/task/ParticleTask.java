package net.treasure.effect.task;

import net.treasure.core.TreasurePlugin;
import net.treasure.util.TimeKeeper;
import org.bukkit.Bukkit;

public class ParticleTask implements Runnable {

    @Override
    public void run() {
        TimeKeeper.increaseTime();
        var iterator = TreasurePlugin.getInstance().getPlayerManager().getPlayersData().entrySet().iterator();
        while (iterator.hasNext()) {
            var set = iterator.next();

            var uuid = set.getKey();
            var data = set.getValue();

            var player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }

            if (!data.isEnabled())
                continue;

            if (!player.isGliding())
                continue;

            var current = data.getCurrentEffect();
            if (current != null)
                current.doTick(player, data);
        }
    }
}
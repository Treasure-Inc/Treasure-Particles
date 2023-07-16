package net.treasure.effect.task;

import lombok.AllArgsConstructor;
import net.treasure.player.PlayerManager;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class MovementCheck implements Runnable {

    final PlayerManager playerManager;

    @Override
    public void run() {
        var iterator = playerManager.getData().entrySet().iterator();
        while (iterator.hasNext()) {
            var set = iterator.next();

            var data = set.getValue();

            var player = data.player;
            if (player == null) {
                iterator.remove();
                continue;
            }

            if (data.getCurrentEffect() == null)
                continue;

            var location = player.getLocation();
            var last = data.getLastVector();
            var current = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            data.setLastVector(current);

            if (!current.equals(last)) {
                data.resetInterval();
            } else {
                data.increaseInterval();
            }
        }
    }
}
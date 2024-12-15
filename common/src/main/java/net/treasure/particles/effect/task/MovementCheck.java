package net.treasure.particles.effect.task;

import lombok.AllArgsConstructor;
import net.treasure.particles.player.PlayerManager;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class MovementCheck implements Runnable {

    private final PlayerManager playerManager;

    @Override
    public void run() {
        for (var set : playerManager.getData().entrySet()) {
            var data = set.getValue();

            var player = data.player;

            if (data.getCurrentEffect() == null) continue;

            var location = player.getLocation();
            var last = data.getLastVector();
            var current = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            data.setLastVector(current);

            if (!current.equals(last)) {
                data.resetMovingInterval();
            } else {
                data.increaseMovingInterval();
            }
        }
    }
}
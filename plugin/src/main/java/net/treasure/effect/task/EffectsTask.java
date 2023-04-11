package net.treasure.effect.task;

import lombok.AllArgsConstructor;
import net.treasure.util.TimeKeeper;
import net.treasure.core.player.PlayerManager;

@AllArgsConstructor
public class EffectsTask implements Runnable {

    PlayerManager playerManager;

    @Override
    public void run() {
        TimeKeeper.increaseTime();
        var iterator = playerManager.getData().entrySet().iterator();
        while (iterator.hasNext()) {
            var set = iterator.next();

            var data = set.getValue();

            var player = data.getPlayer();
            if (player == null) {
                iterator.remove();
                continue;
            }

            if (!data.isEnabled())
                continue;

            if (!player.isGliding() && !data.isDebugModeEnabled())
                continue;

            var current = data.getCurrentEffect();
            if (current != null)
                current.doTick(player, data);
        }
    }
}
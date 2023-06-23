package net.treasure.effect.task;

import lombok.AllArgsConstructor;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.util.TimeKeeper;

@AllArgsConstructor
public class EffectsTask implements Runnable {

    final PlayerManager playerManager;

    @Override
    public void run() {
        TimeKeeper.increaseTime();
        var iterator = playerManager.getData().entrySet().iterator();
        while (iterator.hasNext()) {
            var set = iterator.next();

            var data = set.getValue();

            var player = data.player;
            if (player == null) {
                iterator.remove();
                continue;
            }

            var current = data.getCurrentEffect();
            if (current == null)
                continue;

            var event = data.getCurrentEvent();
            if (event == null || !event.isSpecial())
                data.setCurrentEvent(player.isGliding() ? HandlerEvent.ELYTRA : (data.isMoving() ? HandlerEvent.MOVING : HandlerEvent.STANDING));

            current.doTick(player, data);
        }
    }
}
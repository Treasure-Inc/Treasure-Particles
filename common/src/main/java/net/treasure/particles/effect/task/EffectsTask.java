package net.treasure.particles.effect.task;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.EffectManager;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.util.TimeKeeper;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class EffectsTask extends BukkitRunnable {

    private final EffectManager effectManager;

    @Override
    public void run() {
        TimeKeeper.increaseTime();
        var iterator = effectManager.getData().entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var data = entry.getValue();

            var location = data.getLocation();
            if (location == null) {
                iterator.remove();
                continue;
            }

            var current = data.getCurrentEffect();
            if (current == null) {
                iterator.remove();
                continue;
            }

            var event = data.getCurrentEvent();
            if (data instanceof PlayerEffectData playerEffectData && (event == null || !event.isSpecial())) {
                var player = playerEffectData.player;
                if (player == null) {
                    iterator.remove();
                    continue;
                }
                data.setCurrentEvent(player.isGliding() ? HandlerEvent.ELYTRA : (player.isSneaking() ? HandlerEvent.SNEAKING : (playerEffectData.isMoving() ? HandlerEvent.MOVING : HandlerEvent.STANDING)));
            }
            current.doTick(data);
        }
    }
}
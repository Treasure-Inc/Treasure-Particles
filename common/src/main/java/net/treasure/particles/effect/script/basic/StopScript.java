package net.treasure.particles.effect.script.basic;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import org.bukkit.entity.Player;

public class StopScript extends Script {

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        data.setCurrentEffect(null);
        return TickResult.NORMAL;
    }

    @Override
    public StopScript clone() {
        return new StopScript();
    }
}

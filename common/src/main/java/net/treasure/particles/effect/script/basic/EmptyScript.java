package net.treasure.particles.effect.script.basic;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import org.bukkit.entity.Player;

public class EmptyScript extends Script {

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        return TickResult.NORMAL;
    }

    @Override
    public EmptyScript clone() {
        return new EmptyScript();
    }
}
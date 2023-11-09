package net.treasure.particles.effect.script.basic;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import org.bukkit.entity.Player;

public class BreakHandlerScript extends Script {

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        return TickResult.BREAK_HANDLER;
    }

    @Override
    public BreakHandlerScript clone() {
        return new BreakHandlerScript();
    }
}

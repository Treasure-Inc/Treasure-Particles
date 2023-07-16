package net.treasure.effect.script.basic;

import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

public class BreakScript extends Script {

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        return TickResult.BREAK;
    }

    @Override
    public BreakScript clone() {
        return new BreakScript();
    }
}

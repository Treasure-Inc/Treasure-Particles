package net.treasure.effect.script.basic;

import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

public class BreakHandlerScript extends Script {

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        return TickResult.BREAK_HANDLER;
    }

    @Override
    public BreakHandlerScript clone() {
        return new BreakHandlerScript();
    }
}

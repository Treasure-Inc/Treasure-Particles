package net.treasure.effect.script.basic;

import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.Script;
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
package net.treasure.effect.script;

import net.treasure.effect.player.EffectData;
import org.bukkit.entity.Player;

public class EmptyScript extends Script {

    @Override
    public void tick(Player player, EffectData data, int times) {
    }

    @Override
    public Script clone() {
        return new EmptyScript();
    }
}
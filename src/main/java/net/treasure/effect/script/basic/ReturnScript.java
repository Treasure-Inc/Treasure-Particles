package net.treasure.effect.script.basic;

import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

public class ReturnScript extends Script {

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        return false;
    }

    @Override
    public Script clone() {
        return new ReturnScript();
    }
}

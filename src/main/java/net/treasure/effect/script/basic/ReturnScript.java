package net.treasure.effect.script.basic;

import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.ScriptReader;
import org.bukkit.entity.Player;

public class ReturnScript extends Script implements ScriptReader<ReturnScript> {

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        return false;
    }

    @Override
    public ReturnScript clone() {
        return new ReturnScript();
    }

    @Override
    public ReturnScript read(Effect effect, String line) {
        return new ReturnScript();
    }
}
package net.treasure.effect.script.basic;

import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.ScriptReader;
import org.bukkit.entity.Player;

public class EmptyScript extends Script implements ScriptReader<EmptyScript> {

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        return true;
    }

    @Override
    public EmptyScript clone() {
        return new EmptyScript();
    }

    @Override
    public EmptyScript read(Effect effect, String line) {
        return new EmptyScript();
    }
}
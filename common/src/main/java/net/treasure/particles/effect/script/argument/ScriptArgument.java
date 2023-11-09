package net.treasure.particles.effect.script.argument;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.reader.ReaderContext;
import org.bukkit.entity.Player;

public interface ScriptArgument<T> {
    T get(Player player, Script script, EffectData data);

    ScriptArgument<?> validate(ReaderContext<?> context) throws ReaderException;
}
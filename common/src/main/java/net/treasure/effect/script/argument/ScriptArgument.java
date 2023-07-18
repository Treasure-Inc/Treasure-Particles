package net.treasure.effect.script.argument;

import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.reader.ReaderContext;
import org.bukkit.entity.Player;

public interface ScriptArgument<T> {
    T get(Player player, Script script, EffectData data);

    ScriptArgument<?> validate(ReaderContext<?> context) throws ReaderException;
}
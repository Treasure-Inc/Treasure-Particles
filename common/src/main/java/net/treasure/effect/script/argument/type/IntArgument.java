package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.ScriptArgument;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.variable.Variable;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class IntArgument implements ScriptArgument<Integer> {

    Object value;

    public static IntArgument read(ReaderContext<?> context) throws ReaderException {
        return read(context, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static IntArgument read(ReaderContext<?> context, int min) throws ReaderException {
        return read(context, min, Integer.MAX_VALUE);
    }

    public static IntArgument read(ReaderContext<?> context, int min, int max) throws ReaderException {
        var arg = context.value();
        try {
            var i = Integer.parseInt(arg);
            if (i > max || i < min)
                throw new ReaderException("Integer must be in range (" + min + ", " + (max != Integer.MAX_VALUE ? max : "∞") + ")");
            return new IntArgument(i).validate(context);
        } catch (Exception e) {
            return new IntArgument(arg).validate(context);
        }
    }

    @Override
    public Integer get(Player player, Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Integer i) return i;
        else if (value instanceof String s) return data.getVariable(script.getEffect(), s).y().intValue();
        else return null;
    }

    @Override
    public IntArgument validate(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        try {
            Integer.parseInt(arg);
            return this;
        } catch (Exception e) {
            arg = Variable.replace(arg);
            if (context.effect().isValidVariable(arg))
                return new IntArgument(arg);
            else
                throw new ReaderException("Valid values for Integer argument: integers, {variable}");
        }
    }
}
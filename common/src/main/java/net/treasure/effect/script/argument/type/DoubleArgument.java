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
public class DoubleArgument implements ScriptArgument<Double> {

    public Object value;

    public static DoubleArgument read(String arg) {
        try {
            return new DoubleArgument(Double.parseDouble(arg));
        } catch (Exception e) {
            return new DoubleArgument(arg);
        }
    }

    public static DoubleArgument read(ReaderContext<?> context) throws ReaderException {
        return read(context.value()).validate(context);
    }

    public static DoubleArgument read(ReaderContext<?> context, String arg) throws ReaderException {
        var value = context.value();
        context.value(arg);
        try {
            var result = read(arg).validate(context);
            context.value(value);
            return result;
        } catch (Exception e) {
            context.value(value);
            throw e;
        }
    }

    @Override
    public Double get(Player player, Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Double d) return d;
        else if (value instanceof String s) return data.getVariable(script.getEffect(), s).y();
        else return null;
    }

    @Override
    public DoubleArgument validate(ReaderContext<?> context) throws ReaderException {
        if (value instanceof Double) return this;
        if (value instanceof String arg) {
            try {
                Double.parseDouble(arg);
                return this;
            } catch (Exception e) {
                arg = Variable.replace(arg);
                if (context.effect().isValidVariable(arg))
                    return new DoubleArgument(arg);
                else
                    throw new ReaderException("Valid values for Double argument: decimals, {variable}");
            }
        }
        throw new ReaderException("Valid values for Double argument: decimals, {variable}");
    }
}
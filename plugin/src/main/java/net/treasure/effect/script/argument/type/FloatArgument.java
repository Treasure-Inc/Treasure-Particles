package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.argument.ScriptArgument;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.variable.Variable;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class FloatArgument implements ScriptArgument<Float> {

    Object value;

    public static FloatArgument read(String arg) {
        try {
            return new FloatArgument(Float.parseFloat(arg));
        } catch (Exception e) {
            return new FloatArgument(arg);
        }
    }

    public static FloatArgument read(ReaderContext<?> context) throws ReaderException {
        return read(context.value()).validate(context);
    }

    public static FloatArgument read(ReaderContext<?> context, String arg) throws ReaderException {
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
    public Float get(Player player, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Float f) return f;
        else if (value instanceof String s) return Float.parseFloat(data.replaceVariables(s));
        else return null;
    }

    @Override
    public FloatArgument validate(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        try {
            Float.parseFloat(arg);
            return this;
        } catch (Exception e) {
            if (context.effect().isValidVariable(Variable.replace(arg)))
                return this;
            else
                throw new ReaderException("Valid values for Float argument: decimals, {variable}");
        }
    }
}
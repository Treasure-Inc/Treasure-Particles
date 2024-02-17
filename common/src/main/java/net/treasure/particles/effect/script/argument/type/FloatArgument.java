package net.treasure.particles.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.ScriptArgument;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.variable.Variable;

@AllArgsConstructor
public class FloatArgument implements ScriptArgument<Float> {

    private Object value;

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
    public Float get(Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Float f) return f;
        else if (value instanceof String s) return data.getVariable(script.getEffect(), s).y().floatValue();
        else return null;
    }

    @Override
    public FloatArgument validate(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        try {
            Float.parseFloat(arg);
            return this;
        } catch (Exception e) {
            arg = Variable.replace(arg);
            if (context.effect().isValidVariable(arg))
                return new FloatArgument(arg);
            else
                throw new ReaderException("Valid values for Float argument: decimals, {variable}");
        }
    }
}
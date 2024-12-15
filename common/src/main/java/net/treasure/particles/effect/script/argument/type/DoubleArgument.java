package net.treasure.particles.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.ScriptArgument;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.variable.Variable;

import java.util.List;

@AllArgsConstructor
public class DoubleArgument implements ScriptArgument<Double> {

    public Object value;

    @Override
    public String getName() {
        return "Dynamic Double";
    }

    @Override
    public List<String> getExamples() {
        return List.of("decimals", variableExample());
    }

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
    public Double get(Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Double d) return d;
        else if (value instanceof String s) return data.getVariable(script.getEffect(), s).getValue();
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
                    throw invalidValues();
            }
        }
        throw invalidValues();
    }
}
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
public class IntArgument implements ScriptArgument<Integer> {

    private Object value;

    @Override
    public String getName() {
        return "Dynamic Integer";
    }

    @Override
    public List<String> getExamples() {
        return List.of("integers", variableExample());
    }

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
    public Integer get(Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Integer i) return i;
        else if (value instanceof String s) return (int) data.getVariable(script.getEffect(), s).getValue();
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
                throw invalidValues();
        }
    }
}
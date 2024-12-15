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
public class BooleanArgument implements ScriptArgument<Boolean> {

    private Object value;

    @Override
    public String getName() {
        return "Dynamic Boolean";
    }

    @Override
    public List<String> getExamples() {
        return List.of("true", "false", variableExample());
    }

    public static BooleanArgument read(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        if (arg.equals("true") || arg.equals("false"))
            return new BooleanArgument(Boolean.parseBoolean(arg)).validate(context);
        else
            return new BooleanArgument(arg).validate(context);
    }

    @Override
    public Boolean get(Script script, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Boolean b) return b;
        else if (value instanceof String s) {
            var i = data.getVariable(script.getEffect(), s).getValue();
            return i != 1 && i != 0 ? null : i == 1;
        } else return null;
    }

    @Override
    public BooleanArgument validate(ReaderContext<?> context) throws ReaderException {
        if (value instanceof Boolean) return this;
        var arg = Variable.replace(context.value());
        if (context.effect().isValidVariable(arg))
            return new BooleanArgument(arg);
        else
            throw invalidValues();
    }
}
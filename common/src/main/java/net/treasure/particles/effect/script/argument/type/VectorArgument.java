package net.treasure.particles.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.particles.constants.Patterns;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.ScriptArgument;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.util.logging.ComponentLogger;
import org.bukkit.util.Vector;

import java.util.List;

@AllArgsConstructor
public class VectorArgument implements ScriptArgument<Vector> {

    private Object x, y, z;

    @Override
    public String getName() {
        return "Dynamic Vector";
    }

    @Override
    public List<String> getExamples() {
        return List.of("vectors");
    }

    public static VectorArgument read(ReaderContext<?> context) throws ReaderException {
        return read(context, context.value());
    }

    public static VectorArgument read(ReaderContext<?> context, String arg) throws ReaderException {
        Object x = null, y = null, z = null;
        var offsetMatcher = Patterns.INNER_SCRIPT.matcher(arg);
        while (offsetMatcher.find()) {
            var type = offsetMatcher.group("type");
            var value = offsetMatcher.group("value");
            try {
                switch (type) {
                    case "x" -> x = DoubleArgument.read(value).validate(context).value;
                    case "y" -> y = DoubleArgument.read(value).validate(context).value;
                    case "z" -> z = DoubleArgument.read(value).validate(context).value;
                    default -> ComponentLogger.error(context, "Unexpected vector argument: " + type);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context, "Unexpected '" + type + "' value for vector argument: " + value);
            }
        }
        return new VectorArgument(x, y, z).validate(context);
    }

    @Override
    public Vector get(Script script, EffectData data) {
        double x = 0D, y = 0D, z = 0D;

        var effect = script.getEffect();

        if (this.x instanceof Double d)
            x = d;
        else if (this.x instanceof String s)
            x = data.getVariable(effect, s).getValue();

        if (this.y instanceof Double d)
            y = d;
        else if (this.y instanceof String s)
            y = data.getVariable(effect, s).getValue();

        if (this.z instanceof Double d)
            z = d;
        else if (this.z instanceof String s)
            z = data.getVariable(effect, s).getValue();

        return new Vector(x, y, z);
    }

    @Override
    public VectorArgument validate(ReaderContext<?> context) throws ReaderException {
        if (x == null && y == null && z == null)
            throw new ReaderException("Incorrect vector argument usage");
        return this;
    }
}
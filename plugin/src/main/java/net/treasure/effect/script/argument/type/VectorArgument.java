package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.common.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.argument.ScriptArgument;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.util.logging.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class VectorArgument implements ScriptArgument<Vector> {

    public static VectorArgument read(ReaderContext<?> context) throws ReaderException {
        return read(context, context.value());
    }

    public static VectorArgument read(ReaderContext<?> context, String arg) throws ReaderException {
        Object x = null, y = null, z = null;
        var offsetMatcher = Patterns.INNER_SCRIPT.matcher(arg);
        while (offsetMatcher.find()) {
            String type = offsetMatcher.group("type");
            String value = offsetMatcher.group("value");
            try {
                switch (type) {
                    case "x" -> x = DoubleArgument.read(value).value;
                    case "y" -> y = DoubleArgument.read(value).value;
                    case "z" -> z = DoubleArgument.read(value).value;
                    default -> ComponentLogger.error(context, "Unexpected vector argument: " + type);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context, "Unexpected '" + type + "' value for vector argument: " + value);
            }
        }
        return new VectorArgument(x, y, z).validate(context);
    }

    Object x, y, z;

    @Override
    public Vector get(Player player, EffectData data) {
        double x = 0, y = 0, z = 0;

        if (this.x instanceof Double d)
            x = d;
        else if (this.x instanceof String s)
            x = Double.parseDouble(data.replaceVariables(s));

        if (this.y instanceof Double d)
            y = d;
        else if (this.y instanceof String s)
            y = Double.parseDouble(data.replaceVariables(s));

        if (this.z instanceof Double d)
            z = d;
        else if (this.z instanceof String s)
            z = Double.parseDouble(data.replaceVariables(s));

        return new Vector(x, y, z);
    }

    @Override
    public VectorArgument validate(ReaderContext<?> context) throws ReaderException {
        if (x == null && y == null && z == null)
            throw new ReaderException("Incorrect vector argument usage");
        return this;
    }
}
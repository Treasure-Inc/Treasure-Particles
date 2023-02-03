package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.common.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.argument.ScriptArgument;
import net.treasure.effect.script.variable.Variable;
import net.treasure.util.logging.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class VectorArgument implements ScriptArgument<Vector> {

    public static VectorArgument read(ReaderContext<?> context) throws ReaderException {
        String x = null, y = null, z = null;
        var offsetMatcher = Patterns.INNER_SCRIPT.matcher(context.value());
        while (offsetMatcher.find()) {
            String type = offsetMatcher.group("type");
            String value = offsetMatcher.group("value");
            try {
                switch (type) {
                    case "x" -> x = value;
                    case "y" -> y = value;
                    case "z" -> z = value;
                    default -> ComponentLogger.error(context, "Unexpected vector argument: " + type);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context, "Unexpected '" + type + "' value for vector argument: " + value);
            }
        }
        return new VectorArgument(x, y, z).validate(context);
    }

    String x, y, z;

    @Override
    public Vector get(Player player, EffectData data) {
        double x = 0, y = 0, z = 0;

        if (this.x != null) {
            try {
                x = Double.parseDouble(this.x);
            } catch (Exception e) {
                x = Double.parseDouble(data.replaceVariables(player, this.x));
            }
        }

        if (this.y != null) {
            try {
                y = Double.parseDouble(this.y);
            } catch (Exception e) {
                y = Double.parseDouble(data.replaceVariables(player, this.y));
            }
        }

        if (this.z != null) {
            try {
                z = Double.parseDouble(this.z);
            } catch (Exception e) {
                z = Double.parseDouble(data.replaceVariables(player, this.z));
            }
        }

        return new Vector(x, y, z);
    }

    @Override
    public VectorArgument validate(ReaderContext<?> context) throws ReaderException {
        if (x != null) {
            try {
                Double.parseDouble(x);
            } catch (Exception e) {
                if (!context.effect().hasVariable(Variable.replace(x)))
                    throw new ReaderException("Valid values for Vector argument: decimals, {variable}");
            }
        }

        if (y != null) {
            try {
                Double.parseDouble(y);
            } catch (Exception e) {
                if (!context.effect().hasVariable(Variable.replace(y)))
                    throw new ReaderException("Valid values for Vector argument: decimals, {variable}");
            }
        }

        if (z != null) {
            try {
                Double.parseDouble(z);
            } catch (Exception e) {
                if (!context.effect().hasVariable(Variable.replace(z)))
                    throw new ReaderException("Valid values for Vector argument: decimals, {variable}");
            }
        }

        return this;
    }
}

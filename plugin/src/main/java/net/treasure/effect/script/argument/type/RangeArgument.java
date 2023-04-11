package net.treasure.effect.script.argument.type;

import net.treasure.common.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.variable.Variable;
import net.treasure.util.logging.ComponentLogger;
import org.bukkit.entity.Player;

public class RangeArgument extends FloatArgument {

    Float min;
    Float max;

    public static RangeArgument read(ReaderContext<?> context) throws ReaderException {
        Float min = null;
        Float max = null;
        Object val = null;

        var matcher = Patterns.INNER_SCRIPT.matcher(context.value());
        while (matcher.find()) {
            String type = matcher.group("type");
            String value = matcher.group("value");
            try {
                switch (type) {
                    case "val", "value" -> val = value;
                    case "min" -> min = StaticArgument.asFloat(value);
                    case "max" -> max = StaticArgument.asFloat(value);
                    default -> ComponentLogger.error(context, "Unexpected Range argument: " + type);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context, "Unexpected '" + type + "' value for range argument: " + value);
            }
        }

        if (val == null) {
            var arg = context.value();
            try {
                Float.parseFloat(arg);
                return new RangeArgument(arg);
            } catch (Exception e) {
                if (context.effect().hasVariable(Variable.replace(arg)))
                    return new RangeArgument(arg);
                else
                    throw new ReaderException("Valid values for Float argument: decimals, {variable}");
            }
        }

        if (min == null && max == null) return new RangeArgument(val);

        try {
            return new RangeArgument(Float.parseFloat(String.valueOf(val)), min, max).validate(context);
        } catch (Exception e) {
            return new RangeArgument(val, min, max);
        }
    }

    public RangeArgument(Object value, Float min, Float max) {
        super(value);
        this.min = min;
        this.max = max;
    }

    public RangeArgument(Object value) {
        super(value);
    }

    @Override
    public Float get(Player player, EffectData data) {
        var result = super.get(player, data);
        if (max != null && result > max) return max;
        else if (min != null && result < min) return min;
        else return result;
    }

    @Override
    public RangeArgument validate(ReaderContext<?> context) throws ReaderException {
        super.validate(context);
        return this;
    }
}
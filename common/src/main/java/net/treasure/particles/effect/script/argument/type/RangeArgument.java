package net.treasure.particles.effect.script.argument.type;

import net.treasure.particles.constants.Patterns;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.variable.Variable;
import net.treasure.particles.util.logging.ComponentLogger;
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
                return new RangeArgument(Float.parseFloat(arg));
            } catch (Exception e) {
                arg = Variable.replace(arg);
                if (context.effect().isValidVariable(arg))
                    return new RangeArgument(arg);
                throw new ReaderException("Valid values for Float argument: decimals, {variable}");
            }
        }

        if (min == null && max == null) return new RangeArgument(val);

        var strVal = String.valueOf(val);
        try {
            return new RangeArgument(Float.parseFloat(strVal), min, max).validate(context);
        } catch (Exception e) {
            strVal = Variable.replace(strVal);
            if (context.effect().isValidVariable(strVal))
                return new RangeArgument(strVal, min, max);
            throw new ReaderException("Valid values for Range argument: decimals, {variable}");

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
    public Float get(Player player, Script script, EffectData data) {
        var result = super.get(player, script, data);
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
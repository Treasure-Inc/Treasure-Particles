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

    public static RangeArgument read(ReaderContext<?> context) {
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
                    case "min" -> min = Float.parseFloat(value);
                    case "max" -> max = Float.parseFloat(value);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context.effect(), context.type(), context.line(), matcher.start(), matcher.end(), "Unexpected value for " + type + ": " + value);
            }
        }

        if (val == null) return null;
        if (min == null && max == null) return new RangeArgument(context);

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
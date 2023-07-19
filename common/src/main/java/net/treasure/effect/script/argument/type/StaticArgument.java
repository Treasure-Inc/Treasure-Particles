package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.util.logging.ComponentLogger;
import net.treasure.util.math.MathUtils;

import java.util.Locale;
import java.util.function.Function;


@AllArgsConstructor
public class StaticArgument<T> {

    private final Function<String, T> function;

    public static <E extends Enum<E>> StaticArgument<E> asEnumArgument(ReaderContext<?> context, Class<E> enumClazz) {
        return new StaticArgument<>(s -> {
            try {
                return Enum.valueOf(enumClazz, s.toUpperCase(Locale.ENGLISH));
            } catch (Exception e) {
                ComponentLogger.error(context, "Unexpected '" + context.key() + "' value: " + context.value());
            }
            return null;
        });
    }

    public static <E extends Enum<E>> E asEnum(ReaderContext<?> context, Class<E> enumClazz) {
        try {
            return Enum.valueOf(enumClazz, context.value().toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            ComponentLogger.error(context, "Unexpected '" + context.key() + "' value: " + context.value());
        }
        return null;
    }

    public static String asString(ReaderContext<?> context) throws ReaderException {
        return asString(context.value());
    }

    public static String asString(String value) throws ReaderException {
        try {
            return value.substring(1, value.length() - 1);
        } catch (Exception e) {
            throw new ReaderException("Valid values for Static String argument: \"text\"");
        }
    }

    public static int asInt(ReaderContext<?> context) throws ReaderException {
        return asInt(context.value(), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int asInt(ReaderContext<?> context, int min) throws ReaderException {
        return asInt(context.value(), min, Integer.MAX_VALUE);
    }

    public static int asInt(ReaderContext<?> context, int min, int max) throws ReaderException {
        return asInt(context.value(), min, max);
    }

    public static int asInt(String value, int min, int max) throws ReaderException {
        try {
            var i = Integer.parseInt(value);
            if (i > max || i < min)
                throw new ReaderException("Static Integer must be in range (" + min + (max != Integer.MAX_VALUE ? "," + max : "") + "))");
            return i;
        } catch (Exception e) {
            throw new ReaderException("Valid values for Static Integer argument: integers");
        }
    }

    public static float asFloat(ReaderContext<?> context) throws ReaderException {
        return asFloat(context.value());
    }

    public static float asFloat(String value) throws ReaderException {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            throw new ReaderException("Valid values for Static Float argument: decimals");
        }
    }

    public static boolean asBoolean(ReaderContext<?> context) throws ReaderException {
        return asBoolean(context.value());
    }

    public static boolean asBoolean(String arg) throws ReaderException {
        if (arg.equals("true") || arg.equals("false"))
            return Boolean.parseBoolean(arg);
        else
            throw new ReaderException("Valid values for Static Boolean argument: true, false");
    }

    public T get(String value) {
        return function.apply(value);
    }
}
package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.util.unsafe.UnsafeFunction;

import java.util.Locale;


@AllArgsConstructor
public class StaticArgument<T> {

    private final UnsafeFunction<String, T> function;

    public static <E extends Enum<E>> StaticArgument<E> asEnumArgument(ReaderContext<?> context, Class<E> enumClazz) throws ReaderException {
        return new StaticArgument<>(s -> {
            try {
                return Enum.valueOf(enumClazz, s.toUpperCase(Locale.ENGLISH));
            } catch (Exception e) {
                throw new ReaderException("Unexpected '" + context.key() + "' value: " + context.value());
            }
        });
    }

    public static <E extends Enum<E>> E asEnum(ReaderContext<?> context, Class<E> enumClazz) throws ReaderException {
        try {
            return Enum.valueOf(enumClazz, context.value().toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            throw new ReaderException("Unexpected '" + context.key() + "' value: " + context.value());
        }
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
                throw new ReaderException("Integer must be in range (" + min + ", " + (max != Integer.MAX_VALUE ? max : "∞") + ")");
            return i;
        } catch (ReaderException e) {
            throw e;
        } catch (Exception e) {
            throw new ReaderException("Valid values for Static Integer argument: integers");
        }
    }

    public static float asFloat(ReaderContext<?> context) throws ReaderException {
        return asFloat(context.value(), Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public static float asFloat(ReaderContext<?> context, float min) throws ReaderException {
        return asFloat(context.value(), min, Float.MAX_VALUE);
    }

    public static float asFloat(ReaderContext<?> context, float min, float max) throws ReaderException {
        return asFloat(context.value(), min, max);
    }

    public static float asFloat(String value) throws ReaderException {
        return asFloat(value, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public static float asFloat(String value, float min, float max) throws ReaderException {
        try {
            var f = Float.parseFloat(value);
            if (f > max || f < min)
                throw new ReaderException("Float must be in range (" + min + ", " + (max != Integer.MAX_VALUE ? max : "∞") + ")");
            return f;
        } catch (ReaderException e) {
            throw e;
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

    public T get(String value) throws ReaderException {
        return function.apply(value);
    }
}
package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ReaderContext;
import net.treasure.util.logging.ComponentLogger;

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

    public static int asInt(ReaderContext<?> context) throws ReaderException {
        return asInt(context.value());
    }

    public static int asInt(String value) throws ReaderException {
        try {
            return Integer.parseInt(value);
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

    public T get(String value) {
        return function.apply(value);
    }
}
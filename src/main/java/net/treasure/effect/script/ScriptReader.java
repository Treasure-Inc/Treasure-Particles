package net.treasure.effect.script;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;

public interface ScriptReader<T> {

    T read(Effect effect, String line) throws ReaderException;

    default void error(Effect effect, String line, String... messages) throws ReaderException {
        var logger = TreasurePlugin.logger();
        logger.warning(effect.getPrefix() + "Couldn't read the line: " + line);
        for (String message : messages) {
            logger.warning("└ " + message);
        }
        throw new ReaderException();
    }

    default void error(Effect effect, String line, int start, int end, String... messages) throws ReaderException {
        var logger = TreasurePlugin.logger();
        line = "§6" + line.substring(0, start) + "§n" + line.substring(start, end) + "§r§6" + line.substring(end);
        logger.warning(effect.getPrefix() + "Couldn't read the line: " + line);
        for (String message : messages) {
            logger.warning("└ " + message);
        }
        throw new ReaderException();
    }
}
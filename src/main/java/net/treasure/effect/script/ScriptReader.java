package net.treasure.effect.script;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;

public interface ScriptReader<T> {
    T read(Effect effect, String line);

    default void error(String line, String... messages) {
        var logger = TreasurePlugin.logger();
        logger.warning("Couldn't read the line: " + line);
        for (String message : messages) {
            logger.warning("└ " + message);
        }
    }
}
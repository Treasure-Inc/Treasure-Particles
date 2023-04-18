package net.treasure.util.logging;

import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.util.message.MessageUtils;

import java.util.logging.Logger;

public class ComponentLogger {

    static Logger logger = TreasurePlugin.logger();
    @Setter
    static boolean colored = true;

    public static void error(ReaderContext<?> context, String... messages) {
        error(context.effect(), context.type(), context.line(), context.start(), context.end(), messages);
    }

    public static void error(Effect effect, String type, String line, String... messages) {
        if (colored) {
            MessageUtils.logParsed("<yellow>[TrElytraPlus] <red><effect><yellow><type> <line>",
                    Placeholder.unparsed("effect", effect.getPrefix()),
                    Placeholder.unparsed("type", type),
                    Placeholder.unparsed("line", line)
            );
            for (String message : messages)
                MessageUtils.logParsed("<gold>└ " + message);
            return;
        }

        logger.warning(effect.getPrefix() + line);
        for (String message : messages) {
            logger.warning("└ " + message);
        }
    }

    public static void error(Effect effect, String type, String line, int start, int end, String... messages) {
        if (!colored) {
            error(effect, type, line, messages);
            return;
        }

        line = "<yellow>" + line.substring(0, start) + "<gold><u>" + line.substring(start, end) + "</u></gold>" + line.substring(end);
        MessageUtils.logParsed("<yellow>[TrElytraPlus] <red><effect><yellow><type> <line>",
                Placeholder.unparsed("effect", effect.getPrefix()),
                Placeholder.unparsed("type", type),
                Placeholder.parsed("line", line)
        );
        for (String message : messages) {
            MessageUtils.logParsed("<gold>└ " + message);
        }
    }
}
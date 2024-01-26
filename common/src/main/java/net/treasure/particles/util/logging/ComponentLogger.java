package net.treasure.particles.util.logging;

import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentLogger {

    private static final String TAG = "TrParticles";
    private static final Logger logger = TreasureParticles.getPlugin().getLogger();
    @Setter
    private static boolean colored = true;
    @Setter
    private static boolean chatLogsEnabled = true;
    @Setter
    private static CommandSender chatReceiver;

    // Console Logs
    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
        if (chatLogsEnabled && chatReceiver != null)
            MessageUtils.sendParsed(chatReceiver, "<yellow>[" + TAG + "] " + message);
    }

    // Errors
    public static void error(ConfigurationGenerator generator, String error, String... messages) {
        error("[" + generator.getFileName() + "]", error, messages);
    }


    // Effect Errors
    public static void error(Effect effect, String error, String... messages) {
        error(effect.getPrefix(), error, messages);
    }

    public static void error(Effect effect, String type, String line, String... messages) {
        error(effect.getPrefix(), type + " " + line, messages);
    }

    public static void error(ReaderContext<?> context, String... messages) {
        error(context.effect(), context.type(), context.line(), context.start(), context.end(), messages);
    }

    public static void error(Effect effect, String type, String line, int start, int end, String... messages) {
        if (!colored) {
            error(effect, type, line, messages);
            return;
        }

        error(effect, type, "<yellow>" + line.substring(0, start) + "<gold><u>" + line.substring(start, end) + "</u></gold>" + line.substring(end), messages);
    }

    public static void error(String prefix, String error, String... messages) {
        var parsed = MessageUtils.parse("<red><prefix> <yellow><error>",
                Placeholder.unparsed("prefix", prefix),
                Placeholder.parsed("error", error)
        );
        for (var message : messages) parsed = parsed.appendNewline().append(MessageUtils.parse("<gold>└ " + message));

        if (!colored) {
            logger.warning(prefix + " " + error);
            for (var message : messages) logger.warning("└ " + message);
            if (chatLogsEnabled && chatReceiver instanceof Player) MessageUtils.send(chatReceiver, parsed);
            return;
        }

        MessageUtils.sendConsole(Component.text("[" + TAG + "] ", NamedTextColor.YELLOW).append(parsed));
        if (chatLogsEnabled && chatReceiver instanceof Player) MessageUtils.send(chatReceiver, parsed);
    }
}
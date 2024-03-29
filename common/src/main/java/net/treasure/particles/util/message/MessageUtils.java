package net.treasure.particles.util.message;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.locale.Translations;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

@Accessors(fluent = true)
public class MessageUtils {

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .resolvers(
                            TagResolver.resolver("prefix", (args, context) -> Tag.inserting(parse(Translations.PREFIX))),
                            TagResolver.resolver("discord", (args, context) -> Tag.styling(ClickEvent.openUrl("https://discord.com/invite/qQbePCtSjh/"))),
                            TagResolver.resolver("download", (args, context) -> Tag.styling(ClickEvent.openUrl("https://builtbybit.com/resources/26794/"))),
                            TagResolver.resolver("wiki", (args, context) -> Tag.styling(ClickEvent.openUrl("https://treasurestore.gitbook.io/treasure-particles/")))

                    ).build()
            ).build();
    private static final BukkitAudiences adventure;
    @Getter
    private static final LegacyComponentSerializer serializer;
    private static final GsonComponentSerializer gsonSerializer;

    static {
        adventure = TreasureParticles.adventure();
        serializer = BukkitComponentSerializer.legacy();
        gsonSerializer = BukkitComponentSerializer.gson();
    }

    // Parsers
    public static Component parse(String message, TagResolver... resolvers) {
        return miniMessage.deserialize(message, resolvers);
    }

    public static String parseLegacy(String message, Object... arguments) {
        return serializer.serialize(miniMessage.deserialize(MessageFormat.format(message, arguments)));
    }

    public static String gui(String message, Object... arguments) {
        return serializer.serialize(miniMessage.deserialize(MessageFormat.format(message, arguments)).colorIfAbsent(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
    }

    public static String gui(String message, TextColor color, Object... arguments) {
        return serializer.serialize(miniMessage.deserialize(MessageFormat.format(message, arguments)).colorIfAbsent(color).decoration(TextDecoration.ITALIC, false));
    }

    public static String json(String message, Object... arguments) {
        return gsonSerializer.serialize(miniMessage.deserialize(MessageFormat.format(message, arguments)));
    }

    // Console
    public static void sendConsole(Component message) {
        adventure.console().sendMessage(message);
    }

    public static void sendConsoleParsed(String message, TagResolver... resolvers) {
        adventure.console().sendMessage(miniMessage.deserialize(message, resolvers));
    }

    // Command Sender
    public static void send(CommandSender sender, Component message) {
        adventure.sender(sender).sendMessage(message);
    }

    public static void sendParsed(CommandSender sender, String message) {
        adventure.sender(sender).sendMessage(miniMessage.deserialize(message));
    }

    public static void sendParsed(CommandSender sender, String message, Object... arguments) {
        adventure.sender(sender).sendMessage(miniMessage.deserialize(MessageFormat.format(message, arguments)));
    }

    public static void sendActionBar(CommandSender sender, Component message) {
        adventure.sender(sender).sendActionBar(message);
    }

    public static void sendActionBarParsed(CommandSender sender, String message) {
        sendActionBar(sender, miniMessage.deserialize(message));
    }

    public static void sendTitle(CommandSender sender, Title title) {
        adventure.sender(sender).showTitle(title);
    }

    public static void sendTitleParsed(CommandSender sender, String title, String subtitle, long fadeIn, long stay, long fadeOut) {
        sendTitle(sender, Title.title(
                title == null ? Component.empty() : miniMessage.deserialize(title),
                subtitle == null ? Component.empty() : miniMessage.deserialize(subtitle),
                Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut))
        ));
    }
}
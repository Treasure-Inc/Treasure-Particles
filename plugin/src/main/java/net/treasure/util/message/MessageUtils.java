package net.treasure.util.message;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.treasure.core.TreasurePlugin;
import net.treasure.locale.Translations;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class MessageUtils {

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .resolvers(
                            Placeholder.parsed("prefix", Translations.PREFIX),
                            TagResolver.resolver("discord", (args, context) -> Tag.styling(ClickEvent.openUrl("https://discord.com/invite/qQbePCtSjh/"))),
                            TagResolver.resolver("download", (args, context) -> Tag.styling(ClickEvent.openUrl("https://builtbybit.com/resources/26794/"))),
                            TagResolver.resolver("wiki", (args, context) -> Tag.styling(ClickEvent.openUrl("https://treasurestore.gitbook.io/treasure-elytra/")))

                    ).build()
            ).build();
    private static final BukkitAudiences adventure;
    @Getter
    private static final LegacyComponentSerializer serializer;

    static {
        adventure = TreasurePlugin.getInstance().adventure();
        serializer = BukkitComponentSerializer.legacy();
    }

    public static void logParsed(String message, TagResolver.Single... resolvers) {
        adventure.console().sendMessage(miniMessage.deserialize(message, resolvers));
    }

    public static void send(CommandSender sender, Component message) {
        adventure.sender(sender).sendMessage(message);
    }

    public static void sendParsed(CommandSender sender, String message) {
        adventure.sender(sender).sendMessage(miniMessage.deserialize(message));
    }

    public static void sendParsed(CommandSender sender, String message, Object... arguments) {
        adventure.sender(sender).sendMessage(miniMessage.deserialize(MessageFormat.format(message, arguments)));
    }

    public static void openBook(CommandSender sender, List<String> messages) {
        var audience = adventure.sender(sender);
        List<Component> components = new ArrayList<>();
        for (String message : messages)
            components.add(miniMessage.deserialize(message));
        audience.openBook(Book.book(
                Component.text("TreasureElytra"),
                Component.text("ItsZypec"),
                components
        ));
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

    public static Component parse(String message) {
        return miniMessage.deserialize(message);
    }

    public static String parseLegacy(String message, Object... arguments) {
        return serializer.serialize(miniMessage.deserialize(MessageFormat.format(message, arguments)));
    }
}
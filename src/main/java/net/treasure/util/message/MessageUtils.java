package net.treasure.util.message;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.treasure.core.TreasurePlugin;
import org.bukkit.command.CommandSender;

@Accessors(fluent = true)
public class MessageUtils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final BukkitAudiences adventure;
    @Getter
    private static final LegacyComponentSerializer serializer;

    static {
        adventure = TreasurePlugin.getInstance().adventure();
        serializer = BukkitComponentSerializer.legacy();
    }

    public static void send(CommandSender sender, Component message) {
        adventure.sender(sender).sendMessage(message);
    }

    public static void sendParsed(CommandSender sender, String message) {
        adventure.sender(sender).sendMessage(miniMessage.deserialize(message));
    }

    public static void sendActionBar(CommandSender sender, Component message) {
        adventure.sender(sender).sendActionBar(message);
    }

    public static void sendActionBarParsed(CommandSender sender, String message) {
        adventure.sender(sender).sendActionBar(miniMessage.deserialize(message));
    }

    public static Component parse(String message) {
        return miniMessage.deserialize(message);
    }

    public static String parseLegacy(String message) {
        return serializer.serialize(miniMessage.deserialize(message));
    }
}
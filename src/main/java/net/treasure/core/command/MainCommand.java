package net.treasure.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.EffectsGUI;
import net.treasure.effect.Effect;
import net.treasure.locale.Messages;
import net.treasure.util.message.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@CommandAlias("treasureelytra|trelytra|tre")
public class MainCommand extends BaseCommand {

    TreasurePlugin plugin;

    @HelpCommand
    @CommandPermission("%basecmd")
    public void menu(Player player) {
        new EffectsGUI().open(player, 0);
    }

    @Subcommand("toggle")
    public void toggleEffects(Player player) {
        var data = plugin.getPlayerManager().getPlayerData(player);
        data.setEffectsEnabled(!data.isEffectsEnabled());
        MessageUtils.sendParsed(player, String.format(Messages.EFFECT_TOGGLE, data.isEffectsEnabled() ? Messages.ENABLED : Messages.DISABLED));
    }

    @CommandPermission("%basecmd")
    @Subcommand("select|sel")
    @CommandCompletion("@effects")
    public void selectEffect(Player player, Effect effect) {
        if (!effect.canUse(player)) {
            MessageUtils.sendParsed(player, Messages.EFFECT_NO_PERMISSION);
            return;
        }
        plugin.getPlayerManager().getPlayerData(player).setCurrentEffect(player, effect);
        MessageUtils.sendParsed(player, String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
    }

    @CommandPermission("%basecmd")
    @Subcommand("reset")
    public void resetEffect(Player player) {
        plugin.getPlayerManager().getPlayerData(player).setCurrentEffect(player, null);
        player.sendMessage(Messages.EFFECT_RESET);
    }

    @Subcommand("reload|rl")
    @CommandPermission("%admincmd")
    public void reload(CommandSender sender) {
        MessageUtils.sendParsed(sender, Messages.RELOADING);
        plugin.reload();
        MessageUtils.sendParsed(sender, Messages.RELOADED);
    }

    @Subcommand("changelog|changes|updates")
    @CommandCompletion("@versions")
    @CommandPermission("%changelog")
    public void changelog(CommandSender sender, @Single @Optional String version) {
        var v = version;
        if (v == null)
            v = plugin.getDescription().getVersion();
        var notificationManager = plugin.getNotificationManager();
        List<String> changelog = notificationManager.changelog(v);
        if (changelog == null) {
            MessageUtils.sendParsed(sender, Messages.PREFIX + (version == null ? "No changelog for latest version. " : " Unknown version: " + v));
            return;
        }
        changelog.add(0, "<br><br><br><br><br><br><dark_aqua><b>Version " + v);
        MessageUtils.openBook(sender, changelog);
    }

    @Subcommand("notifications")
    @CommandPermission("%notifications")
    public void toggleNotifications(Player player) {
        var data = plugin.getPlayerManager().getPlayerData(player);
        data.setNotificationsEnabled(!data.isNotificationsEnabled());
        MessageUtils.sendParsed(player, String.format(Messages.NOTIFICATIONS_TOGGLE, data.isNotificationsEnabled() ? Messages.ENABLED : Messages.DISABLED));
    }
}
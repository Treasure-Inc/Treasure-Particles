package net.treasure.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.EffectsGUI;
import net.treasure.effect.Effect;
import net.treasure.locale.Messages;
import net.treasure.util.message.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("treasureelytra|trelytra|tre")
public class MainCommand extends BaseCommand {

    @HelpCommand
    @CommandPermission("%basecmd")
    public void menu(Player player) {
        new EffectsGUI().open(player, 0);
    }

    @Subcommand("toggle")
    public void toggle(Player player) {
        var data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);
        data.setEffectsEnabled(!data.isEffectsEnabled());
        MessageUtils.sendParsed(player, String.format(Messages.EFFECT_TOGGLE, data.isEffectsEnabled() ? Messages.ENABLED : Messages.DISABLED));
    }

    @Private
    @CommandPermission("%basecmd")
    @Subcommand("select|sel")
    @CommandCompletion("@effects")
    public void selectEffect(Player player, Effect effect) {
        if (!effect.canUse(player)) {
            MessageUtils.sendParsed(player, Messages.EFFECT_NO_PERMISSION);
            return;
        }
        TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player).setCurrentEffect(player, effect);
        MessageUtils.sendParsed(player, String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
    }

    @Subcommand("reload|rl")
    @CommandPermission("%admincmd")
    public void reload(CommandSender sender) {
        MessageUtils.sendParsed(sender, Messages.RELOADING);
        TreasurePlugin.getInstance().reload();
        MessageUtils.sendParsed(sender, Messages.RELOADED);
    }

    @Subcommand("changes|changelog|updates")
    @CommandCompletion("@versions")
    public void changelog(CommandSender sender, @Single @Optional String version) {
        var v = version;
        if (v == null)
            v = TreasurePlugin.getInstance().getDescription().getVersion();
        var notificationManager = TreasurePlugin.getInstance().getNotificationManager();
        List<String> changelog = notificationManager.changelog(v);
        if (changelog == null) {
            MessageUtils.sendParsed(sender, Messages.PREFIX + (version == null ? "No changelog for latest version. " : " Unknown version: " + v));
            return;
        }
        MessageUtils.sendParsed(sender, Messages.PREFIX + "<aqua><b>v" + v);
        for (String s : changelog)
            MessageUtils.sendParsed(sender, s);
    }
}
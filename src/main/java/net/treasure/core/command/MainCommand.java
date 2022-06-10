package net.treasure.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.EffectsGUI;
import net.treasure.core.command.gui.task.GUIUpdater;
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

    @Default
    @CatchUnknown
    @CommandPermission("%basecmd")
    public void menu(Player player, @Default("0") int page) {
        page = Math.max(0, page - 1);
        int maxPage = (plugin.getEffectManager().getEffects().size() / 28) + 1;
        if (page >= maxPage)
            page = maxPage - 1;
        new EffectsGUI().open(player, page);
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
    public void resetEffect(Player sender, @Optional @CommandPermission("%admincmd") OnlinePlayer reset) {
        boolean self = reset == null || reset.player.equals(sender);
        Player player = self ? sender : reset.player;
        plugin.getPlayerManager().getPlayerData(player).setCurrentEffect(player, null);
        MessageUtils.sendParsed(player, Messages.EFFECT_RESET);
        if (!self)
            MessageUtils.sendParsed(sender, String.format(Messages.EFFECT_RESET_OTHER, reset.player.getName()));
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
        changelog.add(0, "<br><br><br><br><br><dark_aqua><b>Version " + v +
                "<reset><br>• <hover:show_text:'<dark_aqua>Click!'><click:open_url:'https://github.com/Treasure-Inc/Treasure-Elytra/wiki'>Wiki Page</click></hover>" +
                "<br>• <hover:show_text:'<dark_aqua>Click!'><click:open_url:'https://www.spigotmc.org/resources/trelytra-let-your-elytra-create-wonderful-particles.99860/'>Spigot Page</click></hover>");
        MessageUtils.openBook(sender, changelog);
    }

    @Subcommand("notifications")
    @CommandPermission("%notifications")
    public void toggleNotifications(Player player) {
        var data = plugin.getPlayerManager().getPlayerData(player);
        data.setNotificationsEnabled(!data.isNotificationsEnabled());
        MessageUtils.sendParsed(player, String.format(Messages.NOTIFICATIONS_TOGGLE, data.isNotificationsEnabled() ? Messages.ENABLED : Messages.DISABLED));
    }

    @Private
    @CommandPermission("%admincmd")
    @Subcommand("debug")
    public void debug(CommandSender sender) {
        MessageUtils.sendParsed(sender, Messages.PREFIX + "<gray>Menu Viewers: <yellow>" + GUIUpdater.getPlayers().size());
        MessageUtils.sendParsed(sender, Messages.PREFIX + "<gray>Players Using Elytra Effect: <yellow>" + plugin.getPlayerManager().getPlayersData().values().stream().filter(data -> data.isEnabled() && data.getCurrentEffect() != null).count());
    }
}
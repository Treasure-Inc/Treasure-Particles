package net.treasure.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.gui.EffectsGUI;
import net.treasure.util.locale.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("trelytra|tre|te")
public class MainCommand extends BaseCommand {

    @HelpCommand
    public void cue(Player player) {
        new EffectsGUI().open(player, 0);
    }

    @Private
    @Subcommand("select|sel")
    @CommandCompletion("@effects")
    public void selectEffect(Player player, Effect effect) {
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) {
            player.sendMessage(Messages.EFFECT_NO_PERMISSION);
            return;
        }
        TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player).setCurrentEffect(player, effect);
        player.sendMessage(String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
    }

    @Subcommand("reload|rl")
    @CommandPermission("trelytra.admin")
    public void reload(CommandSender sender) {
        sender.sendMessage(Messages.RELOADING);
        TreasurePlugin.getInstance().reloadConfig();
        TreasurePlugin.getInstance().getEffectManager().reload();
        TreasurePlugin.getInstance().getColorManager().reload();
        TreasurePlugin.getInstance().getPlayerManager().reload();
        sender.sendMessage(Messages.RELOADED);
    }
}
package net.cladium.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.cladium.core.CladiumPlugin;
import net.cladium.effect.Effect;
import net.cladium.gui.EffectsGUI;
import net.cladium.util.locale.Messages;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("celytra|ce")
public class MainCommand extends BaseCommand {

    @HelpCommand
    public void cue(Player player) {
        new EffectsGUI().open(player, 0);
    }

    @Subcommand("select|sel")
    @CommandCompletion("@effects")
    public void selectEffect(Player player, Effect effect) {
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) {
            player.sendMessage(Messages.EFFECT_NO_PERMISSION);
            return;
        }
        CladiumPlugin.getInstance().getPlayerManager().getPlayerData(player).setCurrentEffect(player, effect);
        player.sendMessage(String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
    }

    @Subcommand("reload|rl")
    @CommandPermission("celytra.admin")
    public void reload(CommandSender sender) {
        sender.sendMessage(Messages.RELOADING);
        CladiumPlugin.getInstance().reloadConfig();
        CladiumPlugin.getInstance().getEffectManager().reload();
        CladiumPlugin.getInstance().getColorManager().reload();
        CladiumPlugin.getInstance().getPlayerManager().reload();
        sender.sendMessage(Messages.RELOADED);
    }
}
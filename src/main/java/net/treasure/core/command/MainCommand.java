package net.treasure.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.EffectsGUI;
import net.treasure.core.gui.task.GUIUpdater;
import net.treasure.effect.Effect;
import net.treasure.locale.Locale;
import net.treasure.locale.Translations;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
@CommandAlias("trelytra|treasureelytra|tre")
public class MainCommand extends BaseCommand {

    final TreasurePlugin plugin;

    @Default
    @CatchUnknown
    @CommandPermission("%basecmd")
    public void menu(Player player, @Default("0") @Name("%page") int page) {
        page = Math.max(0, page - 1);
        int maxPage = (plugin.getEffectManager().getEffects().size() / 28) + 1;
        if (page >= maxPage)
            page = maxPage - 1;
        EffectsGUI.open(player, page);
    }

    @Subcommand("toggle")
    public void toggleEffects(Player player) {
        var data = plugin.getPlayerManager().getEffectData(player);
        data.setEffectsEnabled(!data.isEffectsEnabled());
        MessageUtils.sendParsed(player, String.format(Translations.EFFECT_TOGGLE, data.isEffectsEnabled() ? Translations.ENABLED : Translations.DISABLED));
    }

    @CommandPermission("%basecmd")
    @Subcommand("select|sel")
    @CommandCompletion("@effects")
    public void selectEffect(Player player, @Name("%effect") @Single String key) {
        var effect = plugin.getEffectManager().get(key);
        if (effect == null) {
            MessageUtils.sendParsed(player, String.format(Translations.EFFECT_UNKNOWN, key));
            return;
        }
        if (!effect.canUse(player)) {
            MessageUtils.sendParsed(player, Translations.EFFECT_NO_PERMISSION);
            return;
        }
        plugin.getPlayerManager().getEffectData(player).setCurrentEffect(player, effect);
        MessageUtils.sendParsed(player, String.format(Translations.EFFECT_SELECTED, effect.getDisplayName()));
    }

    @CommandPermission("%basecmd")
    @Subcommand("reset")
    public void resetEffect(Player sender, @Optional @CommandPermission("%admincmd") OnlinePlayer reset) {
        boolean self = reset == null || reset.player.equals(sender);
        var player = self ? sender : reset.player;
        plugin.getPlayerManager().getEffectData(player).setCurrentEffect(player, null);
        MessageUtils.sendParsed(player, Translations.EFFECT_RESET);
        if (!self)
            MessageUtils.sendParsed(sender, String.format(Translations.EFFECT_RESET_OTHER, reset.player.getName()));
    }

    @Subcommand("reload|rl")
    @CommandPermission("%admincmd")
    public void reload(CommandSender sender) {
        MessageUtils.sendParsed(sender, Translations.RELOADING);
        plugin.reload();
        MessageUtils.sendParsed(sender, Translations.RELOADED);
    }

    @Subcommand("locale")
    @CommandPermission("%admincmd")
    public void locale(CommandSender sender, @Optional @Single String locale) {
        if (locale == null) {
            boolean supported = Locale.isSupported(Translations.LOCALE);
            MessageUtils.sendParsed(sender, "<prefix> <gray>Locale: <gold>" + Translations.LOCALE + "</gold> (<i>" + (supported ? "<green>supported" : "not supported") + "</i>)");
            MessageUtils.sendParsed(sender, String.format(Translations.COMMAND_USAGE, "/trelytra locale [locale]"));
            return;
        }
        if (locale.equals(Translations.LOCALE)) {
            MessageUtils.sendParsed(sender, "<prefix> <gray>Locale already set to " + locale);
            return;
        }
        var oldLocale = Translations.LOCALE;
        plugin.getConfig().set("locale", locale);
        plugin.saveConfig();
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.reload();
            if (oldLocale.equals(Translations.LOCALE)) {
                MessageUtils.sendParsed(sender, "<prefix> <red>Couldn't set locale to " + locale);
                return;
            }
            MessageUtils.sendParsed(sender, "<prefix> <gray>Set locale to <gold>" + locale);
        });
    }

    @Subcommand("notifications")
    @CommandPermission("%notification")
    public void toggleNotifications(Player player) {
        var data = plugin.getPlayerManager().getEffectData(player);
        data.setNotificationsEnabled(!data.isNotificationsEnabled());
        MessageUtils.sendParsed(player, String.format(Translations.NOTIFICATIONS_TOGGLE, data.isNotificationsEnabled() ? Translations.ENABLED : Translations.DISABLED));
    }

    @Private
    @CommandPermission("%admincmd")
    @Subcommand("debug toggle")
    public void debugToggle(Player player) {
        var data = plugin.getPlayerManager().getEffectData(player);
        data.setDebugModeEnabled(!data.isDebugModeEnabled());
        MessageUtils.sendParsed(player, "<prefix> <gray>Debug Mode Enabled: " + data.isDebugModeEnabled());
    }

    @Private
    @CommandPermission("%admincmd")
    @Subcommand("debug effect")
    public void debugEffect(Player player) {
        var data = plugin.getPlayerManager().getEffectData(player);
        data.setEnabled(!data.isEnabled());
        MessageUtils.sendParsed(player, "<prefix> <gray>Enabled: " + data.isEnabled());
    }

    @Private
    @CommandPermission("%admincmd")
    @Subcommand("debug menu")
    public void debugMenu(CommandSender sender) {
        MessageUtils.sendParsed(sender, "<prefix> <gray>Menu Viewers Size: <yellow>" + GUIUpdater.getPlayers().size());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Players Using Elytra Effect: <yellow>" + plugin.getPlayerManager().getData().values().stream().filter(data -> data.isEnabled() && data.getCurrentEffect() != null).count());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Color Cycle Speed: <gold>" + plugin.guiColorCycleSpeed());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Animation Interval: <gold>" + plugin.guiInterval());
    }

    @Private
    @CommandPermission("%admincmd")
    @Subcommand("debug effects")
    public void debug(Player player) {
        var effects = plugin.getEffectManager().getEffects();
        if (effects.isEmpty()) {
            MessageUtils.sendParsed(player, "<prefix> <gray>No.");
            return;
        }
        var data = plugin.getPlayerManager().getEffectData(player);
        new BukkitRunnable() {

            int index = -1;
            int i = 0;
            Effect current = null;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (i % 7 == 0) {
                    index++;
                    try {
                        current = effects.get(index);
                    } catch (Exception e) {
                        MessageUtils.sendParsed(player, "<prefix> <dark_red>Cancelled.");
                        cancel();
                        return;
                    }
                    data.setCurrentEffect(player, current);
                    MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED.formatted(current.getDisplayName()));
                    MessageUtils.sendParsed(Bukkit.getConsoleSender(), Translations.EFFECT_SELECTED.formatted(current.getDisplayName()));
                }
                if (current == null) {
                    MessageUtils.sendParsed(player, "<prefix> <red>Cancelled.");
                    cancel();
                    return;
                }
                var dir = player.getLocation().getDirection();
                player.setVelocity(dir.setX(dir.getX() * 1.25).setZ(dir.getZ() * 1.25));
                i++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
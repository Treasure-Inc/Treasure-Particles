package net.treasure.particles.command;

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
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.TreasurePlugin;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.EffectManager;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.gui.task.GUITask;
import net.treasure.particles.locale.Locale;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

@CommandAlias("trp|trparticles|treasureparticles")
public class MainCommand extends BaseCommand {

    final TreasurePlugin plugin;
    final PlayerManager playerManager;
    final EffectManager effectManager;
    final GUIManager guiManager;

    public MainCommand(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.playerManager = TreasureParticles.getPlayerManager();
        this.effectManager = TreasureParticles.getEffectManager();
        this.guiManager = TreasureParticles.getGUIManager();
    }

    @Default
    @CatchUnknown
    @CommandPermission(Permissions.COMMAND_BASE)
    public void menu(Player player, @Default("0") @Name("%page") int page) {
        page = Math.max(0, page - 1);
        int maxPage = (effectManager.getEffects().size() / 28) + 1;
        if (page >= maxPage)
            page = maxPage - 1;
        guiManager.effectsGUI().open(player, page);
    }

    @Subcommand("toggle")
    public void toggleEffects(Player player) {
        var data = playerManager.getEffectData(player);
        data.setEffectsEnabled(!data.isEffectsEnabled());
        MessageUtils.sendParsed(player, Translations.EFFECT_TOGGLE, data.isEffectsEnabled() ? Translations.ENABLED : Translations.DISABLED);
    }

    @Subcommand("mixer")
    @CommandPermission(Permissions.COMMAND_MIXER)
    public void mixer(CommandSender sender, @Optional @Name("%mix-name") String mixName, @Optional @CommandPermission(Permissions.COMMAND_ADMIN) OnlinePlayer select) {
        if (mixName == null) {
            if (!(sender instanceof Player player)) {
                MessageUtils.sendParsed(sender, Translations.COMMAND_PLAYERS_ONLY);
                return;
            }

            var data = playerManager.getEffectData(player);
            if (!data.canCreateAnotherMix()) {
                MessageUtils.sendParsed(player, Translations.COMMAND_NO_PERMISSION);
                return;
            }

            guiManager.mixerGUI().open(player);
            return;
        }

        boolean self = select == null || select.player.equals(sender);
        if (self && !(sender instanceof Player)) {
            MessageUtils.sendParsed(sender, Translations.COMMAND_PLAYERS_ONLY);
            return;
        }

        var player = self ? (Player) sender : select.player;
        var data = playerManager.getEffectData(player);
        var o = data.getMixData().stream().filter(mixData -> mixData.name().replaceAll(" ", "").equalsIgnoreCase(mixName)).findFirst();
        if (o.isEmpty()) {
            MessageUtils.sendParsed(sender, Translations.MIX_UNKNOWN, mixName);
            return;
        }

        var mix = o.get();
        var effect = mix.get(player);
        if (effect == null) {
            data.getMixData().remove(mix);
            MessageUtils.sendParsed(sender, Translations.MIX_UNKNOWN, mixName);
            return;
        }

        data.setCurrentEffect(effect);
        MessageUtils.sendParsed(player, Translations.MIX_SELECTED, mix.name());

        if (!self)
            MessageUtils.sendParsed(sender, Translations.MIX_SELECTED_OTHER, player.getName(), mix.name());
    }

    @Subcommand("select|sel")
    @CommandCompletion("@effects *")
    @CommandPermission(Permissions.COMMAND_BASE)
    public void selectEffect(CommandSender sender, @Name("%effect") String key, @Optional @CommandPermission(Permissions.COMMAND_ADMIN) OnlinePlayer select) {
        var effect = effectManager.get(key);
        if (effect == null) {
            MessageUtils.sendParsed(sender, Translations.EFFECT_UNKNOWN, key);
            return;
        }

        boolean self = select == null || select.player.equals(sender);
        if (self && !(sender instanceof Player)) {
            MessageUtils.sendParsed(sender, Translations.COMMAND_PLAYERS_ONLY);
            return;
        }

        var player = self ? (Player) sender : select.player;
        if ((self || Permissions.ALWAYS_CHECK_PERMISSION) && !effect.canUse(player)) {
            MessageUtils.sendParsed(sender, self ? Translations.EFFECT_NO_PERMISSION : Translations.EFFECT_NO_PERMISSION_OTHER);
            return;
        }

        playerManager.getEffectData(player).setCurrentEffect(effect);
        MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());

        if (!self)
            MessageUtils.sendParsed(sender, Translations.EFFECT_SELECTED_OTHER, player.getName(), effect.getDisplayName());
    }

    @Subcommand("random")
    @CommandPermission(Permissions.COMMAND_BASE)
    @CommandCompletion("* true|false")
    public void randomEffect(CommandSender sender, @Optional @CommandPermission(Permissions.COMMAND_ADMIN) OnlinePlayer select, @Optional @Default("false") boolean all) {
        boolean self = select == null || select.player.equals(sender);
        if (self && !(sender instanceof Player)) {
            MessageUtils.sendParsed(sender, Translations.COMMAND_PLAYERS_ONLY);
            return;
        }
        var player = self ? (Player) sender : select.player;

        var effects = effectManager.getEffects().stream().filter(effect -> all || effect.canUse(player)).toList();
        if (effects.isEmpty()) {
            MessageUtils.sendParsed(sender, self ? Translations.CANNOT_USE_ANY_EFFECT : Translations.CANNOT_USE_ANY_EFFECT_OTHER);
            return;
        }

        var effect = effects.get(new Random().nextInt(effects.size()));
        playerManager.getEffectData(player).setCurrentEffect(effect);
        MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());

        if (!self)
            MessageUtils.sendParsed(sender, Translations.EFFECT_SELECTED_OTHER, player.getName(), effect.getDisplayName());
    }

    @Subcommand("reset")
    @CommandPermission(Permissions.COMMAND_BASE)
    public void resetEffect(CommandSender sender, @Optional @CommandPermission(Permissions.COMMAND_ADMIN) OnlinePlayer reset) {
        boolean self = reset == null || reset.player.equals(sender);
        if (self && !(sender instanceof Player)) {
            MessageUtils.sendParsed(sender, Translations.COMMAND_PLAYERS_ONLY);
            return;
        }

        var player = self ? (Player) sender : reset.player;
        playerManager.getEffectData(player).setCurrentEffect(null);
        MessageUtils.sendParsed(player, Translations.EFFECT_RESET);

        if (!self)
            MessageUtils.sendParsed(sender, Translations.EFFECT_RESET_OTHER, player.getName());
    }

    @Subcommand("color")
    @CommandCompletion("@effects @groupColors")
    @CommandPermission(Permissions.COMMAND_BASE)
    public void selectGroupColor(Player player, @Name("%effect") String key, @Single String color) {
        var effect = effectManager.get(key);
        if (effect == null) {
            MessageUtils.sendParsed(player, Translations.EFFECT_UNKNOWN, key);
            return;
        }

        if (!effect.canUse(player)) {
            MessageUtils.sendParsed(player, Translations.EFFECT_NO_PERMISSION);
            return;
        }

        var colorScheme = TreasureParticles.getColorManager().getColorScheme(color);
        if (colorScheme == null) {
            MessageUtils.sendParsed(player, Translations.UNKNOWN_COLOR_SCHEME);
            return;
        }

        var group = effect.getColorGroup();
        if (group == null || !group.hasOption(colorScheme)) {
            MessageUtils.sendParsed(player, Translations.UNKNOWN_COLOR_SCHEME);
            return;
        }

        playerManager.getEffectData(player).setColorPreference(effect, colorScheme);
        MessageUtils.sendParsed(player, Translations.COLOR_SCHEME_SELECTED, colorScheme.getDisplayName(), effect.getDisplayName());
    }

    @Subcommand("admin")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void admin(Player player) {
        guiManager.adminGUI().open(player, null, null, 0);
    }

    @Subcommand("reload|rl")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void reload(CommandSender sender) {
        MessageUtils.sendParsed(sender, Translations.RELOADING);
        TreasureParticles.reload(sender);
        MessageUtils.sendParsed(sender, Translations.RELOADED);
    }

    @Subcommand("locale")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void locale(CommandSender sender, @Optional @Single String locale) {
        if (locale == null) {
            boolean supported = Locale.isSupported(Translations.LOCALE);
            MessageUtils.sendParsed(sender, "<prefix> <gray>Locale: <gold>" + Translations.LOCALE + "</gold> (<i>" + (supported ? "<green>supported" : "not supported") + "</i>)");
            MessageUtils.sendParsed(sender, Translations.COMMAND_USAGE, "/trelytra locale [locale]");
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
            TreasureParticles.reload(sender);
            if (oldLocale.equals(Translations.LOCALE)) {
                MessageUtils.sendParsed(sender, "<prefix> <red>Couldn't set locale to " + locale);
                return;
            }
            MessageUtils.sendParsed(sender, "<prefix> <gray>Set locale to <gold>" + locale);
        });
    }

    @Subcommand("notifications")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void toggleNotifications(Player player) {
        var data = playerManager.getEffectData(player);
        data.setNotificationsEnabled(!data.isNotificationsEnabled());
        MessageUtils.sendParsed(player, Translations.NOTIFICATIONS_TOGGLE, data.isNotificationsEnabled() ? Translations.ENABLED : Translations.DISABLED);
    }

    @Private
    @Subcommand("debug info")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void debugInfo(CommandSender sender) {
        MessageUtils.sendParsed(sender, "<prefix> <gray>Effects Size: <red>" + effectManager.getEffects().size());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Animated Menu Viewers: <yellow>" + GUITask.getPlayers().size());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Players Using Effect: <yellow>" + playerManager.getData().values().stream().filter(data -> data.isEnabled() && data.getCurrentEffect() != null).count());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Color Cycle Speed: <gold>" + TreasureParticles.getGUIManager().getColorCycleSpeed());
        MessageUtils.sendParsed(sender, "<prefix> <gray>Animation Interval: <gold>" + TreasureParticles.getGUIManager().getInterval());
    }

    @Private
    @Subcommand("debug permission")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void debugPermissions(CommandSender sender) {
        MessageUtils.sendParsed(sender, "<prefix> <gray>Admin Command: <red>" + Permissions.ADMIN);
        MessageUtils.sendParsed(sender, "<prefix> <gray>Base Command: <red>" + Permissions.BASE);
        MessageUtils.sendParsed(sender, "<prefix> <gray>Effect Mixer: <yellow>" + Permissions.MIXER);
        MessageUtils.sendParsed(sender, "<prefix> <gray>Can See Effects: <gold>" + Permissions.CAN_SEE_EFFECTS);
        MessageUtils.sendParsed(sender, "<prefix> <gray>Access All Effects: <gold>" + Permissions.ACCESS_ALL_EFFECTS);
    }

    @Private
    @Subcommand("debug start")
    @CommandPermission(Permissions.COMMAND_ADMIN)
    public void debug(Player player) {
        var effects = effectManager.getEffects();
        if (effects.isEmpty()) {
            MessageUtils.sendParsed(player, "<prefix> <gray>0 effect found.");
            return;
        }
        var data = playerManager.getEffectData(player);
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
                    data.setCurrentEffect(current);
                    MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, current.getDisplayName());
                    MessageUtils.sendParsed(Bukkit.getConsoleSender(), Translations.EFFECT_SELECTED, current.getDisplayName());
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
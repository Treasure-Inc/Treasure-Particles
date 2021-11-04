package net.cladium.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import lombok.Getter;
import net.cladium.color.ColorManager;
import net.cladium.command.MainCommand;
import net.cladium.core.database.Database;
import net.cladium.core.listener.JoinQuitListener;
import net.cladium.core.player.PlayerManager;
import net.cladium.effect.Effect;
import net.cladium.effect.EffectManager;
import net.cladium.gui.GUIListener;
import net.cladium.gui.task.GUIUpdater;
import net.cladium.util.locale.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CladiumPlugin extends JavaPlugin {

    @Getter
    private static CladiumPlugin instance;

    @Getter
    private Database database;

    @Getter
    private Messages messages;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private EffectManager effectManager;

    @Getter
    private ColorManager colorManager;

    @Getter
    private BukkitCommandManager commandManager;

    @Getter
    private Random random;

    @Override
    public void onEnable() {
        instance = this;
        random = new Random();

        database = new Database();
        if (!database.connect()) {
            disable();
            return;
        }

        messages = new Messages();
        messages.load();

        playerManager = new PlayerManager();

        effectManager = new EffectManager();
        if (!effectManager.load()) {
            disable();
            return;
        }
        effectManager.loadEffects();

        colorManager = new ColorManager();
        if (colorManager.load())
            colorManager.loadColors();

        commandManager = new BukkitCommandManager(this);
        commandManager.getCommandContexts().registerContext(
                Effect.class,
                Effect.getContextResolver());
        commandManager.registerCommand(new MainCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return random.nextBoolean();
        }));
        commandManager.getCommandCompletions().registerAsyncCompletion("effects", context -> effectManager.getEffects().stream().map(Effect::getKey).collect(Collectors.toList()));

        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(CladiumPlugin.getInstance(), new GUIUpdater(), 0, 2);
    }

    public void disable() {
        getLogger().log(Level.WARNING, "Couldn't initialize CElytra!");
        getPluginLoader().disablePlugin(this);
    }
}
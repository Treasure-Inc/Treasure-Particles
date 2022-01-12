package net.treasure.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import lombok.Getter;
import net.treasure.color.ColorManager;
import net.treasure.command.MainCommand;
import net.treasure.core.database.Database;
import net.treasure.core.listener.JoinQuitListener;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.gui.GUIListener;
import net.treasure.gui.task.GUIUpdater;
import net.treasure.util.locale.Messages;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TreasurePlugin extends JavaPlugin {

    @Getter
    private static TreasurePlugin instance;

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

    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        random = new Random();

        messages = new Messages();
        messages.load();

        effectManager = new EffectManager();
        if (!effectManager.load()) {
            disable();
            return;
        }

        database = new Database();
        if (!database.connect()) {
            disable();
            return;
        }

        playerManager = new PlayerManager();

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

        this.adventure = BukkitAudiences.create(this);

        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(TreasurePlugin.getInstance(), new GUIUpdater(), 0, 2);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public BukkitAudiences adventure() {
        return adventure;
    }

    public void disable() {
        getLogger().log(Level.WARNING, "Couldn't initialize CElytra!");
        getPluginLoader().disablePlugin(this);
    }
}
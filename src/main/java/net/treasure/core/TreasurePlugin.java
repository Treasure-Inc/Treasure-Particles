package net.treasure.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.lib.timings.MCTiming;
import co.aikar.commands.lib.timings.TimingManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.color.ColorManager;
import net.treasure.core.command.MainCommand;
import net.treasure.core.command.gui.GUIListener;
import net.treasure.core.command.gui.task.GUIUpdater;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.database.Database;
import net.treasure.core.listener.JoinQuitListener;
import net.treasure.core.notification.NotificationManager;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.locale.Messages;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public class TreasurePlugin extends JavaPlugin {

    @Getter
    private static TreasurePlugin instance;

    private static TimingManager timingManager;

    // Data Holders
    private Messages messages;
    private EffectManager effectManager;
    private ColorManager colorManager;
    private List<DataHolder> dataHolders;

    private Database database;
    private PlayerManager playerManager;
    private NotificationManager notificationManager;

    // ACF
    private BukkitCommandManager commandManager;

    @Accessors(fluent = true)
    private BukkitAudiences adventure;

    private boolean debugModeEnabled;
    private Random random;
    private int taskId = -5;

    @Override
    public void onEnable() {
        instance = this;
        timingManager = TimingManager.of(this);

        random = new Random();
        dataHolders = new ArrayList<>();
        debugModeEnabled = new File(getDataFolder(), "dev").exists();

        // Main Config
        saveDefaultConfig();
        configure();

        // Database
        database = new Database();
        if (!database.connect()) {
            disable();
            return;
        }

        // Initialize data holders
        messages = new Messages();
        messages.initialize();
        dataHolders.add(messages);

        effectManager = new EffectManager();
        if (!effectManager.initialize()) {
            disable();
            return;
        }
        dataHolders.add(effectManager);

        colorManager = new ColorManager();
        if (!colorManager.initialize()) {
            disable();
            return;
        }
        dataHolders.add(colorManager);

        var config = getConfig();

        // Notification Manager
        notificationManager = new NotificationManager();
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // Load colors & effects
        colorManager.loadColors();
        effectManager.loadEffects();

        playerManager = new PlayerManager();

        // Register command stuffs
        commandManager = new BukkitCommandManager(this);
        commandManager.getCommandContexts().registerContext(
                Effect.class,
                Effect.getContextResolver());
        commandManager.registerCommand(new MainCommand(this));
        var completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("effects", context -> effectManager.getEffects().stream().map(Effect::getKey).collect(Collectors.toList()));
        completions.registerStaticCompletion("versions", notificationManager.getVersions());
        var replacements = commandManager.getCommandReplacements();

        replacements.addReplacement("basecmd", config.getString("permissions.menu", "trelytra.menu"));
        replacements.addReplacement("admincmd", config.getString("permissions.admin", "trelytra.admin"));
        replacements.addReplacement("changelog", "trelytra.notifications");

        // Adventure
        this.adventure = BukkitAudiences.create(this);

        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        // Listeners & Tasks
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(this), this);
        pluginManager.registerEvents(new GUIListener(), this);
        if (config.getBoolean("gui.animation", true))
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, 2).getTaskId();

        var metrics = new Metrics(this, 14508);
        metrics.addCustomChart(new SimplePie("effects_size", () -> String.valueOf(effectManager.getEffects().size())));
        metrics.addCustomChart(new SimplePie("colors_size", () -> String.valueOf(colorManager.getColors().size())));
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public void reload() {
        getLogger().info("Reloading TreasureElytra");

        // config.yml
        configure();
        reloadConfig();
        getLogger().info("Reloaded config.yml!");

        // Data Holders
        dataHolders.forEach(DataHolder::reload);
        getLogger().info("Reloaded data holders!");

        // Player Manager
        playerManager.reload();
        getLogger().info("Reloaded player manager!");

        // Debug Mode
        final var tempDebugMode = this.debugModeEnabled;
        debugModeEnabled = new File(getDataFolder(), "dev").exists();
        if (tempDebugMode != debugModeEnabled)
            getLogger().info("> Debug mode " + (debugModeEnabled ? "enabled!" : "disabled!"));

        // Config Stuffs
        var config = getConfig();

        // Notification Manager
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // GUI Animations
        if (taskId != -5 && !config.getBoolean("gui.animation", true)) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -5;
            getLogger().info("> Disabled gui animations");
        } else if (taskId == -5 && config.getBoolean("gui.animation", true)) {
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, 2).getTaskId();
            getLogger().info("> Enabled gui animations");
        }

        // Command Permissions
        commandManager.getCommandReplacements().addReplacement("basecmd", config.getString("permissions.menu", "trelytra.menu"));
        commandManager.getCommandReplacements().addReplacement("admincmd", config.getString("permissions.admin", "trelytra.admin"));
        getLogger().info("Reloaded permissions!");

        getLogger().info("Reloaded TreasureElytra!");
    }

    public void disable() {
        getLogger().log(Level.WARNING, "Couldn't initialize TreasureElytra!");
        getPluginLoader().disablePlugin(this);
    }

    public void configure() {
        var config = getConfig();
        if (!config.contains("permissions") || !config.contains("locale"))
            saveResource("config.yml", true);
    }

    public static Logger logger() {
        return instance.getLogger();
    }

    public static MCTiming timing(String name) {
        return timingManager.of(name);
    }
}
package net.treasure.core;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.color.ColorManager;
import net.treasure.common.Permissions;
import net.treasure.core.command.MainCommand;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.database.Database;
import net.treasure.core.gui.EffectsGUI;
import net.treasure.core.gui.listener.GUIListener;
import net.treasure.core.gui.task.GUIUpdater;
import net.treasure.core.integration.Expansions;
import net.treasure.core.listener.JoinQuitListener;
import net.treasure.core.notification.NotificationManager;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.locale.Translations;
import net.treasure.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter
public class TreasurePlugin extends JavaPlugin {

    @Getter
    private static TreasurePlugin instance;
    public static final String VERSION = "1.3.0";

    // Data Holders
    private Translations translations;
    private EffectManager effectManager;
    private ColorManager colorManager;
    private Permissions permissions;
    private EffectsGUI gui;
    private List<DataHolder> dataHolders;

    private Database database;
    private PlayerManager playerManager;
    private NotificationManager notificationManager;
    private UpdateChecker updateChecker;

    // ACF
    private BukkitCommandManager commandManager;

    @Accessors(fluent = true)
    private BukkitAudiences adventure;

    private boolean debugModeEnabled;
    private boolean autoUpdateEnabled = true;
    @Accessors(fluent = true)
    private int guiTask = -5, guiInterval = 2;
    @Accessors(fluent = true)
    private float guiColorCycleSpeed = 0.75f;

    @Override
    public void onEnable() {
        var current = System.currentTimeMillis();

        instance = this;

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

        // Initialize player manager
        playerManager = new PlayerManager();

        // Command stuffs
        commandManager = new BukkitCommandManager(this);

        // Adventure
        this.adventure = BukkitAudiences.create(this);

        //region Initialize data holders
        translations = new Translations();
        translations.initialize();
        dataHolders.add(translations);

        // Colors
        colorManager = new ColorManager();
        if (!colorManager.initialize()) {
            disable();
            return;
        }
        dataHolders.add(colorManager);

        // Effects
        effectManager = new EffectManager();
        if (!effectManager.initialize()) {
            disable();
            return;
        }
        dataHolders.add(effectManager);

        // Effects GUI
        gui = new EffectsGUI();
        gui.initialize();
        dataHolders.add(gui);

        // Permissions
        permissions = new Permissions();
        permissions.initialize();
        dataHolders.add(permissions);

        //endregion

        var config = getConfig();

        // Notification Manager
        notificationManager = new NotificationManager();
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // Load colors & effects
        colorManager.loadColors();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> effectManager.loadEffects());

        // Main command with completions
        commandManager.registerCommand(new MainCommand(this));
        var completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("effects", context -> effectManager.getEffects().stream().map(Effect::getKey).toList());
        completions.registerStaticCompletion("versions", notificationManager.getVersions());

        // Initialize players
        for (var player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        // Listeners & Tasks
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(this), this);
        pluginManager.registerEvents(new GUIListener(), this);
        if (config.getBoolean("gui.animation.enabled", true)) {
            guiInterval = config.getInt("gui.animation.interval", guiInterval);
            guiColorCycleSpeed = (float) config.getDouble("gui.animation.color-cycle-speed", guiColorCycleSpeed);
            guiTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, guiInterval).getTaskId();
        }

        // Update Checker
        updateChecker = new UpdateChecker(this);
        updateChecker.check();

        // bStats
        var metrics = new Metrics(this, 14508);
        metrics.addCustomChart(new SimplePie("effects_size", () -> String.valueOf(effectManager.getEffects().size())));
        metrics.addCustomChart(new SimplePie("colors_size", () -> String.valueOf(colorManager.getColors().size())));

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new Expansions(playerManager).register();

        getLogger().info("Enabled TreasureElytra (" + (System.currentTimeMillis() - current) + "ms)");
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
        saveDefaultConfig();
        reloadConfig();
        configure();
        getLogger().info("Reloaded config!");

        // Data Holders
        dataHolders.forEach(DataHolder::reload);
        getLogger().info("Reloaded data holders!");

        // Player Manager
        playerManager.reload();
        getLogger().info("Reloaded player manager!");

        // Debug Mode
        final var tempDebugMode = debugModeEnabled;
        this.debugModeEnabled = new File(getDataFolder(), "dev").exists();
        if (tempDebugMode != debugModeEnabled)
            getLogger().info("> Debug mode " + (debugModeEnabled ? "enabled!" : "disabled!"));

        // Config Stuffs
        var config = getConfig();

        // Notification Manager
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // GUI Animations
        if (guiTask != -5 && !config.getBoolean("gui.animation", true)) {
            Bukkit.getScheduler().cancelTask(guiTask);
            this.guiTask = -5;
            getLogger().info("> Disabled gui animations");
        } else if (guiTask == -5 && config.getBoolean("gui.animation", true)) {
            this.guiTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, guiInterval).getTaskId();
            getLogger().info("> Enabled gui animations");
        }

        this.guiInterval = config.getInt("gui.animation.interval", guiInterval);
        this.guiColorCycleSpeed = (float) config.getDouble("gui.animation.color-cycle-speed", guiColorCycleSpeed);

        // Command Permissions
        permissions.reload();
        getLogger().info("Reloaded permissions!");

        getLogger().info("Reloaded TreasureElytra!");
    }

    public void disable() {
        getLogger().warning("Couldn't initialize TreasureElytra!");
        getPluginLoader().disablePlugin(this);
    }

    public void configure() {
        var config = getConfig();
        if (!VERSION.equals(config.getString("version")))
            saveResource("config.yml", true);
        this.autoUpdateEnabled = config.getBoolean("auto-update-configurations", true);
    }

    public static Logger logger() {
        return instance.getLogger();
    }

    public String getVersion() {
        return VERSION;
    }
}
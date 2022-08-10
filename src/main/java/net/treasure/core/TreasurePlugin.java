package net.treasure.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.lib.timings.MCTiming;
import co.aikar.commands.lib.timings.TimingManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.color.ColorManager;
import net.treasure.common.Permissions;
import net.treasure.core.command.MainCommand;
import net.treasure.core.command.gui.GUIElements;
import net.treasure.core.command.gui.listener.GUIListener;
import net.treasure.core.command.gui.task.GUIUpdater;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.database.Database;
import net.treasure.core.listener.JoinQuitListener;
import net.treasure.core.notification.NotificationManager;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.locale.Messages;
import net.treasure.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Getter
public class TreasurePlugin extends JavaPlugin {

    @Getter
    private static TreasurePlugin instance;
    public static final String VERSION = "1.2.3";

    // Timings
    private static TimingManager timingManager;
    private static boolean timingsEnabled;

    // Data Holders
    private Messages messages;
    private EffectManager effectManager;
    private ColorManager colorManager;
    private Permissions permissions;
    private GUIElements guiElements;
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
    private Random random;
    private int GUI_TASK = -5;

    @Override
    public void onEnable() {
        var current = System.currentTimeMillis();

        instance = this;

        random = new Random();
        dataHolders = new ArrayList<>();
        debugModeEnabled = new File(getDataFolder(), "dev").exists();

        // Main Config
        saveDefaultConfig();
        configure();

        // Timings
        timingManager = TimingManager.of(this);
        timingsEnabled = getConfig().getBoolean("timings", true);

        // Database
        database = new Database();
        if (!database.connect()) {
            disable();
            return;
        }

        playerManager = new PlayerManager();

        // Command stuffs
        commandManager = new BukkitCommandManager(this);

        // Adventure
        this.adventure = BukkitAudiences.create(this);

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

        guiElements = new GUIElements();
        guiElements.initialize();
        dataHolders.add(guiElements);

        var config = getConfig();

        // Notification Manager
        notificationManager = new NotificationManager();
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // Load colors & effects
        colorManager.loadColors();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> effectManager.loadEffects());

        // Permissions
        permissions = new Permissions();
        permissions.initialize();
        dataHolders.add(permissions);

        // Main command with completions
        commandManager.registerCommand(new MainCommand(this));
        var completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("effects", context -> effectManager.getEffects().stream().map(Effect::getKey).toList());
        completions.registerStaticCompletion("versions", notificationManager.getVersions());

        // Initialize players
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        // Listeners & Tasks
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(this), this);
        pluginManager.registerEvents(new GUIListener(), this);
        if (config.getBoolean("gui.animation", true))
            GUI_TASK = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, 2).getTaskId();

        // Update Checker
        updateChecker = new UpdateChecker(this);
        updateChecker.check();

        // bStats
        var metrics = new Metrics(this, 14508);
        metrics.addCustomChart(new SimplePie("effects_size", () -> String.valueOf(effectManager.getEffects().size())));
        metrics.addCustomChart(new SimplePie("colors_size", () -> String.valueOf(colorManager.getColors().size())));

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
        configure();
        reloadConfig();
        getLogger().info("Reloaded config!");

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

        // Timings
        final var tempTimings = timingsEnabled;
        timingsEnabled = getConfig().getBoolean("timings", true);
        if (tempTimings != timingsEnabled)
            getLogger().info("> Timings " + (timingsEnabled ? "enabled!" : "disabled!"));

        // Config Stuffs
        var config = getConfig();

        // Notification Manager
        notificationManager.setEnabled(config.getBoolean("notifications", true));

        // GUI Animations
        if (GUI_TASK != -5 && !config.getBoolean("gui.animation", true)) {
            Bukkit.getScheduler().cancelTask(GUI_TASK);
            GUI_TASK = -5;
            getLogger().info("> Disabled gui animations");
        } else if (GUI_TASK == -5 && config.getBoolean("gui.animation", true)) {
            GUI_TASK = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIUpdater(), 0, 2).getTaskId();
            getLogger().info("> Enabled gui animations");
        }

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
    }

    public static Logger logger() {
        return instance.getLogger();
    }

    public static MCTiming timing(String name) {
        return timingsEnabled ? timingManager.of(name) : null;
    }

    public String getVersion() {
        return VERSION;
    }
}
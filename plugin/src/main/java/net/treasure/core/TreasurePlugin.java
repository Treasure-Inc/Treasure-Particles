package net.treasure.core;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.color.ColorManager;
import net.treasure.common.Permissions;
import net.treasure.core.command.MainCommand;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.database.Database;
import net.treasure.core.database.DatabaseManager;
import net.treasure.core.gui.GUIManager;
import net.treasure.core.integration.Expansions;
import net.treasure.core.listener.JoinQuitListener;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.locale.Translations;
import net.treasure.util.logging.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Getter
public class TreasurePlugin extends JavaPlugin {

    @Getter
    private static TreasurePlugin instance;
    public static final String VERSION = "1.5.1"; // config.yml

    // Data Holders
    private Translations translations;
    private EffectManager effectManager;
    private ColorManager colorManager;
    private Permissions permissions;
    private GUIManager guiManager;
    private List<DataHolder> dataHolders;

    private DatabaseManager databaseManager;
    private PlayerManager playerManager;

    // ACF
    private BukkitCommandManager commandManager;

    @Accessors(fluent = true)
    private BukkitAudiences adventure;

    private boolean debugModeEnabled;
    private boolean autoUpdateEnabled = true;
    private boolean notificationsEnabled;

    @Override
    public void onLoad() {
        debugModeEnabled = new File(getDataFolder(), "dev").exists();
    }

    @Override
    public void onEnable() {
        var current = System.currentTimeMillis();

        instance = this;
        dataHolders = new ArrayList<>();

        // Adventure & ACF
        commandManager = new BukkitCommandManager(this);
        adventure = BukkitAudiences.create(this);

        // Main Config
        saveDefaultConfig();
        configure();

        // Database
        databaseManager = new DatabaseManager();
        if (!databaseManager.initialize(this)) {
            disable();
            return;
        }

        // Initialize player manager
        playerManager = new PlayerManager();

        // Translations
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

        // Permissions
        permissions = new Permissions();
        permissions.initialize();
        dataHolders.add(permissions);

        // Commands & Listeners
        initializeCommands();
        initializeListeners();

        // GUI Manager
        guiManager = new GUIManager(this);
        dataHolders.add(guiManager);

        // Load translations > GUI > colors > effects
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            translations.loadTranslations();
            guiManager.initialize();

            colorManager.loadColors();
            effectManager.loadEffects();
        });

        // Initialize players
        for (var player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);

        // bStats
        initializeMetrics();

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new Expansions(playerManager).register();

        getLogger().info("Enabled TreasureElytra+ (" + (System.currentTimeMillis() - current) + "ms)");
    }

    @Override
    public void onDisable() {
        this.databaseManager.close();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public void disable() {
        getLogger().warning("Couldn't initialize TreasureElytra+");
        getPluginLoader().disablePlugin(this);
    }

    public void reload() {
        getLogger().info("Reloading TreasureElytra+");

        // config.yml
        saveDefaultConfig();
        reloadConfig();
        configure();
        getLogger().info("Reloaded config!");

        // Debug Mode
        final var tempDebugMode = debugModeEnabled;
        this.debugModeEnabled = new File(getDataFolder(), "dev").exists();
        if (tempDebugMode != debugModeEnabled)
            getLogger().info("> Debug mode " + (debugModeEnabled ? "enabled!" : "disabled!"));

        // Data Holders
        dataHolders.forEach(DataHolder::reload);
        getLogger().info("Reloaded data holders!");

        // Player Manager
        playerManager.reload();
        getLogger().info("Reloaded player manager!");

        getLogger().info("Reloaded TreasureElytra+");
    }

    public void configure() {
        var config = getConfig();
        if (!VERSION.equals(config.getString("version"))) {
            if (autoUpdateEnabled) {
                var generator = new ConfigurationGenerator("config.yml");
                generator.generate();
                generator.reset();
                reloadConfig();
            } else
                getLogger().warning("New version of config.yml available (v" + VERSION + ")");
        }

        this.notificationsEnabled = config.getBoolean("notifications", true);
        this.autoUpdateEnabled = config.getBoolean("auto-update-configurations", true);
        ComponentLogger.setColored(config.getBoolean("colored-error-logs", true));
    }

    private void initializeCommands() {
        // Main command with completions
        commandManager.registerCommand(new MainCommand(this));
        var completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("effects", context -> effectManager.getEffects().stream().map(Effect::getKey).toList());
        completions.registerAsyncCompletion("groupColors", context -> {
            var key = context.getContextValue(String.class, 1);
            var effect = effectManager.get(key);
            return effect == null || effect.getColorGroup() == null ? Collections.emptyList() : effect.getColorGroup().getAvailableOptions().stream().map(option -> option.colorScheme().getKey()).toList();
        });
    }

    private void initializeListeners() {
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(this), this);
    }

    private void initializeMetrics() {
        var metrics = new Metrics(this, 14508);
        metrics.addCustomChart(new SimplePie("locale", () -> Translations.LOCALE));

        metrics.addCustomChart(new SimplePie("effects_size", () -> String.valueOf(effectManager.getEffects().size())));
        metrics.addCustomChart(new SimplePie("colors_size", () -> String.valueOf(colorManager.getColors().size())));

        metrics.addCustomChart(new SimplePie("debug_mode_enabled", () -> String.valueOf(debugModeEnabled)));
        metrics.addCustomChart(new SimplePie("auto_update_enabled", () -> String.valueOf(autoUpdateEnabled)));

        metrics.addCustomChart(new SimplePie("gui_animation_enabled", () -> String.valueOf(guiManager.getTaskId() != -5)));
        metrics.addCustomChart(new SimplePie("gui_animation_interval", () -> String.valueOf(guiManager.getInterval())));
        metrics.addCustomChart(new SimplePie("gui_animation_speed", () -> String.valueOf(guiManager.getColorCycleSpeed())));
    }

    public static Logger logger() {
        return instance.getLogger();
    }

    public String getVersion() {
        return VERSION;
    }

    public Database getDatabase() {
        return databaseManager.instance();
    }
}
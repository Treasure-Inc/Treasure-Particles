package net.treasure;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.color.ColorManager;
import net.treasure.configuration.ConfigurationGenerator;
import net.treasure.configuration.DataHolder;
import net.treasure.database.Database;
import net.treasure.database.DatabaseManager;
import net.treasure.effect.EffectManager;
import net.treasure.gui.GUIManager;
import net.treasure.locale.Translations;
import net.treasure.permission.Permissions;
import net.treasure.player.PlayerManager;
import net.treasure.util.logging.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TreasureParticles {

    public static final String VERSION = "1.1.2"; // config.yml

    @Getter
    private static AbstractTreasurePlugin plugin;
    @Getter
    private static BukkitCommandManager commandManager;
    @Getter
    @Accessors(fluent = true)
    private static BukkitAudiences adventure;

    private static List<DataHolder> dataHolders;
    @Getter
    private static Translations translations;
    @Getter
    private static EffectManager effectManager;
    @Getter
    private static ColorManager colorManager;
    @Getter
    private static Permissions permissions;
    private static GUIManager guiManager;

    @Getter
    private static DatabaseManager databaseManager;
    @Getter
    private static PlayerManager playerManager;

    @Getter
    private static boolean autoUpdateEnabled = true;
    @Getter
    private static boolean notificationsEnabled;

    public static void setPlugin(AbstractTreasurePlugin treasurePlugin) {
        if (plugin != null) return;
        plugin = treasurePlugin;
        initialize();
    }

    private static void initialize() {
        commandManager = new BukkitCommandManager(plugin);
        adventure = BukkitAudiences.create(plugin);

        // Main Config
        plugin.saveDefaultConfig();
        configure();

        dataHolders = new ArrayList<>();

        // Database
        databaseManager = new DatabaseManager();
        if (!databaseManager.initialize()) {
            plugin.disable();
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
            plugin.disable();
            return;
        }
        dataHolders.add(colorManager);

        // Effects
        effectManager = new EffectManager();
        if (!effectManager.initialize()) {
            plugin.disable();
            return;
        }
        dataHolders.add(effectManager);

        // Permissions
        permissions = new Permissions();
        permissions.initialize();
        dataHolders.add(permissions);

        // GUI Manager
        guiManager = new GUIManager();
        dataHolders.add(guiManager);

        // Load translations > GUI > colors > effects
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            translations.loadTranslations();
            guiManager.initialize();

            colorManager.loadColors();
            effectManager.loadEffects();
        }, 5);

        // Initialize players
        for (var player : Bukkit.getOnlinePlayers())
            playerManager.initializePlayer(player);
    }

    public static void reload() {
        plugin.getLogger().info("Reloading TreasureParticles");

        // config.yml
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        configure();
        plugin.getLogger().info("Reloaded config!");

        // Data Holders
        dataHolders.forEach(DataHolder::reload);
        plugin.getLogger().info("Reloaded data holders!");

        // Player Manager
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.reload(), 5);
        plugin.getLogger().info("Reloaded player manager!");

        plugin.getLogger().info("Reloaded TreasureParticles");
    }

    private static void configure() {
        var config = plugin.getConfig();
        if (!VERSION.equals(config.getString("version"))) {
            if (autoUpdateEnabled) {
                var generator = new ConfigurationGenerator("config.yml", plugin);
                generator.reset();
                plugin.reloadConfig();
                plugin.getLogger().warning("Generated new config.yml (v" + VERSION + ")");
            } else
                plugin.getLogger().warning("New version of config.yml available (v" + VERSION + ")");
        }

        notificationsEnabled = config.getBoolean("notifications", false);
        autoUpdateEnabled = config.getBoolean("auto-update-configurations", true);
        ComponentLogger.setColored(config.getBoolean("colored-error-logs", true));
    }

    public static Logger logger() {
        return plugin.getLogger();
    }

    public static void newVersionInfo(DataHolder holder) {
        logger().warning("New version of " + holder.getGenerator().getFileName() + " available (v" + holder.getVersion() + ")");
    }

    public static void generatedNewFile(DataHolder holder) {
        logger().warning("Generated new " + holder.getGenerator().getFileName() + " (v" + holder.getVersion() + ")");
    }

    public static File getDataFolder() {
        return plugin.getDataFolder();
    }

    public static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public static void saveConfig() {
        plugin.saveConfig();
    }

    public static GUIManager getGUIManager() {
        return guiManager;
    }

    public static Database getDatabase() {
        return databaseManager.instance();
    }
}
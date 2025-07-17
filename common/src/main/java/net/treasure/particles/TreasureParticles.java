package net.treasure.particles;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.treasure.particles.color.ColorManager;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.configuration.DataHolder;
import net.treasure.particles.database.Database;
import net.treasure.particles.database.DatabaseManager;
import net.treasure.particles.effect.EffectManager;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.player.listener.JoinQuitListener;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TreasureParticles {

    public static final String VERSION = "1.5.0"; // config.yml

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
    @Getter
    private static GUIManager GUIManager;

    @Getter
    private static DatabaseManager databaseManager;
    @Getter
    private static PlayerManager playerManager;

    @Getter
    private static boolean autoUpdateEnabled = true;
    @Getter
    private static boolean notificationsEnabled;

    @Getter
    @Setter
    private static boolean isPaper;

    public static void setPlugin(AbstractTreasurePlugin treasurePlugin) {
        if (plugin != null) return;
        plugin = treasurePlugin;
        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {
        }
        initialize();
    }

    private static void initialize() {
        if (!Particles.initialize()) {
            plugin.disable("Couldn't initialize TreasureParticles");
            return;
        }

        commandManager = new BukkitCommandManager(plugin);
        adventure = BukkitAudiences.create(plugin);

        // Main Config
        plugin.saveDefaultConfig();
        configure();

        dataHolders = new ArrayList<>();

        // Database
        databaseManager = new DatabaseManager();
        if (!databaseManager.initialize()) {
            plugin.disable("Couldn't connect to database");
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
            plugin.disable("Couldn't initialize TreasureParticles");
            return;
        }
        dataHolders.add(colorManager);

        // Effects
        effectManager = new EffectManager();
        if (!effectManager.initialize()) {
            plugin.disable("Couldn't initialize TreasureParticles");
            return;
        }
        dataHolders.add(effectManager);

        // Permissions
        permissions = new Permissions();
        permissions.initialize();
        dataHolders.add(permissions);

        // GUI Manager
        GUIManager = new GUIManager();
        dataHolders.add(GUIManager);

        // Load translations > GUI > colors > effects > players
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            translations.loadTranslations();
            GUIManager.initialize();

            colorManager.loadColors();
            effectManager.loadEffects();

            for (var player : Bukkit.getOnlinePlayers())
                playerManager.initializePlayer(player);
        }, 5);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(playerManager, effectManager), plugin);
    }

    public static void reload(CommandSender sender) {
        ComponentLogger.setChatReceiver(sender);
        effectManager.cancelTask();

        plugin.getLogger().info("Reloading TreasureParticles");

        // config.yml
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        configure();
        plugin.getLogger().info("..Reloaded config");

        // Data Holders
        dataHolders.forEach(DataHolder::reload);
        plugin.getLogger().info("..Reloaded data holders");

        // Player Manager
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.reload(), 5);
        plugin.getLogger().info("..Reloaded player manager");

        plugin.getLogger().info("Reloaded");
        ComponentLogger.setChatReceiver(null);
    }

    private static void configure() {
        plugin.reloadConfig();

        var config = plugin.getConfig();
        if (!VERSION.equals(config.getString("version"))) {
            if (autoUpdateEnabled) {
                var generator = new ConfigurationGenerator("config.yml", plugin);
                generator.reset();
                plugin.reloadConfig();
                ComponentLogger.error("[config.yml]", "Generated new file (v" + VERSION + ")");
            } else
                ComponentLogger.error("[config.yml]", "New version available (v" + VERSION + ")");
        }

        notificationsEnabled = config.getBoolean("notifications", false);
        autoUpdateEnabled = config.getBoolean("auto-update-configurations", true);

        ComponentLogger.setColored(config.getBoolean("colored-error-logs", true));
        ComponentLogger.setChatLogsEnabled(config.getBoolean("chat-logs", true));
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

    public static Database getDatabase() {
        return databaseManager.instance();
    }
}
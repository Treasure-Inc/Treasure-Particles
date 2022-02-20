package net.treasure.util.locale;

import net.treasure.core.TreasurePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class Messages {

    public static String LOCALE;
    private static YamlConfiguration config;

    public static String PREFIX,
            EFFECT_SELECTED,
            EFFECT_NO_PERMISSION,
            EFFECT_UNKNOWN,
            GUI_TITLE,
            GUI_NEXT_PAGE,
            GUI_PREVIOUS_PAGE,
            GUI_CLOSE,
            GUI_EFFECT_SELECTED,
            GUI_SELECT_EFFECT,
            GUI_RESET_EFFECT,
            RELOADING,
            RELOADED;

    public static YamlConfiguration get() {
        return config;
    }

    public void load() {
        for (Locale locale : Locale.values())
            TreasurePlugin.getInstance().saveResource("messages_" + locale.getKey() + ".yml", false);
        try {
            LOCALE = TreasurePlugin.getInstance().getConfig().getString("locale", "en");

            File file = new File(TreasurePlugin.getInstance().getDataFolder(), "messages_" + LOCALE + ".yml");
            if (!file.exists()) {
                LOCALE = "en";
                file = new File(TreasurePlugin.getInstance().getDataFolder(), "messages_en.yml");
            }

            config = YamlConfiguration.loadConfiguration(file);

            PREFIX = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "§e§l[?] §r"));

            RELOADED = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("reloaded", "§aReloaded!"));
            EFFECT_SELECTED = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("effect-selected", "§bSelected: %s"));
            EFFECT_NO_PERMISSION = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("effect-no-permission", "§cYou cannot use that effect!"));
            EFFECT_UNKNOWN = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("effect-unknown", "§cThere is no effect named %s"));

            GUI_TITLE = ChatColor.translateAlternateColorCodes('&', config.getString("gui-title", "§b§lEffects"));
            GUI_EFFECT_SELECTED = ChatColor.translateAlternateColorCodes('&', config.getString("gui-effect-selected", "§aSelected!"));
            GUI_SELECT_EFFECT = ChatColor.translateAlternateColorCodes('&', config.getString("gui-select-effect", "§8» §aClick to use this effect!"));
            GUI_RESET_EFFECT = ChatColor.translateAlternateColorCodes('&', config.getString("gui-reset-effect", "§eReset Effect"));
            GUI_NEXT_PAGE = ChatColor.translateAlternateColorCodes('&', config.getString("gui-next-page", "§a> Next Page"));
            GUI_PREVIOUS_PAGE = ChatColor.translateAlternateColorCodes('&', config.getString("gui-previous-page", "§a< Previous Page"));
            GUI_CLOSE = ChatColor.translateAlternateColorCodes('&', config.getString("gui-close", "§cClose"));

            RELOADING = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("reloading", "§eReloading configurations..."));
            RELOADED = Messages.PREFIX + ChatColor.translateAlternateColorCodes('&', config.getString("reloaded", "§aReloaded!"));
        } catch (Exception exception) {
            exception.printStackTrace();
            Bukkit.getLogger().log(Level.WARNING, "Couldn't load messages from messages_" + LOCALE + ".yml");
        }
    }
}
package net.treasure.locale;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;

import java.io.File;
import java.util.logging.Level;

public class Messages implements DataHolder {

    public static String LOCALE;
    public static final String
            VERSION = "1.1.0",
            UPDATE_DESCRIPTION = "MiniMessage Support (From now on, color chars (&,§) chars cannot be used in messages)";
    private ConfigurationGenerator generator;

    public static String PREFIX,
            EFFECT_SELECTED,
            EFFECT_NO_PERMISSION,
            EFFECT_UNKNOWN,
            EFFECT_TOGGLE,
            GUI_TITLE,
            GUI_NEXT_PAGE,
            GUI_PREVIOUS_PAGE,
            GUI_CLOSE,
            GUI_EFFECT_SELECTED,
            GUI_SELECT_EFFECT,
            GUI_RESET_EFFECT,
            RELOADING,
            RELOADED,
            ENABLED,
            DISABLED;

    @Override
    public boolean checkVersion() {
        return VERSION.equals(generator.getConfiguration().getString("version"));
    }

    @Override
    public boolean initialize() {
        for (var locale : Locale.values())
            new ConfigurationGenerator("messages_" + locale.getKey() + ".yml", "messages").generate();

        try {
            var inst = TreasurePlugin.getInstance();
            LOCALE = inst.getConfig().getString("locale", "en").toLowerCase(java.util.Locale.ENGLISH);

            this.generator = new ConfigurationGenerator("messages_" + LOCALE + ".yml", "messages");
            var config = generator.generate();
            String notify = null;

            if (generator.generate() == null) {
                LOCALE = Locale.ENGLISH.key;
                this.generator = new ConfigurationGenerator("messages_" + LOCALE + ".yml", "messages");
                config = generator.generate();
                notify = "unknown locale";
            } else if (!checkVersion()) {
                generator.reset();
                config = generator.getConfiguration();
                notify = "version change";
            }

            if (config == null)
                throw new Exception();

            // Version Update (v1.0.5 > v1.1.0)
            if (new File(inst.getDataFolder(), "messages_" + LOCALE + ".yml").exists())
                notify = "directory change";

            if (notify != null) {
                inst.getLogger().warning("Generated new messages_" + LOCALE + ".yml (v" + VERSION + ") due to " + notify + ". Changelog:");
                inst.getLogger().warning(UPDATE_DESCRIPTION);
            }

            PREFIX = config.getString("prefix", "<gradient:#A3BCF9:#576490>[TʀEʟʏᴛʀᴀ] <reset>");

            EFFECT_SELECTED = PREFIX + config.getString("effect-selected", "<aqua>Selected: %s");
            EFFECT_NO_PERMISSION = PREFIX + config.getString("effect-no-permission", "<red>You don't have enough permission to use that effect!");
            EFFECT_UNKNOWN = PREFIX + config.getString("effect-unknown", "<red>Couldn't find any effect with name %s");
            EFFECT_TOGGLE = PREFIX + config.getString("effect-toggle", "<gray>Elytra Effects: %s");

            GUI_TITLE = config.getString("gui-title", "<aqua><b>Effects");
            GUI_EFFECT_SELECTED = config.getString("gui-effect-selected", "<green>Selected!");
            GUI_SELECT_EFFECT = config.getString("gui-select-effect", "<dark_gray>• <green>Click to use this effect!");
            GUI_RESET_EFFECT = config.getString("gui-reset-effect", "<yellow>Reset Effect");
            GUI_NEXT_PAGE = config.getString("gui-next-page", "<green>> Next Page");
            GUI_PREVIOUS_PAGE = config.getString("gui-previous-page", "<green>< Previous Page");
            GUI_CLOSE = config.getString("gui-close", "<red>Close");

            RELOADING = PREFIX + config.getString("reloading", "<yellow>Reloading configurations...");
            RELOADED = PREFIX + config.getString("reloaded", "<green>Reloaded!");

            ENABLED = config.getString("enabled", "<green>Enabled");
            DISABLED = config.getString("disabled", "<red>Disabled");
            return true;
        } catch (Exception exception) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't load messages from messages_" + LOCALE + ".yml", exception);
            return false;
        }
    }

    @Override
    public void reload() {
        initialize();
    }

    public String get(String key) {
        return generator.getConfiguration().getString(key, "UNKNOWN");
    }

    public String get(String key, String defaultValue) {
        return generator.getConfiguration().getString(key, defaultValue);
    }
}
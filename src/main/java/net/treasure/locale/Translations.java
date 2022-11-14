package net.treasure.locale;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.locales.MessageKeyProvider;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.message.MessageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Translations implements DataHolder {

    public static String LOCALE;
    public static final String
            VERSION = "1.3.0",
            UPDATE_DESCRIPTION = "New translations";
    private ConfigurationGenerator generator;

    public static String PREFIX,
            EFFECT_SELECTED,
            EFFECT_NO_PERMISSION,
            EFFECT_UNKNOWN,
            EFFECT_TOGGLE,
            EFFECT_RESET,
            EFFECT_RESET_OTHER,
            NOTIFICATIONS_TOGGLE,
            GUI_TITLE,
            GUI_NEXT_PAGE,
            GUI_PREVIOUS_PAGE,
            GUI_CLOSE,
            GUI_EFFECT_SELECTED,
            GUI_SELECT_EFFECT,
            GUI_RESET_EFFECT,
            GUI_RESET_EFFECT_CURRENT,
            RELOADING,
            RELOADED,
            ENABLED,
            DISABLED,
            COMMAND_USAGE,
            COMMAND_ERROR,
            COMMAND_MUST_BE_A_NUMBER,
            COMMAND_USERNAME_TOO_SHORT,
            COMMAND_NOT_A_VALID_NAME,
            COMMAND_NO_PLAYER_FOUND_SERVER,
            COMMAND_NO_PLAYER_FOUND_OFFLINE,
            COMMAND_NO_PERMISSION,
            PAPI_CURRENT_EFFECT_NULL,
            PAPI_ENABLED,
            PAPI_DISABLED;

    @Override
    public boolean checkVersion() {
        return VERSION.equals(generator.getConfiguration().getString("version"));
    }

    @Override
    public boolean initialize() {
        for (var locale : Locale.values())
            new ConfigurationGenerator("translations_" + locale.getKey() + ".yml", "translations").generate();

        try {
            var inst = TreasurePlugin.getInstance();
            LOCALE = inst.getConfig().getString("locale", "en").toLowerCase(java.util.Locale.ENGLISH);

            this.generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
            var config = generator.generate();
            String notify = null;

            if (generator.generate() == null) {
                LOCALE = Locale.ENGLISH.key;
                this.generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
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
            if (new File(inst.getDataFolder(), "translations_" + LOCALE + ".yml").exists())
                notify = "directory change";

            if (notify != null) {
                inst.getLogger().warning("Generated new 'translations_" + LOCALE + ".yml' (v" + VERSION + ") due to " + notify + ". Changelog:");
                inst.getLogger().warning(UPDATE_DESCRIPTION);
            }

            PREFIX = config.getString("prefix", "<gradient:#A3BCF9:#576490>[TʀEʟʏᴛʀᴀ] <reset>");

            EFFECT_SELECTED = PREFIX + config.getString("effect-selected", "<aqua>Selected: %s");
            EFFECT_NO_PERMISSION = PREFIX + config.getString("effect-no-permission", "<red>You don't have enough permission to use that effect!");
            EFFECT_UNKNOWN = PREFIX + config.getString("effect-unknown", "<red>Couldn't find any effect with name %s");
            EFFECT_TOGGLE = PREFIX + config.getString("effect-toggle", "<gray>Elytra Effects: %s");
            EFFECT_RESET = PREFIX + config.getString("effect-reset", "<gray>Elytra Effect: <red>OFF");
            EFFECT_RESET_OTHER = PREFIX + config.getString("effect-reset-other", "<gray>Elytra Effect (%s): <red>OFF");
            NOTIFICATIONS_TOGGLE = PREFIX + config.getString("notifications-toggle", "<gray>Notifications: %s");

            GUI_TITLE = config.getString("gui-title", "<aqua><b>Effects");
            GUI_EFFECT_SELECTED = config.getString("gui-effect-selected", "<green>Selected!");
            GUI_SELECT_EFFECT = config.getString("gui-select-effect", "<dark_gray>• <green>Click to use this effect!");
            GUI_RESET_EFFECT = config.getString("gui-reset-effect", "<yellow>Reset Effect");
            GUI_RESET_EFFECT_CURRENT = config.getString("gui-reset-effect-current", "<gray>Selected: <gold>%s");
            GUI_NEXT_PAGE = config.getString("gui-next-page", "<green>> Next Page");
            GUI_PREVIOUS_PAGE = config.getString("gui-previous-page", "<green>< Previous Page");
            GUI_CLOSE = config.getString("gui-close", "<red>Close");

            RELOADING = PREFIX + config.getString("reloading", "<yellow>Reloading configurations...");
            RELOADED = PREFIX + config.getString("reloaded", "<green>Reloaded!");

            ENABLED = config.getString("enabled", "<green>Enabled");
            DISABLED = config.getString("disabled", "<red>Disabled");

            // PAPI

            PAPI_CURRENT_EFFECT_NULL = config.getString("placeholders.current-effect-null", "None");
            PAPI_ENABLED = config.getString("placeholders.enabled", "Enabled");
            PAPI_DISABLED = config.getString("placeholders.disabled", "Disabled");

            // Commands

            COMMAND_USAGE = config.getString("commands.usage", "<yellow>Usage:<gray> %s");
            COMMAND_ERROR = config.getString("commands.error", "<red>Error: %s");
            COMMAND_MUST_BE_A_NUMBER = config.getString("commands.must-be-a-number", "<red>%s must be a number.");
            COMMAND_USERNAME_TOO_SHORT = config.getString("commands.username-too-short", "<red>Username too short, must be at least three characters.");
            COMMAND_NOT_A_VALID_NAME = config.getString("commands.not-a-valid-name", "<red>%s is not a valid username.");
            COMMAND_NO_PLAYER_FOUND_SERVER = config.getString("commands.no-player-found-server", "<red>No player matching %s is connected to this server.");
            COMMAND_NO_PLAYER_FOUND_OFFLINE = config.getString("commands.no-player-found-offline", "<red>No player matching %s could be found.");
            COMMAND_NO_PERMISSION = config.getString("commands.no-permission", "<red>You don't have enough permission to use this effect!");

            var commandManager = inst.getCommandManager();
            Map<MessageKeyProvider, String> messages = new HashMap<>();
            messages.put(MessageKeys.INVALID_SYNTAX, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_USAGE, "{command} {syntax}")));
            messages.put(MessageKeys.ERROR_PREFIX, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_ERROR, "{message}")));
            messages.put(MessageKeys.MUST_BE_A_NUMBER, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_MUST_BE_A_NUMBER, "{num}")));
            messages.put(MinecraftMessageKeys.USERNAME_TOO_SHORT, MessageUtils.parseLegacy(Translations.PREFIX + COMMAND_USERNAME_TOO_SHORT));
            messages.put(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_NOT_A_VALID_NAME, "{name}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_NO_PLAYER_FOUND_SERVER, "{search}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_NO_PLAYER_FOUND_OFFLINE, "{search}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND, MessageUtils.parseLegacy(String.format(Translations.PREFIX + COMMAND_NO_PLAYER_FOUND_OFFLINE, "{search}")));
            messages.put(MessageKeys.PERMISSION_DENIED, MessageUtils.parseLegacy(Translations.PREFIX + COMMAND_NO_PERMISSION));
            messages.put(MessageKeys.PERMISSION_DENIED_PARAMETER, MessageUtils.parseLegacy(Translations.PREFIX + COMMAND_NO_PERMISSION));

            commandManager.getLocales().addMessages(java.util.Locale.ENGLISH, messages);
            return true;
        } catch (Exception exception) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't load translations from 'translations_" + LOCALE + ".yml'", exception);
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
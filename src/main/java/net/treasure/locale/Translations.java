package net.treasure.locale;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.locales.MessageKeyProvider;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.message.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static net.treasure.common.Keys.NAMESPACE;

public class Translations implements DataHolder {

    public static String LOCALE;
    public static final String
            VERSION = "1.3.0",
            UPDATE_DESCRIPTION = "New translations, Spanish & German support";

    private FileConfiguration config;

    public static String PREFIX,
            EFFECT_SELECTED,
            EFFECT_NO_PERMISSION,
            EFFECT_UNKNOWN,
            EFFECT_TOGGLE,
            EFFECT_RESET,
            EFFECT_RESET_OTHER,
            NOTIFICATIONS_TOGGLE,
            NOTIFICATION,
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
            COMMAND_ARG_PAGE,
            COMMAND_ARG_EFFECT,
            PAPI_CURRENT_EFFECT_NULL,
            PAPI_ENABLED,
            PAPI_DISABLED;

    @Override
    public boolean checkVersion() {
        return VERSION.equals(config.getString("version"));
    }

    @Override
    public boolean initialize() {
        for (var locale : Locale.values())
            locale.generate();

        try {
            var inst = TreasurePlugin.getInstance();
            LOCALE = inst.getConfig().getString("locale", "en").toLowerCase(java.util.Locale.ENGLISH);

            var generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
            config = generator.generate();
            String notify = null;

            if (config == null) {
                LOCALE = Locale.ENGLISH.key;
                inst.getConfig().set("locale", LOCALE);
                inst.saveConfig();

                generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
                config = generator.generate();
                notify = "unknown locale";
            } else if (!checkVersion()) {
                if (!TreasurePlugin.getInstance().isAutoUpdateEnabled()) {
                    TreasurePlugin.logger().warning("New version of translations_" + LOCALE + ".yml available (v" + VERSION + ")");
                } else {
                    generator.reset();
                    config = generator.getConfiguration();
                    notify = "version change";
                }
            }

            if (config == null)
                throw new Exception();

            // Version Update (v1.0.5 > v1.1.0)
            if (new File(inst.getDataFolder(), "messages_" + LOCALE + ".yml").exists())
                notify = "directory change";

            if (notify != null) {
                inst.getLogger().warning("Generated new 'translations_" + LOCALE + ".yml' (v" + VERSION + ") due to " + notify + ". Changelog:");
                inst.getLogger().warning(UPDATE_DESCRIPTION);
            }

            PREFIX = config.getString("prefix", "<gradient:#EF476F:#FFD166><b>TrElytra <dark_gray>| <reset>");

            // Translations
            EFFECT_SELECTED = config.getString("effect-selected", "<prefix> <green>Selected:<reset> %s");
            EFFECT_NO_PERMISSION = config.getString("effect-no-permission", "<prefix> <red>You don't have enough permission to use that effect!");
            EFFECT_UNKNOWN = config.getString("effect-unknown", "<prefix> <red>Couldn't find any effect with name %s");
            EFFECT_TOGGLE = config.getString("effect-toggle", "<prefix> <gray>Elytra Effects: %s");
            EFFECT_RESET = config.getString("effect-reset", "<prefix> <gray>Elytra Effect: <red>OFF");
            EFFECT_RESET_OTHER = config.getString("effect-reset-other", "<prefix> <gray>Elytra Effect (%s): <red>OFF");
            NOTIFICATIONS_TOGGLE = config.getString("notifications-toggle", "<prefix> <gray>Notifications: %s");
            NOTIFICATION = config.getString("notification", "<prefix> <aqua><b><changelog>Changelog</changelog></b> <dark_gray>/</dark_gray> <b><github>GitHub</github></b> <dark_gray>/</dark_gray> <b><wiki>Wiki</wiki>");

            RELOADING = config.getString("reloading", "<prefix> <yellow>Reloading TreasureElytra...");
            RELOADED = config.getString("reloaded", "<prefix> <green>Reloaded!");

            ENABLED = config.getString("enabled", "<green>Enabled");
            DISABLED = config.getString("disabled", "<red>Disabled");

            // GUI
            GUI_TITLE = config.getString("gui-title", "        <gradient:#EF476F:#FFD166><b>Treasure Elytra");
            GUI_EFFECT_SELECTED = config.getString("gui-effect-selected", "<green>Selected!");
            GUI_SELECT_EFFECT = config.getString("gui-select-effect", "<#87FF65>❤ Click to use this effect!");
            GUI_RESET_EFFECT = config.getString("gui-reset-effect", "<yellow>Reset Effect");
            GUI_RESET_EFFECT_CURRENT = config.getString("gui-reset-effect-current", "<gray>Selected: <gold>%s");
            GUI_NEXT_PAGE = config.getString("gui-next-page", "<green>> Next Page");
            GUI_PREVIOUS_PAGE = config.getString("gui-previous-page", "<green>< Previous Page");
            GUI_CLOSE = config.getString("gui-close", "<red>Close");

            // PAPI
            PAPI_CURRENT_EFFECT_NULL = config.getString("placeholders.current-effect-null", "None");
            PAPI_ENABLED = config.getString("placeholders.enabled", "Enabled");
            PAPI_DISABLED = config.getString("placeholders.disabled", "Disabled");

            // Commands
            COMMAND_USAGE = config.getString("commands.usage", "<prefix> <yellow>Usage:<gray> %s");
            COMMAND_ERROR = config.getString("commands.error", "<prefix> <red>Error: %s");
            COMMAND_MUST_BE_A_NUMBER = config.getString("commands.must-be-a-number", "<prefix> <red>%s must be a number.");
            COMMAND_USERNAME_TOO_SHORT = config.getString("commands.username-too-short", "<prefix> <red>Username too short, must be at least three characters.");
            COMMAND_NOT_A_VALID_NAME = config.getString("commands.not-a-valid-name", "<prefix> <red>%s is not a valid username.");
            COMMAND_NO_PLAYER_FOUND_SERVER = config.getString("commands.no-player-found-server", "<prefix> <red>No player matching %s is connected to this server.");
            COMMAND_NO_PLAYER_FOUND_OFFLINE = config.getString("commands.no-player-found-offline", "<prefix> <red>No player matching %s could be found.");
            COMMAND_NO_PERMISSION = config.getString("commands.no-permission", "<prefix> <red>You don't have enough permission to use this effect!");

            var commandManager = inst.getCommandManager();

            Map<MessageKeyProvider, String> messages = new HashMap<>();
            messages.put(MessageKeys.INVALID_SYNTAX, MessageUtils.parseLegacy(String.format(COMMAND_USAGE, "{command} {syntax}")));
            messages.put(MessageKeys.ERROR_PREFIX, MessageUtils.parseLegacy(String.format(COMMAND_ERROR, "{message}")));
            messages.put(MessageKeys.MUST_BE_A_NUMBER, MessageUtils.parseLegacy(String.format(COMMAND_MUST_BE_A_NUMBER, "{num}")));
            messages.put(MinecraftMessageKeys.USERNAME_TOO_SHORT, MessageUtils.parseLegacy(COMMAND_USERNAME_TOO_SHORT));
            messages.put(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, MessageUtils.parseLegacy(String.format(COMMAND_NOT_A_VALID_NAME, "{name}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, MessageUtils.parseLegacy(String.format(COMMAND_NO_PLAYER_FOUND_SERVER, "{search}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE, MessageUtils.parseLegacy(String.format(COMMAND_NO_PLAYER_FOUND_OFFLINE, "{search}")));
            messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND, MessageUtils.parseLegacy(String.format(COMMAND_NO_PLAYER_FOUND_OFFLINE, "{search}")));
            messages.put(MessageKeys.PERMISSION_DENIED, MessageUtils.parseLegacy(COMMAND_NO_PERMISSION));
            messages.put(MessageKeys.PERMISSION_DENIED_PARAMETER, MessageUtils.parseLegacy(COMMAND_NO_PERMISSION));

            commandManager.getLocales().addMessages(java.util.Locale.ENGLISH, messages);

            var replacements = commandManager.getCommandReplacements();
            replacements.addReplacement("page", COMMAND_ARG_PAGE = config.getString("commands.arg-page", "page"));
            replacements.addReplacement("effect", COMMAND_ARG_EFFECT = config.getString("commands.arg-effect", "effect"));
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
        return config.getString(key, "UNKNOWN");
    }

    public String get(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public String translate(String message) {
        StringBuilder output = new StringBuilder();

        var array = message.toCharArray();
        int startPos = -1;
        StringBuilder sb = new StringBuilder();

        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];
            switch (c) {
                case '%' -> {
                    if (startPos == -1) {
                        sb = new StringBuilder();
                        startPos = pos;
                        continue;
                    }
                    if (sb.isEmpty()) {
                        output.append(c);
                        sb = new StringBuilder();
                        startPos = pos;
                        continue;
                    }
                    var result = sb.toString();
                    output.append(config.getString("descriptions." + result, result));
                    startPos = -1;
                    sb = new StringBuilder();
                    continue;
                }
                case ' ' -> {
                    if (startPos != -1) {
                        output.append(sb);
                        sb = new StringBuilder();
                        startPos = pos;
                        continue;
                    }
                }
            }

            if (startPos != -1)
                sb.append(c);
            else
                output.append(c);
        }

        return output.toString();
    }
}
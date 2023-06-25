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
import java.util.Objects;
import java.util.logging.Level;

public class Translations implements DataHolder {

    public static String LOCALE;
    public static final String VERSION = "1.0.1";

    private FileConfiguration config;

    // General Translations
    public static String PREFIX,
            NOTIFICATION,
            ENABLED,
            DISABLED;

    // GUI Translations
    public static String GUI_TITLE,
            GUI_EFFECT_SELECTED,
            GUI_SELECT_EFFECT,
            GUI_RESET_EFFECT,
            GUI_RESET_EFFECT_CURRENT,
            GUI_NEXT_PAGE,
            GUI_PREVIOUS_PAGE,
            GUI_RANDOM_EFFECT,
            GUI_CLOSE,
            GUI_BACK,
            GUI_FILTER,
            COLOR_SELECTION_AVAILABLE,
            COLORS_GUI_TITLE,
            COLORS_GUI_SCHEME_SELECTED,
            COLORS_GUI_SELECT_SCHEME,
            COLORS_GUI_SAVE_SCHEME;

    public static String COMMAND_USAGE,
            COMMAND_ERROR,
            COMMAND_PLAYERS_ONLY,
            COMMAND_NO_PERMISSION,
            EFFECT_NO_PERMISSION,
            EFFECT_NO_PERMISSION_OTHER,
            EFFECT_UNKNOWN,
            EFFECT_TOGGLE,
            EFFECT_SELECTED,
            EFFECT_SELECTED_OTHER,
            EFFECT_RESET,
            EFFECT_RESET_OTHER,
            CANNOT_USE_ANY_EFFECT,
            CANNOT_USE_ANY_EFFECT_OTHER,
            UNKNOWN_COLOR_SCHEME,
            COLOR_SCHEME_SELECTED,
            NOTIFICATIONS_TOGGLE,
            RELOADING,
            RELOADED;

    public static String
            ARGS_MUST_BE_A_NUMBER,
            ARGS_USERNAME_TOO_SHORT,
            ARGS_NOT_A_VALID_NAME,
            ARGS_NO_PLAYER_FOUND_SERVER,
            ARGS_NO_PLAYER_FOUND_OFFLINE,
            ARGS_PAGE,
            ARGS_EFFECT;

    public static String PAPI_CURRENT_EFFECT_NULL,
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
            LOCALE = Objects.requireNonNull(inst.getConfig().getString("locale", "en")).toLowerCase(java.util.Locale.ENGLISH);

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
                if (!inst.isAutoUpdateEnabled()) {
                    inst.getLogger().warning("New version of translations_" + LOCALE + ".yml available (v" + VERSION + ")");
                } else {
                    generator.reset();
                    config = generator.getConfiguration();
                    notify = "version change";
                }
            }

            if (config == null)
                throw new NullPointerException("config is null");

            // Version Update (v1.0.5 > v1.1.0)
            if (new File(inst.getDataFolder(), "messages_" + LOCALE + ".yml").exists())
                notify = "directory change";

            if (notify != null)
                inst.getLogger().warning("Generated new 'translations_" + LOCALE + ".yml' (v" + VERSION + ") due to " + notify + ".");
            return true;
        } catch (Exception exception) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't load translations from 'translations_" + LOCALE + ".yml'", exception);
            return false;
        }
    }

    @Override
    public void reload() {
        if (initialize())
            loadTranslations();
    }

    public void loadTranslations() {
        var inst = TreasurePlugin.getInstance();

        // General
        PREFIX = config.getString("prefix", "<b><gradient:#EF476F:#FFD166>TrParticles</b> <dark_gray><b>|</b></dark_gray>");
        NOTIFICATION = config.getString("notification", "<prefix> <aqua><b><discord>Discord</discord></b> <dark_gray>/</dark_gray> <b><spigot>GitHub</spigot></b> <dark_gray>/</dark_gray> <b><wiki>Wiki</wiki>");
        ENABLED = config.getString("enabled", "<green>Enabled");
        DISABLED = config.getString("disabled", "<red>Disabled");

        // GUI
        GUI_TITLE = config.getString("gui.title", "      <gradient:#EF476F:#FFD166><b>Treasure Particles");
        GUI_EFFECT_SELECTED = config.getString("gui.effect-selected", "<green>Selected!");
        GUI_SELECT_EFFECT = config.getString("gui.select-effect", "<#87FF65>❤ Click to use this effect!");
        GUI_RESET_EFFECT = config.getString("gui.reset-effect", "<yellow>Reset Effect");
        GUI_RESET_EFFECT_CURRENT = config.getString("gui.reset-effect-current", "<gray>Selected: <gold>{0}");
        GUI_NEXT_PAGE = config.getString("gui.next-page", "<green>> Next Page");
        GUI_PREVIOUS_PAGE = config.getString("gui.previous-page", "<green>< Previous Page");
        GUI_RANDOM_EFFECT = config.getString("gui.random-effect", "<#87FF65>❤ Select Random Effect");
        GUI_CLOSE = config.getString("gui.close", "<red>Close");
        GUI_BACK = config.getString("gui.back", "<red>Back");
        GUI_FILTER = config.getString("gui.filter", "<gold>Supported Events");
        COLOR_SELECTION_AVAILABLE = config.getString("gui.color-selection-available", "<gray>[Right-Click] You can change the colors of this effect.");
        COLORS_GUI_TITLE = config.getString("gui.colors-gui.title", "Pick a color scheme");
        COLORS_GUI_SCHEME_SELECTED = config.getString("gui.colors-gui.color-scheme-selected", "<green>Selected!");
        COLORS_GUI_SELECT_SCHEME = config.getString("gui.colors-gui.select-color-scheme", "<#87FF65>❤ Click to use the effect with this color scheme!");
        COLORS_GUI_SAVE_SCHEME = config.getString("gui.colors-gui.save-color-scheme", "<gray><i>Right click to save this color scheme to preferences.");

        // Commands
        COMMAND_USAGE = config.getString("commands.usage", "<prefix> <yellow>Usage:<gray> {0}");
        COMMAND_ERROR = config.getString("commands.error", "<prefix> <red>Error: {0}");
        COMMAND_PLAYERS_ONLY = config.getString("commands.players-only", "<prefix> <red>This command can only be executed by players.");
        COMMAND_NO_PERMISSION = config.getString("commands.no-permission", "<prefix> <red>You don't have enough permission to perform this command!");
        EFFECT_NO_PERMISSION = config.getString("commands.effect-no-permission", "<prefix> <red>You don't have enough permission to use that effect.");
        EFFECT_NO_PERMISSION_OTHER = config.getString("commands.effect-no-permission-other", "<prefix> <red>That player doesn't have enough permission to use that effect.");
        EFFECT_UNKNOWN = config.getString("commands.effect-unknown", "<prefix> <red>Couldn't find any effect with name {0}");
        EFFECT_TOGGLE = config.getString("commands.effect-toggle", "<prefix> <gray>Elytra Effects: {0}");
        EFFECT_SELECTED = config.getString("commands.effect-selected", "<prefix> <green>Selected:<reset> {0}");
        EFFECT_SELECTED_OTHER = config.getString("commands.effect-selected-other", "<prefix> <green>Selected ({0}):<reset> {1}");
        EFFECT_RESET = config.getString("commands.effect-reset", "<prefix> <gray>Elytra Effect: <red>OFF");
        EFFECT_RESET_OTHER = config.getString("commands.effect-reset-other", "<prefix> <gray>Elytra Effect ({0}): <red>OFF");
        CANNOT_USE_ANY_EFFECT = config.getString("commands.cannot-use-any-effect", "<prefix> <red>You cannot use any effect.");
        CANNOT_USE_ANY_EFFECT_OTHER = config.getString("commands.cannot-use-any-effect-other", "<prefix> <red>That player cannot use any effect.");
        UNKNOWN_COLOR_SCHEME = config.getString("commands.unknown-color-scheme", "<prefix> <red>Unknown color scheme.");
        COLOR_SCHEME_SELECTED = config.getString("commands.color-scheme-selected", "<prefix> <green>Selected color scheme {0} for the {1} effect.");
        NOTIFICATIONS_TOGGLE = config.getString("commands.notifications-toggle", "<prefix> <gray>Notifications: {0}");
        RELOADING = config.getString("commands.reloading", "<prefix> <yellow>Reloading TreasureParticles");
        RELOADED = config.getString("commands.reloaded", "<prefix> <green>Reloaded!");
        // Args
        ARGS_MUST_BE_A_NUMBER = config.getString("commands.args.must-be-a-number", "<prefix> <red>{0} must be a number.");
        ARGS_USERNAME_TOO_SHORT = config.getString("commands.args.username-too-short", "<prefix> <red>Username too short, must be at least three characters.");
        ARGS_NOT_A_VALID_NAME = config.getString("commands.args.not-a-valid-name", "<prefix> <red>{0} is not a valid username.");
        ARGS_NO_PLAYER_FOUND_SERVER = config.getString("commands.args.no-player-found-server", "<prefix> <red>No player matching {0} is connected to this server.");
        ARGS_NO_PLAYER_FOUND_OFFLINE = config.getString("commands.args.no-player-found-offline", "<prefix> <red>No player matching {0} could be found.");
        ARGS_PAGE = config.getString("commands.args.arg-page", "page");
        ARGS_EFFECT = config.getString("commands.args.arg-effect", "effect");

        // PAPI
        PAPI_CURRENT_EFFECT_NULL = config.getString("placeholders.current-effect-null", "None");
        PAPI_ENABLED = config.getString("placeholders.enabled", "Enabled");
        PAPI_DISABLED = config.getString("placeholders.disabled", "Disabled");

        // Configure ACF
        var commandManager = inst.getCommandManager();

        Map<MessageKeyProvider, String> messages = new HashMap<>();
        messages.put(MessageKeys.NOT_ALLOWED_ON_CONSOLE, MessageUtils.parseLegacy(COMMAND_PLAYERS_ONLY));
        messages.put(MessageKeys.INVALID_SYNTAX, MessageUtils.parseLegacy(COMMAND_USAGE, "{command} {syntax}"));
        messages.put(MessageKeys.ERROR_PREFIX, MessageUtils.parseLegacy(COMMAND_ERROR, "{message}"));
        messages.put(MessageKeys.MUST_BE_A_NUMBER, MessageUtils.parseLegacy(ARGS_MUST_BE_A_NUMBER, "{num}"));
        messages.put(MinecraftMessageKeys.USERNAME_TOO_SHORT, MessageUtils.parseLegacy(ARGS_USERNAME_TOO_SHORT));
        messages.put(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, MessageUtils.parseLegacy(ARGS_NOT_A_VALID_NAME, "{name}"));
        messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, MessageUtils.parseLegacy(ARGS_NO_PLAYER_FOUND_SERVER, "{search}"));
        messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE, MessageUtils.parseLegacy(ARGS_NO_PLAYER_FOUND_OFFLINE, "{search}"));
        messages.put(MinecraftMessageKeys.NO_PLAYER_FOUND, MessageUtils.parseLegacy(ARGS_NO_PLAYER_FOUND_OFFLINE, "{search}"));
        messages.put(MessageKeys.PERMISSION_DENIED, MessageUtils.parseLegacy(COMMAND_NO_PERMISSION));
        messages.put(MessageKeys.PERMISSION_DENIED_PARAMETER, MessageUtils.parseLegacy(COMMAND_NO_PERMISSION));

        commandManager.getLocales().addMessages(java.util.Locale.ENGLISH, messages);

        var replacements = commandManager.getCommandReplacements();
        replacements.addReplacement("page", ARGS_PAGE);
        replacements.addReplacement("effect", ARGS_EFFECT);
    }

    public String get(String key) {
        return config.getString(key, "UNKNOWN");
    }

    public String get(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public String translate(String path, String message) {
        if (message == null) return null;

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
                    output.append(config.getString(path + "." + result, result));
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
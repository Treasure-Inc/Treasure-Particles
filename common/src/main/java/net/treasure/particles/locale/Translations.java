package net.treasure.particles.locale;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.locales.MessageKeyProvider;
import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.configuration.DataHolder;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Translations implements DataHolder {

    public static String LOCALE;
    public static final String VERSION = "1.4.0";

    private FileConfiguration config;
    @Getter
    private ConfigurationGenerator generator;

    // General
    public static String PREFIX,
            NOTIFICATION,
            ENABLED,
            DISABLED;

    // Common GUI Elements
    public static String BUTTON_NEXT_PAGE,
            BUTTON_PREVIOUS_PAGE,
            BUTTON_CLOSE,
            BUTTON_BACK,
            BUTTON_FILTER,
            FILTER_UP,
            FILTER_DOWN,
            FILTER_RESET;

    // Effects GUI
    public static String EFFECTS_GUI_TITLE,
            EFFECTS_GUI_SELECTED,
            EFFECTS_GUI_SELECT,
            EFFECTS_GUI_RANDOM,
            EFFECTS_GUI_RESET,
            EFFECTS_GUI_CURRENT,
            EFFECTS_GUI_ONLY_ELYTRA,
            COLOR_SELECTION_AVAILABLE,
            EFFECTS_GUI_EVENT_TYPE,
            EFFECTS_GUI_EVENT_TYPES,
            EFFECTS_GUI_MIXER,
            EFFECTS_GUI_OPEN_PLAYER_MIX_LIST,
            EFFECTS_GUI_OPEN_MIXER,
            EFFECTS_GUI_REMOVE_MIX,
            EFFECTS_GUI_MIX_REMOVED;

    // Colors GUI
    public static String COLORS_GUI_TITLE,
            COLORS_GUI_SCHEME_SELECTED,
            COLORS_GUI_SELECT_SCHEME,
            COLORS_GUI_SAVE_SCHEME,
            COLORS_GUI_RANDOM_COLOR;

    // Mixer GUI
    public static String MIXER_GUI_TITLE,
            MIXER_GUI_ALL_SELECTED,
            MIXER_GUI_SELECT_ALL,
            MIXER_GUI_SELECTED_ALL,
            MIXER_GUI_SELECTED_HANDLER,
            MIXER_GUI_UNSELECT_ALL,
            MIXER_GUI_UNSELECTED_ALL,
            MIXER_GUI_UNSELECTED_HANDLER,
            MIXER_GUI_SELECT_HANDLERS,
            MIXER_GUI_HAS_DYNAMIC_COLOR,
            MIXER_GUI_PREFER_COLOR_GROUP,
            MIXER_GUI_PREFERRED_COLOR_GROUP,
            MIXER_GUI_CURRENT_SELECTIONS,
            MIXER_GUI_RESET_SELECTIONS,
            MIXER_GUI_CONFIRM,
            MIXER_GUI_PREFERRED_COLOR_GROUP_NEEDED,
            MIXER_GUI_ENTER_NAME,
            MIXER_GUI_NAME_ALREADY_USED,
            MIX_CREATED,
            MIX_FAILED;

    // Tick Handlers GUI
    public static String HANDLERS_GUI_SELECTED,
            HANDLERS_GUI_SELECT,
            HANDLERS_GUI_UNSELECT,
            HANDLER_EVENT_LOCKED,
            HANDLER_EVENT_TYPE;

    // Command Messages
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
            CANNOT_USE_ONLY_ELYTRA,
            CANNOT_USE_ONLY_ELYTRA_OTHER,
            MIX_UNKNOWN,
            MIX_SELECTED,
            MIX_SELECTED_OTHER,
            UNKNOWN_COLOR_SCHEME,
            COLOR_SCHEME_SELECTED,
            EFFECT_STATIC_START,
            EFFECT_STATIC_NOT_SUPPORTED,
            EFFECT_STATIC_EXISTS,
            EFFECT_STATIC_UNKNOWN,
            EFFECT_STATIC_STOP,
            EFFECT_STATIC_TP,
            EFFECT_STATIC_TPHERE,
            EFFECT_STATIC_UPDATE,
            NOTIFICATIONS_TOGGLE,
            NOTIFICATIONS_DISABLED,
            RELOADING,
            RELOADED;

    public static String
            ARGS_MUST_BE_A_NUMBER,
            ARGS_USERNAME_TOO_SHORT,
            ARGS_NOT_A_VALID_NAME,
            ARGS_NO_PLAYER_FOUND_SERVER,
            ARGS_NO_PLAYER_FOUND_OFFLINE,
            ARGS_PAGE,
            ARGS_EFFECT,
            ARGS_MIX_NAME;

    public static String PAPI_CURRENT_EFFECT_NULL,
            PAPI_ENABLED,
            PAPI_DISABLED;

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean initialize() {
        for (var locale : Locale.values())
            locale.generate();

        try {
            LOCALE = Objects.requireNonNull(TreasureParticles.getConfig().getString("locale", "en")).toLowerCase(java.util.Locale.ENGLISH);

            generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
            config = generator.generate();

            if (config == null) {
                LOCALE = Locale.ENGLISH.getKey();
                TreasureParticles.getConfig().set("locale", LOCALE);
                TreasureParticles.saveConfig();

                generator = new ConfigurationGenerator("translations_" + LOCALE + ".yml", "translations");
                config = generator.generate();
                logGeneratedNewFile();
            } else if (!checkVersion()) {
                config = getConfiguration();
            }

            if (config == null)
                throw new NullPointerException("config is null");

            return true;
        } catch (Exception exception) {
            ComponentLogger.log("Couldn't load translations from 'translations_" + LOCALE + ".yml'", exception);
            return false;
        }
    }

    @Override
    public void reload() {
        if (initialize())
            loadTranslations();
    }

    public void loadTranslations() {
        // General
        PREFIX = load("prefix", "<b><gradient:#EF476F:#FFD166>TrParticles</b> <dark_gray><b>|</b></dark_gray>");
        NOTIFICATION = load("notification", "<prefix> <aqua><b><discord>Discord</discord></b> <dark_gray>/</dark_gray> <b><spigot>GitHub</spigot></b> <dark_gray>/</dark_gray> <b><wiki>Wiki</wiki>");
        ENABLED = load("enabled", "<green>Enabled");
        DISABLED = load("disabled", "<red>Disabled");

        // Buttons
        BUTTON_NEXT_PAGE = load("gui.next-page", "<green>> Next Page");
        BUTTON_PREVIOUS_PAGE = load("gui.previous-page", "<green>< Previous Page");
        BUTTON_CLOSE = load("gui.close", "<red>Close");
        BUTTON_BACK = load("gui.back", "<red>Back");
        BUTTON_FILTER = load("gui.filter", "<gold>Supported Events");
        FILTER_UP = load("gui.filter-up", "<yellow>↑ [Right-Click]");
        FILTER_DOWN = load("gui.filter-down", "<yellow>↓ [Left-Click]");
        FILTER_RESET = load("gui.filter-reset", "<red>✖ [Middle-Click]");

        // Effects GUI
        EFFECTS_GUI_TITLE = load("gui.effects-gui.title", "      <gradient:#EF476F:#FFD166><b>Treasure Particles");
        EFFECTS_GUI_SELECTED = load("gui.effects-gui.effect-selected", "<green>Selected!");
        EFFECTS_GUI_SELECT = load("gui.effects-gui.select-effect", "<#87FF65>❤ Click to use this effect!");
        EFFECTS_GUI_RANDOM = load("gui.effects-gui.random-effect", "<#87FF65>❤ Select Random Effect");
        EFFECTS_GUI_RESET = load("gui.effects-gui.reset-effect", "<yellow>Reset Effect");
        EFFECTS_GUI_CURRENT = load("gui.effects-gui.current-effect", "<gray>Current Effect: <gold>{0}");
        EFFECTS_GUI_ONLY_ELYTRA = load("gui.effects-gui.only-elytra", "<gold>Only works when Elytra is equipped.");
        COLOR_SELECTION_AVAILABLE = load("gui.effects-gui.color-selection-available", "<gray>[Right-Click] You can change the colors of this effect.");
        EFFECTS_GUI_EVENT_TYPE = load("gui.effects-gui.event-type", "<dark_gray>Event Type: {0}");
        EFFECTS_GUI_EVENT_TYPES = load("gui.effects-gui.event-types", "<dark_gray>Event Types: {0}");
        EFFECTS_GUI_MIXER = load("gui.effects-gui.mixer", "<dark_green>Effect Mixer");
        EFFECTS_GUI_OPEN_PLAYER_MIX_LIST = load("gui.effects-gui.open-player-mix-list", "<green>Click to open your effect mix list!");
        EFFECTS_GUI_OPEN_MIXER = load("gui.effects-gui.open-mixer", "<gray>[Right-Click] Open effect mixer GUI.");
        EFFECTS_GUI_REMOVE_MIX = load("gui.effects-gui.remove-mix", "<red>[Middle-Click] Remove mix.");
        EFFECTS_GUI_MIX_REMOVED = load("gui.effects-gui.mix-removed", "<prefix> <green>Mix removed.");

        // Colors GUI
        COLORS_GUI_TITLE = load("gui.colors-gui.title", "Pick a color scheme");
        COLORS_GUI_SCHEME_SELECTED = load("gui.colors-gui.color-scheme-selected", "<green>Selected!");
        COLORS_GUI_SELECT_SCHEME = load("gui.colors-gui.select-color-scheme", "<#87FF65>❤ Click to use the effect with this color scheme!");
        COLORS_GUI_SAVE_SCHEME = load("gui.colors-gui.save-color-scheme", "<gray><i>Right click to save this color scheme to preferences.");
        COLORS_GUI_RANDOM_COLOR = load("gui.colors-gui.random-color", "<#87FF65>❤ Select Random Color");

        // Mixer GUI
        MIXER_GUI_TITLE = load("gui.mixer-gui.title", "Effect Mixer");
        MIXER_GUI_ALL_SELECTED = load("gui.mixer-gui.all-selected", "<green>All handlers selected!");
        MIXER_GUI_SELECT_ALL = load("gui.mixer-gui.select-all", "<#87FF65>❤ Click to select this effect!");
        MIXER_GUI_SELECTED_ALL = load("gui.mixer-gui.selected-all", "<prefix> <green>Selected:<reset> {0}");
        MIXER_GUI_SELECTED_HANDLER = load("gui.mixer-gui.selected-handler", "<prefix> <green>Selected Handler:<reset> {0} <reset>- {1}");
        MIXER_GUI_UNSELECT_ALL = load("gui.mixer-gui.unselect-all", "<red>[Middle-Click] Unselect all handlers of this effect.");
        MIXER_GUI_UNSELECTED_ALL = load("gui.mixer-gui.unselected-all", "<prefix> <yellow>Unselected:<reset> {0}");
        MIXER_GUI_UNSELECTED_HANDLER = load("gui.mixer-gui.unselected-handler", "<prefix> <yellow>Unselected Handler:<reset> {0} <reset>- {1}");
        MIXER_GUI_SELECT_HANDLERS = load("gui.mixer-gui.select-handlers", "<gray>[Right-Click] You can select handlers individually.");
        MIXER_GUI_HAS_DYNAMIC_COLOR = load("gui.mixer-gui.has-dynamic-color", "<gray>• This effect has dynamic colors.");
        MIXER_GUI_PREFER_COLOR_GROUP = load("gui.mixer-gui.prefer-color-group", "<gray>  <yellow>Shift-right-click</yellow> to use this effect's color group.");
        MIXER_GUI_PREFERRED_COLOR_GROUP = load("gui.mixer-gui.preferred-color-group", "<green>Selected this effect's color group.");
        MIXER_GUI_CURRENT_SELECTIONS = load("gui.mixer-gui.current-selections", "<gray>Current Selections:");
        MIXER_GUI_RESET_SELECTIONS = load("gui.mixer-gui.reset-selections", "<red>Reset Selections");
        MIXER_GUI_CONFIRM = load("gui.mixer-gui.confirm", "<green>Confirm");
        MIXER_GUI_PREFERRED_COLOR_GROUP_NEEDED = load("gui.mixer-gui.preferred-color-group-needed", "<red>You must select an effect's color group to create your mix.");
        MIXER_GUI_ENTER_NAME = load("gui.mixer-gui.enter-name", "Enter a name for mix");
        MIXER_GUI_NAME_ALREADY_USED = load("gui.mixer-gui.name-already-used", "<prefix> <red>You already created an effect mix with this name, please try different name.");
        MIX_CREATED = load("gui.mixer-gui.created", "<prefix> <green>Mix created!");
        MIX_FAILED = load("gui.mixer-gui.failed", "<prefix> <red>Mix creation failed.");

        // Handlers GUI
        HANDLERS_GUI_SELECT = load("gui.handlers-gui.select", "<#87FF65>❤ Click to select this handler!");
        HANDLERS_GUI_SELECTED = load("gui.handlers-gui.selected", "<green>Selected!");
        HANDLERS_GUI_UNSELECT = load("gui.handlers-gui.unselect", "<gray>Click to unselect this handler.");
        HANDLER_EVENT_LOCKED = load("gui.handlers-gui.event-locked", "<yellow>You can no longer select another handler with event type.");
        HANDLER_EVENT_TYPE = load("gui.handlers-gui.event-type", "<dark_gray>Event: {0}");

        // Commands
        COMMAND_USAGE = load("commands.usage", "<prefix> <yellow>Usage:<gray> {0}");
        COMMAND_ERROR = load("commands.error", "<prefix> <red>Error: {0}");
        COMMAND_PLAYERS_ONLY = load("commands.players-only", "<prefix> <red>This command can only be executed by players.");
        COMMAND_NO_PERMISSION = load("commands.no-permission", "<prefix> <red>You do not have enough permission to perform this command!");
        EFFECT_NO_PERMISSION = load("commands.effect-no-permission", "<prefix> <red>You do not have enough permission to use that effect.");
        EFFECT_NO_PERMISSION_OTHER = load("commands.effect-no-permission-other", "<prefix> <red>That player doesn't have enough permission to use that effect.");
        EFFECT_UNKNOWN = load("commands.effect-unknown", "<prefix> <red>Couldn't find any effect with name {0}");
        EFFECT_TOGGLE = load("commands.effect-toggle", "<prefix> <gray>Effects: {0}");
        EFFECT_SELECTED = load("commands.effect-selected", "<prefix> <green>Selected:<reset> {0}");
        EFFECT_SELECTED_OTHER = load("commands.effect-selected-other", "<prefix> <green>Selected ({0}):<reset> {1}");
        EFFECT_RESET = load("commands.effect-reset", "<prefix> <gray>Effects: <red>OFF");
        EFFECT_RESET_OTHER = load("commands.effect-reset-other", "<prefix> <gray>Effects ({0}): <red>OFF");
        CANNOT_USE_ANY_EFFECT = load("commands.cannot-use-any-effect", "<prefix> <red>You cannot use any effect.");
        CANNOT_USE_ANY_EFFECT_OTHER = load("commands.cannot-use-any-effect-other", "<prefix> <red>That player cannot use any effect.");
        CANNOT_USE_ONLY_ELYTRA = load("commands.cannot-use-only-elytra", "<prefix> <red>You can only use this effect when Elytra is equipped.");
        CANNOT_USE_ONLY_ELYTRA_OTHER = load("commands.cannot-use-only-elytra", "<prefix> <red>They can only use this effect when Elytra is equipped.");
        MIX_UNKNOWN = load("commands.mix-unknown", "<prefix> <red>Could not find any mix with name {0}.<br><i>You can try typing the mix name without adding spaces. Example: <st>My Mix</st> <b>></b> MyMix");
        MIX_SELECTED = load("commands.mix-selected", "<prefix> <green>Mix selected:<reset> {0}");
        MIX_SELECTED_OTHER = load("commands.mix-selected-other", "<prefix> <green>Mix selected ({0}):<reset> {1}");
        UNKNOWN_COLOR_SCHEME = load("commands.unknown-color-scheme", "<prefix> <red>Unknown color scheme.");
        COLOR_SCHEME_SELECTED = load("commands.color-scheme-selected", "<prefix> <green>Selected color scheme {0} for the {1} effect.");
        //-Static Effects
        EFFECT_STATIC_START = load("commands.effect-static-start", "<prefix> <green>Started static effect {0}:<reset> {1}");
        EFFECT_STATIC_NOT_SUPPORTED = load("commands.effect-static-not-supported", "<prefix> <red>This effect does not support static.");
        EFFECT_STATIC_EXISTS = load("commands.effect-static-exists", "<prefix> <red>There is already a static effect with this id, please type a different id.");
        EFFECT_STATIC_UNKNOWN = load("commands.effect-static-unknown", "<prefix> <red>Unknown static effect id.");
        EFFECT_STATIC_STOP = load("commands.effect-static-stop", "<prefix> <gray>Stopped static effect {0}.");
        EFFECT_STATIC_TP = load("commands.effect-static-tp", "<prefix> <green>Teleported to the static effect {0}.");
        EFFECT_STATIC_TPHERE = load("commands.effect-static-tphere", "<prefix> <green>Teleported the static effect {0} to your location.");
        EFFECT_STATIC_UPDATE = load("commands.effect-static-update", "<prefix> <gray>Updated static effect {0}:<reset> {1}");
        //-Notifications
        NOTIFICATIONS_TOGGLE = load("commands.notifications-toggle", "<prefix> <gray>Notifications: {0}");
        NOTIFICATIONS_DISABLED = load("commands.notifications-disabled", "<prefix> <red>Notifications are disabled.");
        //-Reload
        RELOADING = load("commands.reloading", "<prefix> <yellow>Reloading TreasureParticles");
        RELOADED = load("commands.reloaded", "<prefix> <green>Reloaded!");
        //-Args
        ARGS_MUST_BE_A_NUMBER = load("commands.args.must-be-a-number", "<prefix> <red>{0} must be a number.");
        ARGS_USERNAME_TOO_SHORT = load("commands.args.username-too-short", "<prefix> <red>Username too short, must be at least three characters.");
        ARGS_NOT_A_VALID_NAME = load("commands.args.not-a-valid-name", "<prefix> <red>{0} is not a valid username.");
        ARGS_NO_PLAYER_FOUND_SERVER = load("commands.args.no-player-found-server", "<prefix> <red>No player matching {0} is connected to this server.");
        ARGS_NO_PLAYER_FOUND_OFFLINE = load("commands.args.no-player-found-offline", "<prefix> <red>No player matching {0} could be found.");
        ARGS_PAGE = load("commands.args.arg-page", "page");
        ARGS_EFFECT = load("commands.args.arg-effect", "effect");
        ARGS_MIX_NAME = load("commands.args.arg-mix-name", "mix-name");

        // PlaceholderAPI
        PAPI_CURRENT_EFFECT_NULL = load("placeholders.current-effect-null", "None");
        PAPI_ENABLED = load("placeholders.enabled", "Enabled");
        PAPI_DISABLED = load("placeholders.disabled", "Disabled");

        // Configure ACF
        var commandManager = TreasureParticles.getCommandManager();

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
        replacements.addReplacement("mix-name", ARGS_MIX_NAME);
    }

    public String load(String key, String defaultValue) {
        if (!config.contains(key)) {
            ComponentLogger.error(generator, "There is no translation for \"" + key + "\"");
            return defaultValue;
        }
        return config.getString(key);
    }

    public String get(String key) {
        return config.getString(key, "UNKNOWN");
    }

    public String get(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public String translate(String path, String message) {
        if (message == null) return null;

        var output = new StringBuilder();

        var array = message.toCharArray();
        var startPos = -1;
        var sb = new StringBuilder();

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
                    output.append(load(path + "." + result, result));
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
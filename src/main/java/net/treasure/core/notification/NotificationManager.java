package net.treasure.core.notification;

import lombok.Getter;
import lombok.Setter;
import net.treasure.core.TreasurePlugin;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class NotificationManager {

    private boolean enabled;
    private Set<String> versions = Set.of(
            "1.0.1",
            "1.0.2",
            "1.0.3",
            "1.0.4",
            "1.0.5",
            "1.1.0",
            "1.1.1"
    );

    public List<String> changelog() {
        return changelog(TreasurePlugin.getInstance().getDescription().getVersion());
    }

    public List<String> changelog(String version) {
        return switch (version) {
            case "1.0.1" -> List.of(
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue that caused the plugin to not enable",
                    "<yellow>•</yellow> Fixed an issue that didn't cause a problem with the plugin from working but was showing a warning in the console"
            );
            case "1.0.2" -> List.of(
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue related to MiniMessage library"
            );
            case "1.0.3" -> List.of(
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue related to Planor effect",
                    "",
                    "<yellow><b>[!]</b> Changes",
                    "<yellow>•</yellow> Added language support",
                    "<yellow>•</yellow> Added English translations",
                    "<yellow>•</yellow> Added language support to effects.yml. You can select the effect's display name from messages.yml",
                    "<yellow>•</yellow> Added all effects' translation (including English) for displayName in \"messages.yml\"."
            );
            case "1.0.4" -> List.of(
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue related to locale files.",
                    "",
                    "<yellow><b>[!]</b> Changes",
                    "<yellow>•</yellow> Added bStats support."
            );
            case "1.0.5" -> List.of(
                    "<yellow><b>[!]</b> Changes",
                    "<yellow>•</yellow> <gold>1.18.2</gold> Support"
            );
            case "1.1.0" -> List.of(
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue related to cache system",
                    "<yellow>•</yellow> Fixed an issue related to regex patterns",
                    "<yellow>•</yellow> Fixed the reload command is not reloading messages",
                    "",
                    "<yellow><b>[!]</b> Changes",
                    "<yellow>•</yellow> Added new script type <gold>\"conditional\"",
                    "<yellow>•</yellow> Added new script type <gold>\"none\"</gold> (especially for conditions)",
                    "<yellow>•</yellow> Added new script type <gold>\"preset\"",
                    "<yellow>•</yellow> Added new file named <gold>\"presets.yml\"",
                    "<white>- <i>See our <gold><hover:show_text:'<gold>Click!'><click:open_url:'https://github.com/ItsZypec/Treasure-Elytra/wiki'>Wiki Page</click></hover></gold> for detailed information.",
                    "",
                    "<yellow>•</yellow> Added permission for admin commands (trelytra.admin)",
                    "<yellow>•</yellow> Added permission for base command (trelytra.menu)",
                    "<white>- <i>You can change these permissions in \"config.yml\"",
                    "",
                    "<yellow>•</yellow> Added new effects: <gold>Helix, Rain, Rocket",
                    "",
                    "<yellow>•</yellow> Changed message files directory to <gold>../TrElytra/Messages/",
                    "<yellow>•</yellow> Added version for message files (current version: 1.1.0)",
                    "<yellow>•</yellow> MiniMessage Support",
                    "<yellow>  •</yellow> <underlined>From now on, color chars (&,§) chars cannot be used in messages",
                    "<yellow>  •</yellow> MiniMessage Format: <gold><underlined><click:open_url:'https://docs.adventure.kyori.net/minimessage/format.html'>https://docs.adventure.kyori.net/minimessage/format.html",
                    "",
                    "<yellow>•</yellow> Changed native version to <gold>1.16",
                    "<yellow>•</yellow> Optimized variable evaluations",
                    "<yellow>•</yellow> Removed <gold>\"speed\"</gold> option from colors.yml",
                    "<yellow>•</yellow> Added detailed logs for errors ",
                    "<yellow>•</yellow> Added pre-defined <gold>\"{RANDOM}\"</gold> variable",
                    "<yellow>•</yellow> Added <gold>abs(x)</gold> support (Absolute value) for mathematical equations",
                    "<yellow>•</yellow> Players can toggle effects visibility by <gold>/trelytra toggle</gold> command",
                    "<yellow>•</yellow> Added debug mode option (to enable, create a file named dev in the TrElytra folder)"
            );
            case "1.1.1" -> List.of(
                    "<yellow><b>[!]</b> Permissions",
                    "<yellow>•</yellow> You can define your permissions in config.yml now and use them in your effects",
                    "<yellow>  •</yellow> In <gold>config.yml</gold>, put your permission into the permissions section with a unique key",
                    "<yellow>  •</yellow> Then, in <gold>effects.yml</gold>, set the permission value to <gold>\"%key\"",
                    "",
                    "<yellow><b>[!]</b> Bug Fixes",
                    "<yellow>•</yellow> Fixed an issue related to conditions",
                    "<yellow>•</yellow> Fixed an issue related to quad helix effect (rotation correction)",
                    "<yellow>•</yellow> Fixed an issue related to effect reader",
                    "",
                    "<yellow><b>[!]</b> Changes",
                    "<yellow>•</yellow> Added new script type <gold>\"actionbar <message>\"",
                    "<yellow>•</yellow> Added notification system",
                    "<yellow>  •</yellow> You will get notification message every time you join the server, and this can be disabled in <gold>config.yml",
                    "<yellow>•</yellow> Optimized login listener"
            );
            default -> null;
        };
    }
}
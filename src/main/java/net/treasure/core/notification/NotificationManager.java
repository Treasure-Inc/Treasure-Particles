package net.treasure.core.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotificationManager {

    private boolean enabled;
    private List<String> versions = List.of(
            "1.0.1",
            "1.0.2",
            "1.0.3",
            "1.0.4",
            "1.0.5",
            "1.1.0",
            "1.1.1",
            "1.2.0",
            "1.2.1",
            "1.2.2",
            "1.2.3",
            "1.3.0"
    );

    // Needs rework
    public List<String> changelog(String version) {
        if (!versions.contains(version))
            return null;
        var list = new ArrayList<>(switch (version) {
            case "1.0.1" -> List.of(
                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue that caused the plugin to not enable<br>" +
                            "<gold>[2]</gold> Fixed an issue that didn't cause a problem with the plugin from working but was showing a warning in the console"
            );
            case "1.0.2" -> List.of(
                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue related to MiniMessage library"
            );
            case "1.0.3" -> List.of(
                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue related to Planor effect",

                    "<gold><b>[!] Changes</b></gold><br><br>" +
                            "<gold>[1]</gold> Added language support<br>" +
                            "<gold>[2]</gold> Added English translations<br>" +
                            "<gold>[3]</gold> Added language support to effects.yml. You can select the effect's display name from messages.yml",
                    "<gold>[4]</gold> Added all effects' translation for displayName in \"messages.yml\"."
            );
            case "1.0.4" -> List.of(
                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue related to locale files.<br><br>" +
                            "<gold><b>[!] Changes</b></gold><br><br>" +
                            "<gold>[1]</gold> Added bStats support."
            );
            case "1.0.5" -> List.of(
                    "<gold><b>[!] Changes</b></gold><br><br>" +
                            "<gold>[1]</gold> <gold>1.18.2</gold> Support"
            );
            case "1.1.0" -> List.of(
                    "<br><br><br><br><br><br><gold><b>[!] Messages",
                    "<gold>[1]</gold> Changed message files directory to <gold>../TrElytra/Messages/</gold><br>" +
                            "<gold>[2]</gold> Added version for message files (current: 1.1.0)<br>" +
                            "<gold>[3]</gold> MiniMessage Support<br>" +
                            "<gold>  •</gold> <underlined>From now on, color chars (&,§) chars cannot be used in messages</underlined><br>" +
                            "<gold>  •</gold> MiniMessage Format: <gold><underlined><click:open_url:'https://docs.adventure.kyori.net/minimessage/format.html'>CLICK HERE",

                    "<br><br><br><br><br><br><gold><b>[!] Bug Fixes",
                    "<gold>[1]</gold> Fixed an issue related to cache system<br>" +
                            "<gold>[2]</gold> Fixed an issue related to regex patterns<br>" +
                            "<gold>[3]</gold> Fixed the reload command is not reloading messages",

                    "<br><br><br><br><br><br><gold><b>[!] Changes",
                    "<gold>[1]</gold> Added new script type <gold>\"conditional\"</gold><br>" +
                            "<gold>[2]</gold> Added new script type <gold>\"none\"</gold> (especially for conditions)<br>" +
                            "<gold>[3]</gold> Added new script type <gold>\"preset\"</gold><br>" +
                            "<gold>[4]</gold> Added new file named <gold>\"presets.yml\"</gold><br><br>" +
                            "<i>See our <gold><hover:show_text:'<gold>Click!'><click:open_url:'https://github.com/Treasure-Inc/Treasure-Elytra/wiki'>Wiki Page</click></hover></gold> for detailed information.",

                    "<gold>[5]</gold> Added permission for admin commands (trelytra.admin)<br>" +
                            "<gold>[6]</gold> Added permission for base command (trelytra.menu)<br><br>" +
                            "<i>You can change these permissions in <gold>\"config.yml\"<br><br>" +
                            "<gold>[7]</gold> Added new effects: <gold>Helix, Rain, Rocket</gold>",
                    "<gold>[8]</gold> Changed native version to <gold>1.16</gold><br>" +
                            "<gold>[9]</gold> Optimized variable evaluations<br>" +
                            "<gold>[10]</gold> Removed <gold>\"speed\"</gold> option from colors.yml<br>" +
                            "<gold>[11]</gold> Added detailed logs for errors<br>" +
                            "<gold>[12]</gold> Added pre-defined <gold>\"{RANDOM}\"</gold> variable",
                    "<gold>[13]</gold> Added <gold>abs(x)</gold> support (Absolute value) for mathematical equations<br>" +
                            "<gold>[14]</gold> Players can toggle effects visibility by <gold>/trelytra toggle</gold> command<br>" +
                            "<gold>[15]</gold> Added debug mode option (to enable, create a file named dev in the TrElytra folder)"
            );
            case "1.1.1" -> List.of(
                    "<br><br><br><br><br><br><gold><b>[!] Permissions",
                    "<gold>[1]</gold> You can define your permissions in config.yml now and use them in your effects<br><br>" +
                            "<gold>  •</gold> In <gold>config.yml</gold>, put your permission into the permissions section with a unique key<br>" +
                            "<gold>  •</gold> Then, in <gold>effects.yml</gold>, set the permission value to <gold>\"%key\"",

                    "<br><br><br><br><br><br><gold><b>[!] Bug Fixes",
                    "<gold>[1]</gold> Fixed an issue related to conditions<br>" +
                            "<gold>[2]</gold> Fixed an issue related to quad helix effect (<i>rotation correction</i>)<br>" +
                            "<gold>[3]</gold> Fixed an issue related to effect reader",

                    "<br><br><br><br><br><br><gold><b>[!] Changes",
                    "<gold>[1]</gold> Removed debug screen (debug mode is still available but has no feature)<br>" +
                            "<gold>[2]</gold> Added permission for debug mode (<gold>trelytra.debug</gold>)<br>" +
                            "<gold>[3]</gold> Added new script types <gold>\"actionbar,chat,title\"",
                    "<gold>[4]</gold> Added notification system<br>" +
                            "<gold>  •</gold> You will get notification message every time you join the server, and this can be disabled in <gold>config.yml</gold><br><br>" +
                            "<gold>[5]</gold> Optimized login listener",
                    "<gold>[6]</gold> Added disable option for GUI animations to <gold>config.yml</gold><br>" +
                            "<gold>[7]</gold> Added tab completion for \"<gold>/trelytra select</gold>\" command"
            );
            case "1.2.0" -> List.of(
                    "<b>IMPORTANT!</b><br><br>You cannot use your current <gold>effects.yml</gold> file with the new version, you must update your effects by the new scheme or if you haven't changed anything yet, just delete the file and let the plugin create a new version of it.",
                    "<gold><b>[NEW] API Support</b></gold><br><br>" +
                            "<gold>[1]</gold> TreasureElytra now provides an API for you to add your custom script types<br>" +
                            "<gold>  •</gold> You can check out the <gold><hover:show_text:'<gold>Click!'><click:open_url:'https://github.com/Treasure-Inc/Treasure-Elytra/wiki'>Wiki Page</click></hover></gold> for detailed information",

                    "<br><br><br><br><br><br><gold><b>[!] Effects",
                    "<gold>[1]</gold> Added version for <gold>effects.yml</gold> file (1.2.0)<br>" +
                            "<gold>[2]</gold> Added new effects (current effect size: 28)<br>" +
                            "<gold>[3]</gold> Added support for <gold>block_marker</gold> and <gold>item_crack</gold> particles<br>" +
                            "<gold>[4]</gold> Added support for <gold>note</gold> particle (<dark_gray>rainbow</dark_gray> & <dark_gray>random-note</dark_gray> color options)",
                    "<gold>[5]</gold> Added support for <gold>dust_color_transition</gold> particle (color transitions)<br>" +
                            "<gold>[6]</gold> You can now define multiple tick handler",

                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue related to database<br>" +
                            "<gold>[2]</gold> Fixed the issues with the caching system",

                    "<br><br><br><br><br><br><gold><b>[!] Changes",
                    "<gold>[1]</gold> Added detailed log messages for errors and warnings<br>" +
                            "<gold>[2]</gold> Added <gold>/trelytra reset</gold> and <gold>/trelytra notifications</gold> commands<br>" +
                            "<gold>[3]</gold> Added version for <gold>config.yml</gold> file (1.2.0)<br>" +
                            "<gold>[4]</gold> Added version for <gold>colors.yml</gold> file (1.2.0)<br>" +
                            "<gold>[5]</gold> Updated messages version to 1.2.0 (new message translations)"
            );
            case "1.2.1" -> List.of(
                    "<gold><b>[!] Changes</b></gold><br><br>" +
                            "<gold>[1]</gold> <gold>1.19</gold> Support"
            );
            case "1.2.2" -> List.of(
                    "<gold><b>[!] Effects GUI</b></gold><br><br>" +
                            "<gold>[1]</gold> You can now change the item types and slots in the effects GUI<br><br>" +
                            "<gold>[2]</gold> Added <dark_gray>Current Effect: EFFECT</dark_gray> lore for Reset Effects button",

                    "<br><br><br><br><br><br><gold><b>[!] Effects",
                    "<gold>[1]</gold> Added support for <gold>sculk_charge</gold> and <gold>shriek</gold> particles<br><br>" +
                            "<gold>  •</gold> You can define <dark_gray>roll</dark_gray> value for sculk charge effect<br><br>" +
                            "<gold>  •</gold> You can define <dark_gray>delay</dark_gray> value for shriek effect<br>",
                    "<gold>[2]</gold> Added icon and description options for effects<br><br>" +
                            "<gold>[3]</gold> Updated effects version to 1.2.2 (new <dark_gray>sonic</dark_gray> effect for 1.19)",

                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed an issue related to <gold>/trelytra reset</gold> respond message<br><br>" +
                            "<gold>[2]</gold> Fixed an issue related to effects GUI",

                    "<br><br><br><br><br><br><gold><b>[!] Changes",
                    "<gold>[1]</gold> Added page argument <i>(optional)</i> for base command <gold>/trelytra [page]</gold><br><br>" +
                            "<gold>[2]</gold> Added player argument for reset effect command <gold>/trelytra reset [player]</gold> (admin command)",
                    "<gold>[3]</gold> Updated messages version to 1.2.2 (new translations)<br><br>" +
                            "<gold>[4]</gold> Updated config version to 1.2.2 (gui options)"
            );
            case "1.2.3" -> List.of(
                    "<gold><b>[!] Conditions</b></gold><br><br>" +
                            "<gold>[1]</gold> Added mathematical equation support for conditions",

                    "<gold><b>[!] Bug Fixes</b></gold><br><br>" +
                            "<gold>[1]</gold> Fixed the issues related to command responses",


                    "<br><br><br><br><br><br><gold><b>[!] Changes",
                    "<gold>[1]</gold> Added translations for commands<br><br>" +
                            "<gold>[2]</gold> Updated messages version to 1.2.3 (command translations)<br><br>" +
                            "<gold>[3]</gold> Updated config version to 1.2.3 (permission for notifications)"
            );
            default -> List.of("<red>WHAT?");
        });
        var index = versions.indexOf(version);
        String s = (index > 0 ? "<br><br><br><dark_aqua><b><hover:show_text:'<dark_aqua>Click!'><click:run_command:'/trelytra changes " + versions.get(index - 1) + "'>← PREVIOUS RELEASE</click></hover>" : "") +
                (index + 1 < versions.size() ? "<br><br><br><dark_aqua><b><hover:show_text:'<dark_aqua>Click!'><click:run_command:'/trelytra changes " + versions.get(index + 1) + "'>→ NEXT RELEASE</click></hover> " : "");
        list.add(s);
        return list;
    }
}
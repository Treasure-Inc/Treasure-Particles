package net.treasure.color;

import lombok.Getter;
import net.treasure.color.generator.Gradient;
import net.treasure.color.generator.Rainbow;
import net.treasure.color.group.ColorGroup;
import net.treasure.color.scheme.ColorScheme;
import net.treasure.color.scheme.GradientColorScheme;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorManager implements DataHolder {

    public static final String VERSION = "1.0.0";
    final ConfigurationGenerator generator;

    @Getter
    final List<ColorScheme> colors;
    @Getter
    final List<ColorGroup> groups;

    public ColorManager() {
        this.generator = new ConfigurationGenerator("colors.yml");
        this.colors = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    @Override
    public boolean initialize() {
        return generator.generate() != null;
    }

    @Override
    public void reload() {
        if (initialize()) {
            groups.clear();
            colors.clear();
            loadColors();
        }
    }

    @Override
    public boolean checkVersion() {
        return VERSION.equals(generator.getConfiguration().getString("version"));
    }

    public ColorScheme getColorScheme(String key) {
        if (key == null) return null;
        return colors.stream().filter(color -> color.getKey().equals(key)).findFirst().orElse(null);
    }

    public ColorGroup getColorGroup(String key) {
        if (key == null) return null;
        return groups.stream().filter(group -> group.getKey().equals(key)).findFirst().orElse(null);
    }

    public void loadColors() {
        var config = generator.getConfiguration();
        if (config == null)
            return;

        if (!checkVersion()) {
            if (!TreasurePlugin.getInstance().isAutoUpdateEnabled()) {
                TreasurePlugin.logger().warning("New version of colors.yml available (v" + VERSION + ")");
            } else {
                generator.reset();
                config = generator.generate();
                TreasurePlugin.logger().warning("Generated new colors.yml (v" + VERSION + ")");
            }
        }

        var section = config.getConfigurationSection("schemes");
        if (section == null)
            return;

        var translations = TreasurePlugin.getInstance().getTranslations();

        for (var key : section.getKeys(false)) {
            if (!checkColorSchemeName(key)) {
                TreasurePlugin.logger().warning("You cannot use '" + key + "' for color scheme name");
                continue;
            }
            var path = key + ".";
            if (!section.contains(path + "values")) {
                TreasurePlugin.logger().warning("Please define color values for color scheme '" + key + "'");
                continue;
            }
            if (!section.contains(path + "size")) {
                TreasurePlugin.logger().warning("Please define a size value for color scheme '" + key + "'");
                continue;
            }
            var displayName = section.getString(path + "name");
            displayName = translations.translate("colors", displayName);

            GradientColorScheme color = new GradientColorScheme(
                    key,
                    displayName,
                    section.getInt(path + "size"),
                    section.getStringList(path + "values").stream().map(Gradient::hex2Rgb).toArray(Color[]::new)
            );
            colors.add(color);
        }

        ColorScheme rainbow = new ColorScheme("rainbow", "%rainbow%");
        rainbow.getColors().addAll(Arrays.asList(new Rainbow().colors(config.getInt("rainbow-colors-size", 25))));
        colors.add(rainbow);

        loadColorGroups();
    }

    private void loadColorGroups() {
        var config = generator.getConfiguration();
        var section = config.getConfigurationSection("groups");
        if (section == null)
            return;

        var permissions = TreasurePlugin.getInstance().getPermissions();

        for (var key : section.getKeys(false)) {
            var tempSection = section.getConfigurationSection(key);
            if (tempSection == null) continue;
            var values = tempSection.getValues(false);
            if (values.isEmpty()) continue;
            ColorGroup group = new ColorGroup(
                    key,
                    values.entrySet()
                            .stream()
                            .map(e -> {
                                var permission = permissions.replace(String.valueOf(e.getValue()));
                                return new ColorGroup.Option(getColorScheme(e.getKey()), permission.equals("none") ? null : permission);
                            })
                            .filter(o -> o.colorScheme() != null)
                            .toList()
            );
            groups.add(group);
        }
    }

    public boolean checkColorSchemeName(String name) {
        return switch (name) {
            case "rainbow", "random-note" -> false;
            default -> true;
        };
    }
}
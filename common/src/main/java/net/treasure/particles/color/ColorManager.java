package net.treasure.particles.color;

import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.generator.Gradient;
import net.treasure.particles.color.generator.Rainbow;
import net.treasure.particles.color.group.ColorGroup;
import net.treasure.particles.color.scheme.ColorScheme;
import net.treasure.particles.color.scheme.GradientColorScheme;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.configuration.DataHolder;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.util.logging.ComponentLogger;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter

public class ColorManager implements DataHolder {

    public static final String VERSION = "1.5.0";
    private final ConfigurationGenerator generator;

    private final List<ColorScheme> colors;
    private final List<ColorGroup> groups;

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
    public String getVersion() {
        return VERSION;
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
        var config = getConfiguration();

        var section = config.getConfigurationSection("schemes");
        if (section == null) return;

        var translations = TreasureParticles.getTranslations();

        for (var key : section.getKeys(false)) {
            if (!checkColorSchemeName(key)) {
                ComponentLogger.error(generator, "You cannot use '" + key + "' for color scheme name");
                continue;
            }

            var tempSection = section.getConfigurationSection(key);
            if (tempSection == null) continue;

            if (!tempSection.contains("values")) {
                ComponentLogger.error(generator, "Please define color values for color scheme '" + key + "'");
                continue;
            }

            if (!tempSection.contains("size")) {
                ComponentLogger.error(generator, "Please define a size value for color scheme '" + key + "'");
                continue;
            }

            var displayName = tempSection.getString("name");
            displayName = translations.translate("colors", displayName);

            var color = new GradientColorScheme(
                    key,
                    displayName,
                    tempSection.getInt("size"),
                    tempSection.getStringList("values").stream().map(Gradient::hex2Rgb).toArray(Color[]::new)
            );
            colors.add(color);
        }

        var rainbow = new ColorScheme("rainbow", "%rainbow%");
        rainbow.getColors().addAll(Arrays.asList(new Rainbow().colors(config.getInt("rainbow-colors-size", 25))));
        colors.add(rainbow);

        loadColorGroups();
    }

    private void loadColorGroups() {
        var config = generator.getConfiguration();
        var section = config.getConfigurationSection("groups");
        if (section == null) return;

        var permissions = TreasureParticles.getPermissions();

        for (var key : section.getKeys(false)) {
            var tempSection = section.getConfigurationSection(key);
            if (tempSection == null) continue;

            var values = tempSection.getValues(false);
            if (values.isEmpty()) {
                ComponentLogger.error(generator, "Please define color values for color group '" + key + "'");
                continue;
            }

            var group = new ColorGroup(
                    key,
                    values.entrySet()
                            .stream()
                            .map(e -> {
                                var permission = permissions.replace(String.valueOf(e.getValue()));
                                return new ColorGroup.Option(getColorScheme(e.getKey()), permission.equals(Permissions.NONE_PERMISSION) ? null : permission);
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
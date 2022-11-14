package net.treasure.color;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.util.color.Gradient;
import net.treasure.util.color.Rainbow;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorManager implements DataHolder {

    public static final String VERSION = "1.3.0";
    final ConfigurationGenerator generator;

    @Getter
    final List<ColorScheme> colors;

    public ColorManager() {
        this.colors = new ArrayList<>();
        this.generator = new ConfigurationGenerator("colors.yml");
    }

    @Override
    public boolean initialize() {
        return generator.generate() != null;
    }

    @Override
    public void reload() {
        if (initialize()) {
            colors.clear();
            loadColors();
        }
    }

    @Override
    public boolean checkVersion() {
        return VERSION.equals(generator.getConfiguration().getString("version"));
    }

    public ColorScheme get(String key) {
        return colors.stream().filter(color -> color.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public void loadColors() {
        var config = generator.getConfiguration();
        if (config == null)
            return;

        if (!checkVersion()) {
            generator.reset();
            config = generator.generate();
            TreasurePlugin.logger().warning("Generated new colors.yml (v" + VERSION + ")");
        }

        var section = config.getConfigurationSection("colors");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            if (!checkColorSchemeName(key))
                continue;
            String path = key + ".";
            if (!section.contains(path + "values"))
                continue;
            GradientColorScheme color = new GradientColorScheme(
                    key,
                    section.getInt(path + "size", 10),
                    section.getStringList(path + "values").stream().map(Gradient::hex2Rgb).toArray(Color[]::new)
            );
            colors.add(color);
        }

        ColorScheme rainbow = new ColorScheme("rainbow");
        rainbow.getColors().addAll(Arrays.asList(new Rainbow().colors(25)));
        colors.add(rainbow);
    }

    public boolean checkColorSchemeName(String name) {
        return switch (name) {
            case "rainbow" -> false;
            default -> true;
        };
    }
}
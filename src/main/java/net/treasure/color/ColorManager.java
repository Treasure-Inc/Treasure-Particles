package net.treasure.color;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.util.color.Gradient;
import net.treasure.util.color.Rainbow;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ColorManager {

    @Getter
    private FileConfiguration config;

    @Getter
    private final List<Color> colors;

    public ColorManager() {
        this.colors = new ArrayList<>();
    }

    public Color get(String key) {
        return colors.stream().filter(color -> color.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public boolean load() {
        try {
            File file = new File(TreasurePlugin.getInstance().getDataFolder(), "colors.yml");
            boolean exists = file.exists();
            if (!exists)
                TreasurePlugin.getInstance().saveResource("colors.yml", false);
            config = YamlConfiguration.loadConfiguration(file);
            return exists;
        } catch (Exception e) {
            TreasurePlugin.getInstance().getLogger().log(Level.WARNING, "Couldn't load/create colors.yml", e);
            return false;
        }
    }

    public void reload() {
        if (load()) {
            colors.clear();
            loadColors();
        }
    }

    public void loadColors() {
        if (config == null)
            return;

        ConfigurationSection section = config.getConfigurationSection("colors");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("rainbow"))
                continue;
            String path = key + ".";
            if (!section.contains(path + "values"))
                continue;
            GradientColor color = new GradientColor(
                    key,
                    section.getInt(path + "size", 10),
                    section.getStringList(path + "values").stream().map(Gradient::hex2Rgb).toArray(java.awt.Color[]::new)
            );
            colors.add(color);
        }

        Color rainbow = new Color("rainbow");
        rainbow.getColors().addAll(Arrays.asList(new Rainbow().colors(25)));
        colors.add(rainbow);
    }
}

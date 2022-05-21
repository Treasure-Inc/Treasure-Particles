package net.treasure.effect.script.preset;

import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Presets implements DataHolder {

    final ConfigurationGenerator generator;
    FileConfiguration configuration;

    public Presets() {
        this.generator = new ConfigurationGenerator("presets.yml");
    }

    @Override
    public boolean initialize() {
        this.configuration = generator.generate();
        return configuration != null;
    }

    @Override
    public void reload() {
        initialize();
    }

    @Override
    public boolean checkVersion() {
        return true;
    }

    public List<String> get(String key) {
        if (configuration.isList(key))
            return configuration.getStringList(key);
        var script = configuration.getString(key);
        return script == null ? null : List.of(script);
    }
}
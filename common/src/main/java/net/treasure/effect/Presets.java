package net.treasure.effect;

import lombok.Getter;
import net.treasure.TreasureParticles;
import net.treasure.configuration.ConfigurationGenerator;
import net.treasure.configuration.DataHolder;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Script;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Presets implements DataHolder {

    public static final String VERSION = "1.1.0";

    @Getter
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
    public String getVersion() {
        return VERSION;
    }

    public void load() {
        if (!checkVersion()) {
            if (!TreasureParticles.isAutoUpdateEnabled()) {
                TreasureParticles.newVersionInfo(this);
            } else {
                generator.reset();
                configuration = generator.getConfiguration();
                TreasureParticles.generatedNewFile(this);
            }
        }
    }

    public void reset() {
        generator.reset();
        configuration = generator.generate();
    }

    public List<String> get(String key) {
        if (configuration.isList(key))
            return configuration.getStringList(key);
        var script = configuration.getString(key);
        return script == null ? null : List.of(script);
    }

    public Script read(Effect effect, String key) throws ReaderException {
        if (configuration.isList(key)) return null;
        var script = configuration.getString(key);
        if (script == null) return null;
        return TreasureParticles.getEffectManager().readLine(effect, script);
    }
}
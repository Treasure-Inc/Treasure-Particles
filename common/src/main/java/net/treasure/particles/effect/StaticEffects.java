package net.treasure.particles.effect;

import lombok.Getter;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.Location;

import java.util.HashMap;

@Getter
public class StaticEffects {

    private final ConfigurationGenerator generator;

    public StaticEffects() {
        this.generator = new ConfigurationGenerator("static_effects.yml");
    }

    public void set(String id, String effectKey, Location location) {
        var config = generator.getConfiguration();
        if (config == null) return;

        if (effectKey == null)
            config.set("statics." + id, null);
        else {
            config.set("statics." + id + ".effect", effectKey);
            config.set("statics." + id + ".location", location);
        }
        generator.save();
    }

    public HashMap<String, Pair<String, Location>> loadAll() {
        HashMap<String, Pair<String, Location>> data = new HashMap<>();

        var config = generator.generate(false);
        if (config == null) return data;

        var section = config.getConfigurationSection("statics");
        if (section == null) return data;
        for (var key : section.getKeys(false))
            data.put(key, new Pair<>(section.getString(key + ".effect"), section.getLocation(key + ".location")));

        return data;
    }
}
package net.treasure.particles.effect;

import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.effect.data.LocationEffectData;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

@Getter
public class StaticEffects {

    private final ConfigurationGenerator generator;

    public StaticEffects() {
        this.generator = new ConfigurationGenerator("static_effects.yml");
    }

    public void load(EffectManager effectManager) {
        Bukkit.getScheduler().runTaskLater(TreasureParticles.getPlugin(), () -> {
            var data = getStaticEffects();
            for (var entry : data.entrySet()) {
                var effect = effectManager.get(entry.getValue().getKey());
                if (effect == null) {
                    ComponentLogger.error(generator, "Unknown static (" + entry.getKey() + ") effect: " + entry.getValue().getKey());
                    continue;
                }
                if (!effect.isStaticSupported()) {
                    ComponentLogger.error(generator, "This effect does not support static (" + entry.getKey() + "): " + entry.getValue().getKey());
                    continue;
                }
                var d = new LocationEffectData(entry.getKey(), entry.getValue().getValue());
                d.setCurrentEffect(effect);
                effectManager.getData().put(entry.getKey(), d);
            }
        }, 20);
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

    public HashMap<String, Pair<String, Location>> getStaticEffects() {
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
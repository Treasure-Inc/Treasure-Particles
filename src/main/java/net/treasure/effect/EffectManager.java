package net.treasure.effect;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.effect.listener.GlideListener;
import net.treasure.effect.script.preset.Presets;
import net.treasure.effect.task.ParticleTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Getter

public class EffectManager implements DataHolder {

    ConfigurationGenerator generator;

    final List<Effect> effects;
    final Presets presets;

    public EffectManager() {
        this.generator = new ConfigurationGenerator("effects.yml");
        this.effects = new ArrayList<>();
        this.presets = new Presets();
        Bukkit.getPluginManager().registerEvents(new GlideListener(), TreasurePlugin.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(TreasurePlugin.getInstance(), new ParticleTask(), 0, 1);
    }

    @Override
    public boolean initialize() {
        try {
            if (!presets.initialize()) return false;
            return generator.generate() != null;
        } catch (Exception e) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't load/create effects.yml", e);
            return false;
        }
    }

    @Override
    public void reload() {
        if (initialize()) {
            effects.clear();
            loadEffects();
        }
    }

    @Override
    public boolean checkVersion() {
        return false;
    }

    public Effect get(String key) {
        return effects.stream().filter(color -> color.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public void loadEffects() {
        var config = generator.getConfiguration();
        if (config == null)
            return;

        ConfigurationSection section = config.getConfigurationSection("effects");
        if (section == null)
            return;

        var inst = TreasurePlugin.getInstance();
        var messages = inst.getMessages();
        var mainConfig = inst.getConfig();
        for (String key : section.getKeys(false)) {
            try {
                String path = key + ".";
                if (!section.contains(path + "onTick")) {
                    inst.getLogger().warning("Effect must have onTick section: " + key);
                    continue;
                }

                String displayName = section.getString(path + "displayName", key);
                if (displayName.startsWith("%"))
                    displayName = messages.get("effects." + displayName.substring(1), displayName);

                String permission = section.getString(path + "permission");
                if (permission != null && permission.startsWith("%"))
                    permission = mainConfig.getString("permissions." + permission.substring(1), permission);

                Effect effect = new Effect(
                        key,
                        displayName,
                        section.getString(path + "armorColor"),
                        permission,
                        section.getStringList(path + "onTick.do"),
                        section.getStringList(path + "onTick.doPost"),
                        section.getStringList(path + "variables"),
                        section.getInt(path + "onTick.interval", 1),
                        section.getInt(path + "onTick.times", 1),
                        section.getInt(path + "onTick.postTimes", 1),
                        section.getBoolean(path + "enableCaching", false)
                );
                effects.add(effect);
            } catch (Exception e) {
                inst.getLogger().warning("Couldn't load effect: " + key);
            }
        }
    }
}
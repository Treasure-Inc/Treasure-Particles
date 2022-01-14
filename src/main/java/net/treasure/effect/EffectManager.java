package net.treasure.effect;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.listener.GlideListener;
import net.treasure.effect.task.ParticleTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class EffectManager {

    @Getter
    private FileConfiguration config;

    @Getter
    private final List<Effect> effects;

    public EffectManager() {
        effects = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(new GlideListener(), TreasurePlugin.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(TreasurePlugin.getInstance(), new ParticleTask(), 0, 1);
    }

    public Effect get(String key) {
        return effects.stream().filter(color -> color.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public boolean load() {
        try {
            File file = new File(TreasurePlugin.getInstance().getDataFolder(), "effects.yml");
            if (!file.exists())
                TreasurePlugin.getInstance().saveResource("effects.yml", false);
            config = YamlConfiguration.loadConfiguration(file);
            return file.exists();
        } catch (Exception e) {
            TreasurePlugin.getInstance().getLogger().log(Level.WARNING, "Couldn't load/create effects.yml", e);
            return false;
        }
    }

    public void reload() {
        if (load()) {
            effects.clear();
            loadEffects();
        }
    }

    public void loadEffects() {
        if (config == null)
            return;

        ConfigurationSection section = config.getConfigurationSection("effects");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            String path = key + ".";
            if (!section.contains(path + "onTick"))
                continue;
            Effect effect = new Effect(
                    key,
                    ChatColor.translateAlternateColorCodes('&', section.getString(path + "displayName", key)),
                    section.getString(path + "armorColor"),
                    section.getString(path + "permission"),
                    section.getStringList(path + "onTick.do"),
                    section.getStringList(path + "onTick.doPost"),
                    section.getStringList(path + "variables"),
                    section.getInt(path + "onTick.interval", 1),
                    section.getInt(path + "onTick.times", 1),
                    section.getInt(path + "onTick.postTimes", 1),
                    section.getBoolean(path + "enableCaching", false));
            effects.add(effect);
        }
    }
}
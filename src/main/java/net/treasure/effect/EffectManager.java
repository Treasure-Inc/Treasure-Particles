package net.treasure.effect;

import lombok.Getter;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.effect.listener.GlideListener;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.basic.EmptyScript;
import net.treasure.effect.script.basic.ReturnScript;
import net.treasure.effect.script.conditional.reader.ConditionalScriptReader;
import net.treasure.effect.script.message.ActionBar;
import net.treasure.effect.script.message.ChatMessage;
import net.treasure.effect.script.message.reader.TitleReader;
import net.treasure.effect.script.particle.reader.ParticleReader;
import net.treasure.effect.script.preset.reader.PresetReader;
import net.treasure.effect.script.sound.reader.SoundReader;
import net.treasure.effect.script.variable.reader.VariableReader;
import net.treasure.effect.task.ParticleTask;
import net.treasure.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

@Getter
public class EffectManager implements DataHolder {

    public static final String VERSION = "1.2.0";

    ConfigurationGenerator generator;

    final List<Effect> effects;
    final Presets presets;
    final HashMap<String, ScriptReader<?>> readers;

    public EffectManager() {
        this.generator = new ConfigurationGenerator("effects.yml");
        this.effects = new ArrayList<>();
        this.presets = new Presets();
        this.readers = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new GlideListener(), TreasurePlugin.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(TreasurePlugin.getInstance(), new ParticleTask(), 0, 1);

        registerReader("variable", new VariableReader());
        registerReader("particle", new ParticleReader());
        registerReader("preset", new PresetReader());
        registerReader("conditional", new ConditionalScriptReader());
        registerReader("sound", new SoundReader());
        registerReader("chat", new ChatMessage());
        registerReader("actionbar", new ActionBar());
        registerReader("title", new TitleReader());
        registerReader("none", new EmptyScript());
        registerReader("return", new ReturnScript());
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
        return VERSION.equals(generator.getConfiguration().getString("version"));
    }

    public Effect get(String key) {
        return effects.stream().filter(color -> color.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public void loadEffects() {
        var inst = TreasurePlugin.getInstance();
        var config = generator.getConfiguration();
        if (config == null)
            return;

        if (!checkVersion()) {
            presets.reset();
            generator.reset();
            config = generator.getConfiguration();
            inst.getLogger().warning("Generated new effects.yml (v" + VERSION + ")");
        }

        ConfigurationSection section = config.getConfigurationSection("effects");
        if (section == null)
            return;

        var messages = inst.getMessages();
        var mainConfig = inst.getConfig();
        for (String key : section.getKeys(false)) {
            try {
                String path = key + ".";

                String displayName = section.getString(path + "displayName", key);
                if (displayName != null && displayName.startsWith("%"))
                    displayName = messages.get("effects." + displayName.substring(1), displayName);

                String permission = section.getString(path + "permission");
                if (permission != null && permission.startsWith("%"))
                    permission = mainConfig.getString("permissions." + permission.substring(1), permission);

                var handlerSection = section.getConfigurationSection(path + "onTick");
                if (handlerSection == null) {
                    inst.getLogger().warning("Effect must have onTick section: " + key);
                    continue;
                }

                LinkedHashMap<String, Pair<Integer, List<String>>> tickHandlers = new LinkedHashMap<>();
                for (String tickHandlerKey : handlerSection.getKeys(false)) {
                    tickHandlers.put(tickHandlerKey, new Pair<>(
                            handlerSection.getInt(tickHandlerKey + ".times", 1),
                            handlerSection.getStringList(tickHandlerKey + ".scripts")
                    ));
                }

                Effect effect = new Effect(
                        key,
                        displayName,
                        section.getString(path + "armorColor"),
                        permission,
                        section.getStringList(path + "variables"),
                        section.getInt(path + "interval", 1),
                        section.getBoolean(path + "enableCaching", false),
                        tickHandlers
                );

                effects.add(effect);
            } catch (Exception e) {
                e.printStackTrace();
                inst.getLogger().warning("Couldn't load effect: " + key);
            }
        }
    }

    public void registerReader(String key, ScriptReader<?> reader) {
        this.readers.put(key, reader);
    }

    public <S> S read(Effect effect, String key, String line) {
        // noinspection unchecked
        return (S) readers.get(key).read(effect, line);
    }

    public Script readLine(Effect effect, String line) {
        var inst = TreasurePlugin.getInstance();

        int interval = -1;
        int intervalIndex = line.lastIndexOf("~");
        if (intervalIndex != -1) {
            var args = Patterns.TILDE.split(line, 2);
            try {
                interval = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                inst.getLogger().warning("Invalid interval syntax: " + line);
            }
            line = args[0];
        }

        var args = Patterns.SPACE.split(line, 2);
        String type;
        try {
            type = args[0];
        } catch (Exception e) {
            return null;
        }

        Script script = read(effect, type, args.length == 1 ? null : args[1]);

        if (script != null && interval > 0)
            script.setInterval(interval);
        return script;
    }
}
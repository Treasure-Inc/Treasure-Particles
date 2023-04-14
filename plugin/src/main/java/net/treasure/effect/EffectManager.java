package net.treasure.effect;

import lombok.Getter;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.gui.config.GUIElements;
import net.treasure.core.gui.type.effects.EffectsGUI;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.listener.ElytraBoostListener;
import net.treasure.effect.listener.GlideListener;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.basic.BreakHandlerScript;
import net.treasure.effect.script.basic.BreakScript;
import net.treasure.effect.script.basic.EmptyScript;
import net.treasure.effect.script.basic.ReturnScript;
import net.treasure.effect.script.basic.reader.BasicScriptReader;
import net.treasure.effect.script.conditional.reader.ConditionalScriptReader;
import net.treasure.effect.script.message.ActionBar;
import net.treasure.effect.script.message.ChatMessage;
import net.treasure.effect.script.message.reader.TitleReader;
import net.treasure.effect.script.particle.reader.circle.CircleParticleReader;
import net.treasure.effect.script.particle.reader.dot.DotParticleReader;
import net.treasure.effect.script.preset.reader.PresetReader;
import net.treasure.effect.script.sound.reader.SoundReader;
import net.treasure.effect.script.variable.cycle.VariableCycleReader;
import net.treasure.effect.script.variable.reader.VariableReader;
import net.treasure.effect.task.EffectsTask;
import net.treasure.util.Pair;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

@Getter
public class EffectManager implements DataHolder {

    public static final String VERSION = "1.5.0";
    public static boolean EFFECTS_VISIBILITY_PERMISSION = false;
    public static boolean ALWAYS_CHECK_PERMISSION = true;

    final ConfigurationGenerator generator;

    final List<Effect> effects;
    final Presets presets;
    final HashMap<String, ScriptReader<?, ?>> readers;

    public EffectManager() {
        this.generator = new ConfigurationGenerator("effects.yml");
        this.effects = new ArrayList<>();
        this.presets = new Presets();
        this.readers = new HashMap<>();

        var inst = TreasurePlugin.getInstance();

        // Register listeners
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new GlideListener(inst.getPlayerManager()), inst);
        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent");
            pm.registerEvents(new ElytraBoostListener(inst.getPlayerManager()), inst);
            if (inst.isDebugModeEnabled())
                inst.getLogger().info("Registered PlayerElytraBoostEvent listener");
        } catch (Exception ignored) {
            if (inst.isDebugModeEnabled())
                inst.getLogger().warning("Couldn't register PlayerElytraBoostEvent listener (Paper 1.17+)");
        }

        // Run effects task
        Bukkit.getScheduler().runTaskTimerAsynchronously(inst, new EffectsTask(inst.getPlayerManager()), 0, 1);

        // Register readers
        registerReader("variable", new VariableReader(), "var");
        registerReader("variable-cycle", new VariableCycleReader(), "varc");
        registerReader("particle", new DotParticleReader(), "dot");
        registerReader("circle", new CircleParticleReader());
        registerReader("preset", new PresetReader());
        registerReader("conditional", new ConditionalScriptReader());
        registerReader("play-sound", new SoundReader(), "sound");
        registerReader("chat", new BasicScriptReader<>(ChatMessage::new));
        registerReader("actionbar", new BasicScriptReader<>(ActionBar::new));
        registerReader("title", new TitleReader());
        registerReader("none", new BasicScriptReader<>(s -> new EmptyScript()));
        registerReader("return", new BasicScriptReader<>(s -> new ReturnScript()));
        registerReader("break", new BasicScriptReader<>(s -> new BreakScript()));
        registerReader("break-handler", new BasicScriptReader<>(s -> new BreakHandlerScript()));
    }

    @Override
    public boolean initialize() {
        try {
            if (!presets.initialize()) return false;
            var config = generator.generate();
            if (config == null) return false;
            EFFECTS_VISIBILITY_PERMISSION = config.getBoolean("permissions.effects-visibility-permission", false);
            ALWAYS_CHECK_PERMISSION = config.getBoolean("always-check-effect-permission", true);
        } catch (Exception e) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't load/create effects.yml", e);
            return false;
        }
        return true;
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
        return effects.stream().filter(color -> color.getKey().equals(key)).findFirst().orElse(null);
    }

    public boolean has(String key) {
        return effects.stream().anyMatch(color -> color.getKey().equals(key));
    }

    public void loadEffects() {
        var current = System.currentTimeMillis();
        var inst = TreasurePlugin.getInstance();
        var config = generator.getConfiguration();
        if (config == null)
            return;

        if (!checkVersion()) {
            if (!TreasurePlugin.getInstance().isAutoUpdateEnabled()) {
                TreasurePlugin.logger().warning("New version of effects.yml available (v" + VERSION + ")");
                TreasurePlugin.logger().warning("New version of presets.yml available (v" + VERSION + ")");
            } else {
                presets.reset();
                generator.reset();
                config = generator.getConfiguration();
                inst.getLogger().warning("Generated new effects.yml (v" + VERSION + ")");
            }
        }

        var section = config.getConfigurationSection("effects");
        if (section == null) {
            inst.getLogger().warning("Couldn't find any effect");
            return;
        }

        var translations = inst.getTranslations();
        var permissions = inst.getPermissions();

        for (String key : section.getKeys(false)) {
            try {
                String path = key + ".";

                // Display Name
                String displayName = section.getString(path + "display-name", key);
                displayName = translations.translate("effects", displayName);

                // Permission
                String permission = section.getString(path + "permission");
                permission = permissions.replace(permission);

                // Tick Handlers
                var handlerSection = section.getConfigurationSection(path + "on-tick");
                if (handlerSection == null) {
                    inst.getLogger().warning("Effect must have onTick section: " + key);
                    continue;
                }

                LinkedHashMap<String, Pair<Integer, List<String>>> tickHandlers = new LinkedHashMap<>();
                for (var tickHandlerKey : handlerSection.getKeys(false)) {
                    tickHandlers.put(tickHandlerKey, new Pair<>(
                            handlerSection.getInt(tickHandlerKey + ".times", 1),
                            handlerSection.getStringList(tickHandlerKey + ".scripts")
                    ));
                }

                // Icon
                var icon = GUIElements.getItemStack(config, "effects." + path + "icon", EffectsGUI.DEFAULT_ICON.item());

                // Description
                List<String> description;
                if (section.contains(path + "description")) {
                    description = new ArrayList<>();
                    for (var s : section.getStringList(path + "description")) {
                        var translated = MessageUtils.parseLegacy(translations.translate("descriptions", s));
                        description.addAll(List.of(translated.split("%nl%")));
                    }
                } else {
                    description = null;
                }

                var effect = new Effect(
                        key,
                        displayName,
                        description != null ? description.toArray(String[]::new) : null,
                        icon,
                        section.getString(path + "armor-color"),
                        permission,
                        section.getStringList(path + "variables"),
                        section.getInt(path + "interval", 1),
                        section.getBoolean(path + "enable-caching", false),
                        tickHandlers,
                        inst.getColorManager().getColorGroup(section.getString(path + "color-group"))
                );

                effects.add(effect);
            } catch (Exception e) {
                inst.getLogger().log(Level.WARNING, "Couldn't load effect: " + key, e);
            }
        }
        inst.getLogger().info("Loaded " + effects.size() + " effects (" + (System.currentTimeMillis() - current) + "ms)");
    }

    public void registerReader(String key, ScriptReader<?, ?> reader, String... aliases) {
        this.readers.put(key, reader);
        for (var alias : aliases)
            this.readers.put(alias, reader);
    }

    public <S> S read(Effect effect, String type, String line) throws ReaderException {
        if (!readers.containsKey(type))
            throw new ReaderException("Invalid script type: " + type);
        // noinspection unchecked
        return (S) readers.get(type).read(effect, type, line);
    }

    public Script readLine(Effect effect, String line) throws ReaderException {
        var inst = TreasurePlugin.getInstance();

        int interval = -1;
        int intervalIndex = line.lastIndexOf("~");
        if (intervalIndex != -1) {
            var args = Patterns.TILDE.split(line, 2);
            try {
                interval = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                inst.getLogger().warning(effect.getPrefix() + "Invalid interval syntax: " + line);
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
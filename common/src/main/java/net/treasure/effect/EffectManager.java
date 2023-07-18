package net.treasure.effect;

import lombok.Getter;
import net.treasure.TreasureParticles;
import net.treasure.configuration.ConfigurationGenerator;
import net.treasure.configuration.DataHolder;
import net.treasure.constants.Patterns;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.handler.TickHandler;
import net.treasure.effect.listener.ElytraBoostListener;
import net.treasure.effect.listener.HandlerEventsListener;
import net.treasure.effect.mix.MixerOptions;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.basic.BreakHandlerScript;
import net.treasure.effect.script.basic.BreakScript;
import net.treasure.effect.script.basic.EmptyScript;
import net.treasure.effect.script.basic.ReturnScript;
import net.treasure.effect.script.basic.reader.BasicScriptReader;
import net.treasure.effect.script.conditional.reader.ConditionalScriptReader;
import net.treasure.effect.script.message.ActionBar;
import net.treasure.effect.script.message.ChatMessage;
import net.treasure.effect.script.message.reader.TitleReader;
import net.treasure.effect.script.parkour.reader.ParkourReader;
import net.treasure.effect.script.particle.reader.circle.CircleParticleReader;
import net.treasure.effect.script.particle.reader.circle.SpreadCircleParticleReader;
import net.treasure.effect.script.particle.reader.dot.DotParticleReader;
import net.treasure.effect.script.particle.reader.text.TextParticleReader;
import net.treasure.effect.script.preset.reader.PresetReader;
import net.treasure.effect.script.reader.DefaultReader;
import net.treasure.effect.script.sound.reader.SoundReader;
import net.treasure.effect.script.variable.cycle.VariableCycleReader;
import net.treasure.effect.script.variable.reader.VariableReader;
import net.treasure.effect.task.EffectsTask;
import net.treasure.effect.task.MovementCheck;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.type.effects.EffectsGUI;
import net.treasure.util.message.MessageUtils;
import net.treasure.util.tuples.Pair;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Getter
public class EffectManager implements DataHolder {

    public static final String VERSION = "1.1.0";

    final ConfigurationGenerator generator;

    final List<Effect> effects;
    final Presets presets;
    final HashMap<String, DefaultReader<?>> readers;

    public EffectManager() {
        this.generator = new ConfigurationGenerator("effects.yml");
        this.effects = new ArrayList<>();
        this.presets = new Presets();
        this.readers = new HashMap<>();

        var plugin = TreasureParticles.getPlugin();

        // Register listeners
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new HandlerEventsListener(TreasureParticles.getPlayerManager()), plugin);
        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent");
            pm.registerEvents(new ElytraBoostListener(TreasureParticles.getPlayerManager()), plugin);
            TreasureParticles.logger().info("Registered Paper events");
        } catch (Exception ignored) {
        }

        // Run tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EffectsTask(TreasureParticles.getPlayerManager()), 0, 1);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new MovementCheck(TreasureParticles.getPlayerManager()), 0, 5);

        // Variables
        registerReader(new VariableReader(), "variable", "var");
        registerReader(new VariableCycleReader(), "variable-cycle", "var-c");
        // Particles
        registerReader(new DotParticleReader(), "particle", "dot");
        registerReader(new CircleParticleReader(), "circle");
        registerReader(new SpreadCircleParticleReader(), "spread");
        registerReader(new ParkourReader(), "parkour");
        registerReader(new TextParticleReader(), "text");
        // Messages
        registerReader(new BasicScriptReader<>(ChatMessage::new), "chat");
        registerReader(new BasicScriptReader<>(ActionBar::new), "actionbar");
        registerReader(new TitleReader(), "title");
        // Sound
        registerReader(new SoundReader(), "play-sound", "sound");
        // Others
        registerReader(new PresetReader(), "preset");
        registerReader(new ConditionalScriptReader(), "conditional");

        registerReader(new BasicScriptReader<>(s -> new EmptyScript()), "none");
        registerReader(new BasicScriptReader<>(s -> new ReturnScript()), "return");
        registerReader(new BasicScriptReader<>(s -> new BreakScript()), "break");
        registerReader(new BasicScriptReader<>(s -> new BreakHandlerScript()), "break-handler");
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean initialize() {
        try {
            if (!presets.initialize()) return false;
            var config = generator.generate();
            if (config == null) return false;
        } catch (Exception e) {
            TreasureParticles.logger().log(Level.WARNING, "Couldn't load/create effects.yml", e);
            return false;
        }
        return true;
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTask(TreasureParticles.getPlugin(), () -> {
            if (initialize()) {
                effects.clear();
                loadEffects();
            }
        });
    }

    public Effect get(String key) {
        return effects.stream().filter(effect -> effect.getKey().equals(key)).findFirst().orElse(null);
    }

    public boolean has(String key) {
        return effects.stream().anyMatch(effect -> effect.getKey().equals(key));
    }

    public void loadEffects() {
        var start = System.nanoTime();
        var config = generator.getConfiguration();
        if (config == null) return;

        if (!checkVersion()) {
            if (!TreasureParticles.isAutoUpdateEnabled()) {
                TreasureParticles.newVersionInfo(this);
            } else {
                generator.reset();
                config = generator.getConfiguration();
                TreasureParticles.generatedNewFile(this);
            }
        }
        presets.load();

        var section = config.getConfigurationSection("effects");
        if (section == null) {
            TreasureParticles.logger().info("Couldn't find any effect");
            return;
        }

        var translations = TreasureParticles.getTranslations();
        var permissions = TreasureParticles.getPermissions();

        for (var key : section.getKeys(false)) {
            try {
                var path = section.getConfigurationSection(key);
                if (path == null) continue;

                // Display Name
                var displayName = translations.translate("effects", path.getString("display-name", key));

                // Permission
                var permission = permissions.replace(path.getString("permission"));

                // Interval
                var interval = path.getInt("interval", 1);
                if (interval < 1) {
                    TreasureParticles.logger().warning("[" + key + "] Invalid interval value: " + interval);
                    continue;
                }

                // Tick Handlers
                var onTickSection = path.getConfigurationSection("on-tick");
                if (onTickSection == null) {
                    TreasureParticles.logger().warning("[" + key + "] Effect must have on-tick section");
                    continue;
                }

                int tickHandlerIndex = 0;
                LinkedHashMap<String, Pair<TickHandler, List<String>>> tickHandlers = new LinkedHashMap<>();
                for (var tickHandlerKey : onTickSection.getKeys(false)) {
                    var tickHandlerSection = onTickSection.getConfigurationSection(tickHandlerKey);
                    if (tickHandlerSection == null) continue;
                    var event = tickHandlerSection.getString("event");
                    if (event == null) {
                        TreasureParticles.logger().warning("[" + key + "] Tick handler must have event: " + tickHandlerKey);
                        continue;
                    }

                    // Mixer Options
                    var mixerOptions = new MixerOptions();
                    if (tickHandlerSection.contains("mixer-options")) {
                        mixerOptions.lockEvent = tickHandlerSection.getBoolean("mixer-options.lock-event", mixerOptions.lockEvent);
                        mixerOptions.isPrivate = tickHandlerSection.getBoolean("mixer-options.private", mixerOptions.isPrivate);
                        mixerOptions.depends = tickHandlerSection.getStringList("mixer-options.depend");

                        if (mixerOptions.depends.stream().anyMatch(id -> !onTickSection.contains(id))) {
                            TreasureParticles.logger().warning("[" + key + "] Mixer options have unknown depend tick handlers");
                            continue;
                        }
                    }

                    try {
                        var tickHandlerInterval = tickHandlerSection.getInt("interval", interval);
                        if (tickHandlerInterval < interval) {
                            TreasureParticles.logger().warning("[" + key + "] Tick handler's interval cannot be lower than the effect's interval: " + tickHandlerKey);
                            continue;
                        }

                        if (!mixerOptions.isPrivate)
                            tickHandlerIndex++;
                        tickHandlers.put(tickHandlerKey, new Pair<>(
                                new TickHandler(
                                        tickHandlerKey,
                                        tickHandlerSection.getString("display-name", "[" + tickHandlerIndex + "]"),
                                        interval,
                                        tickHandlerSection.getInt("times", 1),
                                        mixerOptions,
                                        tickHandlerSection.getInt("max-executed", 0),
                                        tickHandlerSection.getBoolean("reset-event", true),
                                        event.equalsIgnoreCase("none") ? null : HandlerEvent.valueOf(event.toUpperCase(Locale.ENGLISH))
                                ),
                                tickHandlerSection.getStringList("scripts")
                        ));
                    } catch (IllegalArgumentException e) {
                        TreasureParticles.logger().warning("[" + key + "] Unknown event type: " + tickHandlerKey + ", " + event);
                    } catch (Exception e) {
                        TreasureParticles.logger().warning("[" + key + "] Couldn't read tick handler options: " + tickHandlerKey);
                    }
                }

                // Icon
                var icon = GUIElements.getItemStack(config, "effects." + key + ".icon", EffectsGUI.DEFAULT_ICON.item());

                // Description
                List<String> description = null;
                if (path.contains("description")) {
                    description = new ArrayList<>();
                    for (var s : path.getStringList("description")) {
                        var translated = MessageUtils.gui(translations.translate("descriptions", s));
                        description.addAll(List.of(translated.split("%nl%")));
                    }
                }

                var effect = new Effect(
                        key,
                        displayName,
                        description != null ? description.toArray(String[]::new) : null,
                        icon,
                        path.getString("armor-color"),
                        permission,
                        path.getStringList("variables"),
                        interval,
                        path.getBoolean("enable-caching", false),
                        tickHandlers,
                        TreasureParticles.getColorManager().getColorGroup(path.getString("color-group"))
                );
                effect.configure();

                effects.add(effect);
            } catch (Exception e) {
                TreasureParticles.logger().log(Level.WARNING, "Couldn't load effect: " + key, e);
            }
        }
        TreasureParticles.logger().info("Loaded " + effects.size() + " effects (" + TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS) + "ms)");
    }

    public void registerReader(DefaultReader<?> reader, String... aliases) {
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
        int interval = -1;
        int intervalIndex = line.lastIndexOf("~");
        if (intervalIndex != -1) {
            var args = Patterns.TILDE.split(line, 2);
            try {
                interval = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                TreasureParticles.logger().warning(effect.getPrefix() + "Invalid interval syntax: " + line);
            }
            line = args[0];
        }

        var args = Patterns.SPACE.split(line.trim(), 2);
        String type;
        try {
            type = args[0];
        } catch (Exception e) {
            return null;
        }

        Script script = read(effect, type, args.length == 1 ? null : args[1]);

        if (script != null) {
            script.setEffect(effect);
            if (interval > 0)
                script.setInterval(interval);
        }
        return script;
    }
}
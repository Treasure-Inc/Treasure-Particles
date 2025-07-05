package net.treasure.particles.effect;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.configuration.DataHolder;
import net.treasure.particles.constants.Patterns;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.LocationEffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.effect.listener.ElytraBoostListener;
import net.treasure.particles.effect.listener.HandlerEventsListener;
import net.treasure.particles.effect.mix.MixerOptions;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.basic.BreakHandlerScript;
import net.treasure.particles.effect.script.basic.BreakScript;
import net.treasure.particles.effect.script.basic.EmptyScript;
import net.treasure.particles.effect.script.basic.ReturnScript;
import net.treasure.particles.effect.script.basic.StopScript;
import net.treasure.particles.effect.script.basic.reader.BasicScriptReader;
import net.treasure.particles.effect.script.conditional.reader.ConditionalScriptReader;
import net.treasure.particles.effect.script.delay.reader.DelayReader;
import net.treasure.particles.effect.script.message.ActionBar;
import net.treasure.particles.effect.script.message.ChatMessage;
import net.treasure.particles.effect.script.message.reader.TitleReader;
import net.treasure.particles.effect.script.parkour.reader.ParkourReader;
import net.treasure.particles.effect.script.particle.reader.circle.CircleParticleReader;
import net.treasure.particles.effect.script.particle.reader.circle.SpreadCircleParticleReader;
import net.treasure.particles.effect.script.particle.reader.polygon.PolygonParticleReader;
import net.treasure.particles.effect.script.particle.reader.single.SingleParticleReader;
import net.treasure.particles.effect.script.particle.reader.sphere.SphereParticleReader;
import net.treasure.particles.effect.script.particle.reader.spiral.FullSpiralParticleReader;
import net.treasure.particles.effect.script.particle.reader.spiral.MultiSpiralParticleReader;
import net.treasure.particles.effect.script.particle.reader.spiral.SpiralParticleReader;
import net.treasure.particles.effect.script.particle.reader.target.TargetCircleParticleReader;
import net.treasure.particles.effect.script.particle.reader.target.TargetParticleReader;
import net.treasure.particles.effect.script.particle.reader.text.AnimatedTextParticleReader;
import net.treasure.particles.effect.script.particle.reader.text.TextParticleReader;
import net.treasure.particles.effect.script.preset.reader.PresetReader;
import net.treasure.particles.effect.script.reader.DefaultReader;
import net.treasure.particles.effect.script.sound.reader.SoundReader;
import net.treasure.particles.effect.script.variable.reader.VariableCycleReader;
import net.treasure.particles.effect.script.variable.reader.VariableReader;
import net.treasure.particles.effect.script.visual.reader.LightningReader;
import net.treasure.particles.effect.task.EffectsTask;
import net.treasure.particles.effect.task.MovementCheck;
import net.treasure.particles.gui.config.GUIElements;
import net.treasure.particles.gui.type.effects.EffectsGUI;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.message.MessageUtils;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EffectManager implements DataHolder {

    public static final String VERSION = "1.5.0";

    private final ConcurrentHashMap<String, EffectData> data;

    private final ConfigurationGenerator generator;
    private final List<Effect> effects;
    private final HashMap<String, DefaultReader<?>> readers;

    private final Presets presets;
    private final StaticEffects staticEffects;

    private int effectsTaskId = -5;

    public EffectManager() {
        this.generator = new ConfigurationGenerator("effects.yml");
        this.effects = new ArrayList<>();
        this.readers = new HashMap<>();
        this.data = new ConcurrentHashMap<>();

        this.presets = new Presets();
        this.staticEffects = new StaticEffects();

        // Register listeners
        ArmorEquipEvent.registerListener(TreasureParticles.getPlugin());
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new HandlerEventsListener(TreasureParticles.getPlayerManager()), TreasureParticles.getPlugin());
        if (TreasureParticles.isPaper())
            try {
                pm.registerEvents(new ElytraBoostListener(TreasureParticles.getPlayerManager()), TreasureParticles.getPlugin());
            } catch (Exception ignored) {
            }

        // Run tasks
        runTask();
        Bukkit.getScheduler().runTaskTimerAsynchronously(TreasureParticles.getPlugin(), new MovementCheck(TreasureParticles.getPlayerManager()), 0, 5);

        // Variables
        registerReader(new VariableReader(), "variable", "var");
        registerReader(new VariableCycleReader(), "variable-cycle", "var-c");

        // Particles
        registerReader(new SingleParticleReader(), "particle", "single");
        registerReader(new TargetParticleReader(), "target-particle", "target");
        //- Circles
        registerReader(new CircleParticleReader(), "circle");
        registerReader(new SpreadCircleParticleReader(), "spread");
        registerReader(new TargetCircleParticleReader(), "target-circle");
        //- Text
        registerReader(new TextParticleReader(), "text");
        registerReader(new AnimatedTextParticleReader(), "animated-text");
        //- Misc
        registerReader(new ParkourReader(), "parkour");
        registerReader(new PolygonParticleReader(), "polygon");
        //- Spirals
        registerReader(new SpiralParticleReader(), "spiral");
        registerReader(new MultiSpiralParticleReader(), "multi-spiral");
        registerReader(new FullSpiralParticleReader(), "full-spiral");
        //- Sphere
        registerReader(new SphereParticleReader(), "sphere");

        // Messages
        registerReader(new BasicScriptReader<>(ChatMessage::new), "chat");
        registerReader(new BasicScriptReader<>(ActionBar::new), "actionbar");
        registerReader(new TitleReader(), "title");

        // Sound
        registerReader(new SoundReader(), "play-sound", "sound");

        // Visual
        registerReader(new LightningReader(), "lightning");

        // Others
        registerReader(new PresetReader(), "preset");
        registerReader(new ConditionalScriptReader(), "conditional");
        //- Tick Handler Stuffs
        registerReader(new BasicScriptReader<>(s -> new EmptyScript()), "none");
        registerReader(new BasicScriptReader<>(s -> new ReturnScript()), "return");
        registerReader(new BasicScriptReader<>(s -> new BreakScript()), "break");
        registerReader(new BasicScriptReader<>(s -> new BreakHandlerScript()), "break-handler");
        registerReader(new BasicScriptReader<>(s -> new StopScript()), "stop");
        registerReader(new DelayReader(), "delay");
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
            ComponentLogger.log("Couldn't load/create effects.yml", e);
            return false;
        }
        return true;
    }

    @Override
    public void reload() {
        data.values().removeIf(d -> d instanceof LocationEffectData);
        effects.clear();
        if (initialize()) {
            loadEffects();
            runTask();
        }
    }

    public void runTask() {
        var task = new EffectsTask(this);
        task.runTaskTimerAsynchronously(TreasureParticles.getPlugin(), 5, 1);
        effectsTaskId = task.getTaskId();
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(effectsTaskId);
    }

    public Effect get(String key) {
        return effects.stream().filter(effect -> effect.getKey().equals(key)).findFirst().orElse(null);
    }

    public boolean has(String key) {
        return effects.stream().anyMatch(effect -> effect.getKey().equals(key));
    }

    public void loadEffects() {
        var config = getConfiguration();
        presets.load();

        var section = config.getConfigurationSection("effects");
        if (section == null) {
            ComponentLogger.error(generator, "Couldn't find any effect");
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
                    ComponentLogger.error("[" + key + "]", "Interval value must be greater or equal than 1");
                    continue;
                }

                // Tick Handlers
                var onTickSection = path.getConfigurationSection("on-tick");
                if (onTickSection == null) {
                    ComponentLogger.error("[" + key + "]", "Effect must have on-tick section");
                    continue;
                }

                int tickHandlerIndex = 0;
                LinkedHashMap<String, Pair<TickHandler, List<String>>> tickHandlers = new LinkedHashMap<>();
                for (var tickHandlerKey : onTickSection.getKeys(false)) {
                    var tickHandlerSection = onTickSection.getConfigurationSection(tickHandlerKey);
                    if (tickHandlerSection == null) continue;
                    var event = tickHandlerSection.getString("event");
                    if (event == null) {
                        ComponentLogger.error("[" + key + "]", "Tick handler must have event: " + tickHandlerKey);
                        continue;
                    }

                    // Mixer Options
                    var mixerOptions = new MixerOptions();
                    if (tickHandlerSection.contains("mixer-options")) {
                        mixerOptions.lockEvent = tickHandlerSection.getBoolean("mixer-options.lock-event", mixerOptions.lockEvent);
                        mixerOptions.isPrivate = tickHandlerSection.getBoolean("mixer-options.private", mixerOptions.isPrivate);
                        mixerOptions.depends = tickHandlerSection.getStringList("mixer-options.depend");

                        if (mixerOptions.depends.stream().anyMatch(id -> !onTickSection.contains(id))) {
                            ComponentLogger.error("[" + key + "]", "Mixer options have unknown depend tick handlers: " + tickHandlerKey);
                            continue;
                        }
                    }

                    try {
                        var tickHandlerInterval = tickHandlerSection.getInt("interval", interval);
                        if (tickHandlerInterval < interval) {
                            ComponentLogger.error("[" + key + "]", "Tick handler's interval cannot be lower than the effect's interval: " + tickHandlerKey);
                            continue;
                        }

                        if (!mixerOptions.isPrivate)
                            tickHandlerIndex++;
                        tickHandlers.put(tickHandlerKey, new Pair<>(
                                new TickHandler(
                                        tickHandlerKey,
                                        tickHandlerSection.getString("display-name", "[" + tickHandlerIndex + "]"),
                                        tickHandlerInterval,
                                        tickHandlerSection.getInt("times", 1),
                                        mixerOptions,
                                        tickHandlerSection.getInt("max-executed", 0),
                                        tickHandlerSection.getBoolean("reset-event", true),
                                        event.equalsIgnoreCase("none") ? null : HandlerEvent.valueOf(event.toUpperCase(Locale.ENGLISH))
                                ),
                                tickHandlerSection.getStringList("scripts")
                        ));
                    } catch (IllegalArgumentException e) {
                        ComponentLogger.error("[" + key + "]", "Unknown event type for '" + tickHandlerKey + "' tick handler: " + event);
                    } catch (Exception e) {
                        ComponentLogger.error("[" + key + "]", "Couldn't read tick handler options: " + tickHandlerKey);
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
                        path.getString("color-animation"),
                        permission,
                        path.getBoolean("name-color-animation", false),
                        path.getStringList("variables"),
                        interval,
                        path.getBoolean("enable-caching", false),
                        tickHandlers,
                        TreasureParticles.getColorManager().getColorGroup(path.getString("color-group")),
                        path.getBoolean("only-elytra", false)
                );
                effect.configure();

                effects.add(effect);
            } catch (Exception e) {
                ComponentLogger.log("Couldn't load effect: " + key, e);
            }
        }

        staticEffects.load(this);
    }

    public void registerReader(DefaultReader<?> reader, String... aliases) {
        for (var alias : aliases)
            this.readers.put(alias, reader);
    }

    public <S extends Script> S read(Effect effect, String type, String line) throws ReaderException {
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
                ComponentLogger.error(effect, "", line, intervalIndex, line.length(), "Invalid interval syntax");
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

        var script = read(effect, type, args.length == 1 ? null : args[1]);

        if (script != null) {
            script.setEffect(effect);
            if (interval > 0)
                script.setInterval(interval);
        }
        return script;
    }
}
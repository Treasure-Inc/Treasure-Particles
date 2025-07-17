package net.treasure.particles.effect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.group.ColorGroup;
import net.treasure.particles.constants.Patterns;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.EmptyEffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.effect.script.Cached;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.Script.TickResult;
import net.treasure.particles.effect.script.conditional.ConditionalScript;
import net.treasure.particles.effect.script.variable.Variable;
import net.treasure.particles.effect.script.variable.data.VariableData;
import net.treasure.particles.gui.type.mixer.MixerHolder;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.util.TimeKeeper;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.message.MessageUtils;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Effect {

    private final String key, displayName, colorAnimation, permission;
    private final boolean nameColorAnimationEnabled;

    private String[] description;
    private final ItemStack icon;

    private final int interval;
    private List<TickHandler> tickHandlers;

    private List<VariableData> variables;
    private final boolean cachingEnabled;

    private HashMap<String, double[][]> cache;
    private final ColorGroup colorGroup;

    private final EnumSet<HandlerEvent> events;
    private boolean staticSupported;

    private final boolean onlyElytra;

    private final String mixName;

    public Effect(String key, String displayName, String[] description, ItemStack icon, String colorAnimation, String permission, boolean nameColorAnimationEnabled, List<String> variables, int interval, boolean cachingEnabled, LinkedHashMap<String, Pair<TickHandler, List<String>>> tickHandlers, ColorGroup colorGroup, boolean onlyElytra) {
        this(key, displayName, description, icon, colorAnimation, permission, nameColorAnimationEnabled, interval, cachingEnabled, colorGroup, onlyElytra, null);

        this.variables = new ArrayList<>();
        for (var variable : variables) {
            if (hasVariable(variable)) {
                ComponentLogger.error(this, "Variable '" + variable + "' is already defined.");
                continue;
            }
            if (!isPredefinedVariable(variable))
                addVariable(variable);
            else
                ComponentLogger.error(this, "Variable '" + variable + "' is a pre-defined variable.");
        }

        addVariable(Variable.I);
        addVariable(Variable.TIMES);

        this.tickHandlers = new ArrayList<>();
        for (var entry : tickHandlers.entrySet()) {
            var pair = entry.getValue();
            var handler = pair.getKey();
            handler.lines = readScripts(handler, pair.getValue());
            this.tickHandlers.add(handler);
            if (handler.event != null)
                events.add(handler.event);

            if (handler.event == null || handler.event == HandlerEvent.STATIC)
                staticSupported = true;
        }
    }

    public Effect(String mixName, String key, String displayName, String[] description, ItemStack icon, String colorAnimation, String permission, boolean nameColorAnimationEnabled, List<VariableData> variables, int interval, boolean cachingEnabled, List<TickHandler> tickHandlers, ColorGroup colorGroup, boolean onlyElytra) {
        this(key, displayName, description, icon, colorAnimation, permission, nameColorAnimationEnabled, interval, cachingEnabled, colorGroup, onlyElytra, mixName);
        this.variables = variables;

        this.tickHandlers = tickHandlers;
        for (var handler : tickHandlers)
            if (handler.event != null)
                events.add(handler.event);

        addVariable(Variable.I, null);
        addVariable(Variable.TIMES, null);
    }

    public Effect(String key, String displayName, String[] description, ItemStack icon, String colorAnimation, String permission, boolean nameColorAnimationEnabled, int interval, boolean cachingEnabled, ColorGroup colorGroup, boolean onlyElytra, String mixName) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.colorAnimation = colorAnimation;
        this.permission = permission;
        this.nameColorAnimationEnabled = nameColorAnimationEnabled;
        this.interval = interval;
        this.cachingEnabled = cachingEnabled;
        this.colorGroup = colorGroup;
        this.onlyElytra = onlyElytra;
        this.mixName = mixName;

        this.events = EnumSet.noneOf(HandlerEvent.class);
    }

    public void configure() {
        if (cachingEnabled) {
            cache = new HashMap<>();
            preTick();
        }

        if (!TreasureParticles.getGUIManager().showSupportedEvents()) return;

        var old = this.description;
        this.description = new String[old == null ? 1 : old.length + 2];
        var translations = TreasureParticles.getTranslations();

        if (onlyElytra)
            this.description[0] = MessageUtils.gui(Translations.EFFECTS_GUI_ONLY_ELYTRA);
        else {
            var events = this.events.stream().filter(event -> event != HandlerEvent.STATIC).toList();
            if (events.isEmpty()) {
                this.description = old;
                return;
            }
            this.description[0] = MessageUtils.gui(events.size() > 1 ? Translations.EFFECTS_GUI_EVENT_TYPES : Translations.EFFECTS_GUI_EVENT_TYPE, events.stream().map(event -> translations.get("events." + event.translationKey())).collect(Collectors.joining(", ")));
        }


        if (old == null) return;
        this.description[1] = "";
        System.arraycopy(old, 0, this.description, 2, old.length);
    }

    public boolean canUse(Player player) {
        return permission == null || (player.hasPermission(permission) || player.hasPermission(Permissions.ACCESS_ALL_EFFECTS));
    }

    public void initialize(EffectData data) {
        // Clone variables
        List<VariableData> variables = new ArrayList<>();
        for (var variableData : this.variables) variables.add(variableData.clone());
        data.setVariables(variables);

        // Clone tick handlers and their scripts
        List<TickHandler> tickHandlers = new ArrayList<>();
        for (var tickHandler : this.tickHandlers)
            tickHandlers.add(tickHandler.clone());
        data.setTickHandlers(tickHandlers);
    }

    public void preTick() {
        var data = new EmptyEffectData(new ArrayList<>(variables));

        for (var tickHandler : tickHandlers) {
            int index = 0;
            for (var script : tickHandler.lines) {
                if (script instanceof Cached cached) {
                    cached.setIndex(index);
                    index++;
                } else if (script instanceof ConditionalScript conditionalScript) {
                    List<ConditionalScript> check = new ArrayList<>();
                    check.add(conditionalScript);
                    while (!check.isEmpty()) {
                        var latest = check.remove(0);
                        if (latest.getFirstExpression() instanceof Cached cached) {
                            cached.setIndex(index);
                            index++;
                        } else if (latest.getFirstExpression() instanceof ConditionalScript cs)
                            check.add(cs);

                        if (latest.getSecondExpression() instanceof Cached cached) {
                            cached.setIndex(index);
                            index++;
                        } else if (latest.getSecondExpression() instanceof ConditionalScript cs)
                            check.add(cs);
                    }
                }
            }
            if (index > 0)
                cache.put(tickHandler.key, new double[tickHandler.times][index]);
        }
        if (cache.isEmpty()) {
            ComponentLogger.error(this, "There is nothing to cache for this effect, you can disable the caching");
            cache = null;
            return;
        }

        // Get variable 'i'
        var indexData = data.getVariable(this, Variable.I);
        if (indexData == null) {
            ComponentLogger.error(this, "Couldn't pre-tick effect (Null variable: i)");
            return;
        }

        // Get variable 'times'
        var timesData = data.getVariable(this, Variable.TIMES);
        if (timesData == null) {
            ComponentLogger.error(this, "Couldn't pre-tick effect (Null variable: TIMES)");
            return;
        }

        for (var tickHandler : tickHandlers) {
            timesData.setValue(tickHandler.times);
            for (int i = 0; i < tickHandler.times; i++) {
                indexData.setValue(i);
                for (var script : tickHandler.lines) {
                    if (script instanceof Cached cached) {
                        cached.preTick(this, data, i);
                    }
                }
            }
        }
    }

    public void doTick(EffectData data) {
        if (interval != 1 && !TimeKeeper.isElapsed(interval)) return;
        var event = data.getCurrentEvent();
        TickHandler last = null;
        try {
            var indexData = data.getVariable(this, Variable.I);
            var timesData = data.getVariable(this, Variable.TIMES);
            if (indexData == null || timesData == null) {
                ComponentLogger.error(this, "Couldn't tick effect (Null variable data)");
                return;
            }

            for (var tickHandler : data.getTickHandlers()) {
                if (tickHandler.interval != 1 && !TimeKeeper.isElapsed(tickHandler.interval)) continue;
                last = tickHandler;
                timesData.setValue(tickHandler.times);

                if (!tickHandler.execute(data, event)) continue;
                tickHandlerLoop:
                for (int i = 0; i < tickHandler.times; i++) {
                    indexData.setValue(i);
                    for (var script : tickHandler.lines) {
                        var result = script.doTick(data, event, i);
                        if (result == TickResult.BREAK)
                            break;
                        else if (result == TickResult.BREAK_HANDLER)
                            break tickHandlerLoop;
                        else if (result == TickResult.RETURN)
                            return;
                    }
                }
            }
        } catch (Exception e) {
            ComponentLogger.log(getPrefix() + (last != null ? " Tick Handler: " + last.key : " Unexpected error"), e);
        }
    }

    public void addVariable(String var) {
        addVariable(var, null);
    }

    public void addVariable(String var, String effectKey) {
        var matcher = Patterns.VARIABLE.matcher(var);
        if (matcher.matches()) {
            String key = matcher.group("name");
            try {
                double value = Double.parseDouble(matcher.group("default"));
                this.variables.add(new VariableData(effectKey, key, value));
            } catch (NumberFormatException e) {
                this.variables.add(new VariableData(effectKey, key, 0D));
            }
        } else
            this.variables.add(new VariableData(effectKey, var, 0D));
    }

    public boolean hasVariable(String variableName) {
        for (var pair : variables)
            if (pair.getName().equals(variableName))
                return true;
        return false;
    }

    public boolean isValidVariable(String variableName) {
        for (var pair : variables)
            if (pair.getName().equals(variableName))
                return true;
        return isPredefinedVariable(variableName);
    }

    public boolean isPredefinedVariable(String variableName) {
        return switch (variableName) {
            case Variable.I, Variable.TIMES,
                 "2PI", "PI", "TICK", "RANDOM", "RANDOM-",
                 "currentTimeMillis", "CTM",
                 "lastBoostMillis", "LBM",
                 "isMoving", "isStanding",
                 "locationYaw", "locationPitch", "locationX", "locationY", "locationZ",
                 "velocityLength", "velocityX", "velocityY", "velocityZ" -> true;
            default -> false;
        };
    }

    public List<Script> readScripts(TickHandler tickHandler, List<String> lines) {
        List<Script> scripts = new ArrayList<>();
        var effectManager = TreasureParticles.getEffectManager();
        for (var line : lines) {
            try {
                var script = effectManager.readLine(this, line);
                if (script != null) {
                    script.setTickHandler(tickHandler);
                    scripts.add(script);
                } else
                    ComponentLogger.error(this, "Couldn't read line: " + line);
            } catch (ReaderException e) {
                if (e.getMessage() != null)
                    ComponentLogger.error(this, "Couldn't read line: " + line, new String[]{e.getMessage()});
            }
        }
        return scripts;
    }

    public String getPrefix() {
        return "[" + key + "]";
    }

    public String getParsedDisplayName() {
        return MessageUtils.gui(displayName);
    }

    public TickHandler getTickHandler(String key) {
        return tickHandlers.stream().filter(handler -> handler.key.equals(key)).findFirst().orElse(null);
    }

    public List<TickHandler> mixerCompatibleTickHandlers() {
        return tickHandlers.stream().filter(handler -> !handler.mixerOptions.isPrivate).toList();
    }

    public List<TickHandler> mixerCompatibleTickHandlersGUI(MixerHolder holder) {
        return tickHandlers.stream().filter(handler -> !handler.mixerOptions.isPrivate && (holder.isSelected(handler) || (holder.canSelectAnotherEffect() && !holder.isLocked(handler.event) && (holder.getFilter() == null || holder.getFilter() == handler.event)))).toList();
    }

    public List<TickHandler> mixerCompatibleTickHandlers(MixerHolder holder) {
        return tickHandlers.stream().filter(handler -> !handler.mixerOptions.isPrivate && (holder.isSelected(handler) || (holder.canSelectAnotherEffect() && !holder.isLocked(handler.event)))).toList();
    }
}
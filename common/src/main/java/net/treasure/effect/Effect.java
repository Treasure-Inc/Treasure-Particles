package net.treasure.effect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.TreasureParticles;
import net.treasure.color.group.ColorGroup;
import net.treasure.constants.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.handler.TickHandler;
import net.treasure.effect.script.Cached;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.conditional.ConditionalScript;
import net.treasure.effect.script.variable.Variable;
import net.treasure.gui.type.mixer.MixerHolder;
import net.treasure.locale.Translations;
import net.treasure.util.TimeKeeper;
import net.treasure.util.message.MessageUtils;
import net.treasure.util.tuples.Pair;
import net.treasure.util.tuples.Triplet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Effect {

    private final String key, displayName, armorColor, permission;

    private String[] description;
    private final ItemStack icon;
    private final int interval;

    private List<TickHandler> tickHandlers;
    private List<Triplet<String, Double, String>> variables;
    private final boolean cachingEnabled;

    private HashMap<String, double[][]> cache;
    private final ColorGroup colorGroup;

    private final EnumSet<HandlerEvent> events;

    public Effect(String key, String displayName, String[] description, ItemStack icon, String armorColor, String permission, List<String> variables, int interval, boolean cachingEnabled, LinkedHashMap<String, Pair<TickHandler, List<String>>> tickHandlers, ColorGroup colorGroup) {
        this(key, displayName, description, icon, armorColor, permission, interval, cachingEnabled, colorGroup);

        this.variables = new ArrayList<>();
        for (var variable : variables) {
            if (hasVariable(variable)) {
                TreasureParticles.logger().warning(getPrefix() + "Variable '" + variable + "' is already defined.");
                continue;
            }
            if (!isPredefinedVariable(variable))
                addVariable(variable);
            else
                TreasureParticles.logger().warning(getPrefix() + "'" + variable + "' is a pre-defined variable.");
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
        }
    }

    public Effect(String key, String displayName, String[] description, ItemStack icon, String armorColor, String permission, List<Triplet<String, Double, String>> variables, int interval, boolean cachingEnabled, List<TickHandler> tickHandlers, ColorGroup colorGroup) {
        this(key, displayName, description, icon, armorColor, permission, interval, cachingEnabled, colorGroup);
        this.variables = variables;

        this.tickHandlers = tickHandlers;
        for (var handler : tickHandlers)
            if (handler.event != null)
                events.add(handler.event);

        addVariable(Variable.I, null);
        addVariable(Variable.TIMES, null);
    }

    public Effect(String key, String displayName, String[] description, ItemStack icon, String armorColor, String permission, int interval, boolean cachingEnabled, ColorGroup colorGroup) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.armorColor = armorColor;
        this.permission = permission;
        this.interval = interval;
        this.cachingEnabled = cachingEnabled;
        this.colorGroup = colorGroup;

        this.events = EnumSet.noneOf(HandlerEvent.class);
    }

    public void configure() {
        if (cachingEnabled) {
            cache = new HashMap<>();
            preTick();
        }

        var translations = TreasureParticles.getTranslations();

        if (events.isEmpty()) return;
        var old = this.description;

        this.description = new String[old == null ? 1 : old.length + 2];
        this.description[0] = MessageUtils.gui(events.size() > 1 ? Translations.EFFECTS_GUI_EVENT_TYPES : Translations.EFFECTS_GUI_EVENT_TYPE, events.stream().map(event -> translations.get("events." + event.translationKey())).collect(Collectors.joining(", ")));
        if (old == null) return;
        this.description[1] = "";
        System.arraycopy(old, 0, this.description, 2, old.length);
    }

    public boolean canUse(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public void initialize(EffectData data) {
        // Clone variables
        List<Triplet<String, Double, String>> variables = new ArrayList<>();
        for (var triplet : this.variables) variables.add(triplet.clone());
        data.setVariables(variables);

        // Clone tick handlers and their scripts
        List<TickHandler> tickHandlers = new ArrayList<>();
        for (var tickHandler : this.tickHandlers)
            tickHandlers.add(tickHandler.clone());
        data.setTickHandlers(tickHandlers);
    }

    public void preTick() {
        var data = new EffectData(new ArrayList<>(variables));

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
            TreasureParticles.logger().warning(getPrefix() + "There is nothing to cache for this effect, you can disable the caching");
            cache = null;
            return;
        }

        // Get variable 'i'
        var ip = data.getVariable(this, Variable.I);
        if (ip == null) {
            TreasureParticles.logger().warning(getPrefix() + "Couldn't pre-tick effect (Null variable: i)");
            return;
        }

        // Get variable 'times'
        var tp = data.getVariable(this, Variable.TIMES);
        if (tp == null) {
            TreasureParticles.logger().warning(getPrefix() + "Couldn't pre-tick effect (Null variable: TIMES)");
            return;
        }

        for (var tickHandler : tickHandlers) {
            tp.y((double) tickHandler.times);
            for (int i = 0; i < tickHandler.times; i++) {
                ip.y((double) i);
                for (var script : tickHandler.lines) {
                    if (script instanceof Cached cached) {
                        cached.preTick(this, data, i);
                    }
                }
            }
        }
    }

    public void doTick(Player player, EffectData data) {
        if (interval > 1 && !TimeKeeper.isElapsed(interval)) return;
        var event = data.getCurrentEvent();
        TickHandler last = null;
        try {
            var ip = data.getVariable(this, Variable.I);
            var tp = data.getVariable(this, Variable.TIMES);
            if (ip == null || tp == null) {
                TreasureParticles.logger().warning(getPrefix() + "Couldn't tick effect (Null variable: i or TIMES)");
                return;
            }
            for (var tickHandler : data.getTickHandlers()) {
                if (tickHandler.interval > 1 && !TimeKeeper.isElapsed(tickHandler.interval)) continue;
                last = tickHandler;
                tp.y((double) tickHandler.times);

                if (!tickHandler.execute(data, event)) continue;
                tickHandlerLoop:
                for (int i = 0; i < tickHandler.times; i++) {
                    ip.y((double) i);
                    for (var script : tickHandler.lines) {
                        var result = script.doTick(player, data, event, i);
                        if (result == Script.TickResult.BREAK)
                            break;
                        else if (result == Script.TickResult.BREAK_HANDLER)
                            break tickHandlerLoop;
                        else if (result == Script.TickResult.RETURN)
                            return;
                    }
                }
            }
        } catch (Exception e) {
            TreasureParticles.logger().log(Level.WARNING, getPrefix() + (last != null ? "Tick Handler: " + last.key : "Unexpected error."), e);
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
                this.variables.add(new Triplet<>(key, value, effectKey));
            } catch (NumberFormatException e) {
                this.getVariables().add(new Triplet<>(key, 0d, effectKey));
            }
        } else
            this.getVariables().add(new Triplet<>(var, 0d, effectKey));
    }

    public boolean hasVariable(String var) {
        for (var pair : variables)
            if (pair.x().equals(var))
                return true;
        return false;
    }

    public boolean isValidVariable(String var) {
        for (var pair : variables)
            if (pair.x().equals(var))
                return true;
        return isPredefinedVariable(var);
    }

    public boolean isPredefinedVariable(String var) {
        return switch (var) {
            case Variable.I, Variable.TIMES,
                    "PI", "TICK", "RANDOM",
                    "currentTimeMillis", "CTM",
                    "lastBoostMillis", "LBM",
                    "isMoving", "isStanding",
                    "playerYaw", "playerPitch", "playerX", "playerY", "playerZ",
                    "velocityLength", "velocityX", "velocityY", "velocityZ" -> true;
            default -> false;
        };
    }

    public List<Script> readScripts(TickHandler tickHandler, List<String> lines) {
        List<Script> scripts = new ArrayList<>();
        var effectManager = TreasureParticles.getEffectManager();
        var logger = TreasureParticles.logger();
        for (var line : lines) {
            try {
                var script = effectManager.readLine(this, line);
                if (script != null) {
                    script.setTickHandler(tickHandler);
                    scripts.add(script);
                } else
                    logger.log(Level.WARNING, getPrefix() + "Couldn't read line: " + line);
            } catch (ReaderException e) {
                if (e.getMessage() != null) {
                    logger.log(Level.WARNING, getPrefix() + "Couldn't read line: " + line);
                    logger.warning("└ " + e.getMessage());
                }
            }
        }
        return scripts;
    }

    public String getPrefix() {
        return "[" + key + "] ";
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

    public List<TickHandler> mixerCompatibleTickHandlers(MixerHolder holder) {
        return tickHandlers.stream().filter(handler -> !handler.mixerOptions.isPrivate && (holder.isSelected(handler) || (holder.canSelectAnotherEffect() && !holder.isLocked(handler.event)))).toList();
    }
}
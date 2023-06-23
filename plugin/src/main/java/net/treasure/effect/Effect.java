package net.treasure.effect;

import lombok.Getter;
import net.treasure.color.group.ColorGroup;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Cached;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.conditional.ConditionalScript;
import net.treasure.effect.script.variable.Variable;
import net.treasure.util.TimeKeeper;
import net.treasure.util.message.MessageUtils;
import net.treasure.util.tuples.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

@Getter
public class Effect {

    private final String key, displayName, armorColor, permission;
    private final String[] description;
    private final ItemStack icon;

    private final int interval;
    private final List<TickHandler> tickHandlers;
    private final List<Pair<String, Double>> variables;

    private final boolean enableCaching;
    private HashMap<String, double[][]> cache;

    private final ColorGroup colorGroup;

    private final List<HandlerEvent> events;

    public Effect(String key, String displayName, String[] description, ItemStack icon, String armorColor, String permission, List<String> variables, int interval, boolean enableCaching, LinkedHashMap<String, Pair<TickHandler, List<String>>> tickHandlers, ColorGroup colorGroup) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.armorColor = armorColor;
        this.permission = permission;
        this.interval = interval;
        this.enableCaching = enableCaching;
        this.colorGroup = colorGroup;

        this.variables = new ArrayList<>();
        this.tickHandlers = new ArrayList<>();

        for (var variable : variables) {
            if (hasVariable(variable)) {
                TreasurePlugin.logger().warning(getPrefix() + "Variable '" + variable + "' is already defined.");
                continue;
            }
            if (checkPredefinedVariable(variable))
                addVariable(variable);
            else
                TreasurePlugin.logger().warning(getPrefix() + "'" + variable + "' is pre-defined variable.");
        }

        this.events = new ArrayList<>();

        for (var entry : tickHandlers.entrySet()) {
            var pair = entry.getValue();
            var handler = pair.getKey();
            handler.lines = readScripts(handler, pair.getValue());
            this.tickHandlers.add(handler);
            if (handler.event != null)
                this.events.add(handler.event);
        }

        addVariable(Variable.I);
        addVariable(Variable.TIMES);

        if (enableCaching) {
            cache = new HashMap<>();
            preTick();
        }
    }

    public boolean canUse(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public void initialize(Player player, EffectData data) {
        List<Pair<String, Double>> variables = new ArrayList<>();
        for (var pair : this.variables)
            variables.add(pair.clone());
        data.setVariables(variables);

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
            TreasurePlugin.logger().warning(getPrefix() + "There is nothing to cache for this effect, you can disable the caching");
            cache = null;
            return;
        }

        // Get variable 'i'
        var ip = data.getVariable(Variable.I);
        if (ip == null) {
            TreasurePlugin.logger().warning(getPrefix() + "Couldn't pre-tick effect (Variable.I == null)");
            return;
        }

        // Get variable 'times'
        var tp = data.getVariable(Variable.TIMES);
        if (tp == null) {
            TreasurePlugin.logger().warning(getPrefix() + "Couldn't pre-tick effect (Variable.TIMES == null)");
            return;
        }

        for (var tickHandler : tickHandlers) {
            tp.setValue((double) tickHandler.times);
            for (int i = 0; i < tickHandler.times; i++) {
                ip.setValue((double) i);
                for (var script : tickHandler.lines) {
                    if (script instanceof Cached cached) {
                        cached.preTick(this, data, i);
                    }
                }
            }
        }
    }

    public void doTick(Player player, EffectData data) {
        if (!TimeKeeper.isElapsed(interval)) return;
        var event = data.getCurrentEvent();
        TickHandler last = null;
        try {
            var ip = data.getVariable(Variable.I);
            var tp = data.getVariable(Variable.TIMES);
            if (ip == null || tp == null) {
                TreasurePlugin.logger().warning(getPrefix() + "Couldn't tick effect (Null variable: i or TIMES)");
                return;
            }
            for (var tickHandler : data.getTickHandlers()) {
                last = tickHandler;
                tp.setValue((double) tickHandler.times);

                if (!tickHandler.execute(data, event)) continue;
                tickHandlerLoop:
                for (int i = 0; i < tickHandler.times; i++) {
                    ip.setValue((double) i);
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
            TreasurePlugin.logger().log(Level.WARNING, getPrefix() + (last != null ? "Tick Handler: " + last.key : "Unexpected error."), e);
        }
    }

    public void addVariable(String var) {
        var matcher = Patterns.VARIABLE.matcher(var);
        if (matcher.matches()) {
            String key = matcher.group("name");
            try {
                double value = Double.parseDouble(matcher.group("default"));
                this.variables.add(new Pair<>(key, value));
            } catch (NumberFormatException e) {
                this.getVariables().add(new Pair<>(key, 0d));
            }
        } else
            this.getVariables().add(new Pair<>(var, 0d));
    }

    public boolean hasVariable(String var) {
        for (var pair : variables)
            if (pair.getKey().equals(var))
                return true;
        return false;
    }

    public boolean isValidVariable(String var) {
        for (var pair : variables)
            if (pair.getKey().equals(var))
                return true;
        return !checkPredefinedVariable(var);
    }

    public boolean checkPredefinedVariable(String var) {
        return switch (var) {
            case Variable.I, Variable.TIMES,
                    "PI", "TICK", "RANDOM",
                    "currentTimeMillis", "CTM",
                    "lastBoostMillis", "LBM",
                    "isMoving", "isStanding",
                    "playerYaw", "playerPitch", "playerX", "playerY", "playerZ",
                    "velocityLength", "velocityX", "velocityY", "velocityZ" -> false;
            default -> true;
        };
    }

    public List<Script> readScripts(TickHandler tickHandler, List<String> lines) {
        List<Script> scripts = new ArrayList<>();
        var effectManager = TreasurePlugin.getInstance().getEffectManager();
        var logger = TreasurePlugin.logger();
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
        return MessageUtils.parseLegacy(displayName);
    }
}
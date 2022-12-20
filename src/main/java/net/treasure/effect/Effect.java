package net.treasure.effect;

import lombok.Getter;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.conditional.ConditionalScript;
import net.treasure.effect.script.variable.Variable;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Getter
public class Effect {

    private final String key, displayName, armorColor, permission;
    private final List<String> description;
    private final ItemStack icon;

    private final int interval;

    private final LinkedHashMap<String, TickHandler> lines;
    private final HashMap<String, double[][]> cache;

    private final Set<Pair<String, Double>> variables;

    private final boolean enableCaching;

    public Effect(String key, String displayName, List<String> description, ItemStack icon, String armorColor, String permission, List<String> variables, int interval, boolean enableCaching, LinkedHashMap<String, Pair<Integer, List<String>>> tickHandlers) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.armorColor = armorColor;
        this.permission = permission;
        this.interval = interval;
        this.enableCaching = enableCaching;

        this.variables = new HashSet<>();
        this.lines = new LinkedHashMap<>();
        this.cache = new HashMap<>();

        for (var variable : variables)
            if (checkVariable(variable))
                addVariable(variable);
            else
                TreasurePlugin.logger().warning(variable + " is pre-defined variable.");

        for (var entry : tickHandlers.entrySet()) {
            var tickHandlerKey = entry.getKey();
            var pair = entry.getValue();
            lines.put(tickHandlerKey, new TickHandler(tickHandlerKey, pair.getKey(), readScripts(tickHandlerKey, pair.getValue())));
        }

        addVariable("i");

        if (enableCaching)
            preTick();
    }

    public boolean canUse(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public void initialize(Player player, EffectData data, boolean debugModeEnabled) {
        if (debugModeEnabled)
            TreasurePlugin.logger().info(getPrefix() + "Initializing effect for player: " + player.getName());
        for (var pair : variables)
            data.getVariables().add(new Pair<>(pair.getKey(), pair.getValue()));
        data.setTickHandlers(new LinkedHashMap<>(lines));
    }

    public void preTick() {
        var variables = new HashSet<>(this.variables);
        var timesPairOptional = variables.stream().filter(pair -> pair.getKey().equalsIgnoreCase("i")).findFirst();
        Pair<String, Double> timesPair;
        if (timesPairOptional.isPresent())
            timesPair = timesPairOptional.get();
        else {
            TreasurePlugin.logger().warning("Couldn't pre-tick effect: " + key);
            return;
        }
        int index = 0;
        for (var entry : lines.entrySet()) {
            var tickHandler = entry.getValue();
            for (var script : tickHandler.lines) {
                if (script instanceof Variable) {
                    script.setIndex(index);
                } else if (script instanceof ConditionalScript conditionalScript) {
                    List<ConditionalScript> check = new ArrayList<>();
                    check.add(conditionalScript);
                    while (!check.isEmpty()) {
                        var latest = check.remove(0);
                        if (latest.getFirstExpression() instanceof Variable variable) {
                            variable.setIndex(index);
                            index += 1;
                        } else if (latest.getFirstExpression() instanceof ConditionalScript cs)
                            check.add(cs);

                        if (latest.getSecondExpression() instanceof Variable variable) {
                            variable.setIndex(index);
                            index += 1;
                        } else if (latest.getSecondExpression() instanceof ConditionalScript cs)
                            check.add(cs);
                    }
                } else {
                    continue;
                }
                index++;
            }
            cache.put(entry.getKey(), new double[tickHandler.times][index + 1]);
        }

        for (var entry : lines.entrySet()) {
            var tickHandler = entry.getValue();
            for (int i = 0; i < tickHandler.times; i++) {
                timesPair.setValue((double) i);
                for (Script script : tickHandler.lines) {
                    if (script instanceof Variable variable) {
                        cache.get(entry.getKey())[i][variable.getIndex()] = variable.preTick(variables);
                    }
                }
            }
        }
    }

    public void doTick(Player player, EffectData data) {
        if (!TimeKeeper.isElapsed(interval))
            return;
        TickHandler last = null;
        try {
            var timesPair = data.getVariable(player, "i");
            for (var entry : data.getTickHandlers().entrySet()) {
                var tickHandler = entry.getValue();
                last = tickHandler;
                for (int i = 0; i < tickHandler.times; i++) {
                    timesPair.setValue((double) i);
                    for (Script script : tickHandler.lines)
                        if (!script.doTick(player, data, i))
                            break;
                }
            }
        } catch (Exception e) {
            TreasurePlugin.logger().log(Level.WARNING, (last != null ? "Tick Handler: " + last.name : "Unexpected error.") + " Effect=" + key, e);
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

    public boolean checkVariable(String var) {
        return switch (var) {
            case "i", "pi", "PI",
                    "tick", "TICK",
                    "random", "RANDOM",
                    "currentTimeMillis", "CTM",
                    "lastBoostMillis", "LBM",
                    "playerYaw", "playerPitch", "playerX", "playerY", "playerZ" -> false;
            default -> true;
        };
    }

    public List<Script> readScripts(String tickHandlerKey, List<String> lines) {
        List<Script> scripts = new ArrayList<>();
        var effectManager = TreasurePlugin.getInstance().getEffectManager();
        var logger = TreasurePlugin.logger();
        for (var line : lines) {
            try {
                var script = effectManager.readLine(this, line);
                if (script != null) {
                    script.setTickHandlerKey(tickHandlerKey);
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
package net.treasure.effect.data;

import lombok.Getter;
import lombok.Setter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.script.Script;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class EffectData {

    private Effect currentEffect;

    @Setter
    private boolean enabled = false, effectsEnabled = true, debugModeEnabled;

    private final Set<Pair<String, Double>> variables;

    @Setter
    private List<Script> lines, postLines;

    @Setter
    private long startedGliding;

    public EffectData(Set<Pair<String, Double>> variables) {
        this.variables = variables;
    }

    public EffectData() {
        this.variables = new HashSet<>();
        this.lines = new ArrayList<>();
        this.postLines = new ArrayList<>();
    }

    public void setCurrentEffect(Player player, Effect currentEffect) {
        this.currentEffect = currentEffect;
        this.variables.clear();
        this.lines.clear();
        this.postLines.clear();
        this.debugModeEnabled = player.isOp() && TreasurePlugin.getInstance().isDebugModeEnabled();
        if (this.currentEffect != null)
            this.currentEffect.initialize(player, this);
    }

    public Pair<String, Double> getVariable(Player player, String variable) {
        if (variable == null)
            return null;
        for (var pair : variables)
            if (pair.getKey().equals(variable))
                return pair;
        if (player == null)
            return null;
        var location = player.getLocation();
        Double value = switch (variable) {
            case "playerYaw" -> (double) location.getYaw();
            case "playerPitch" -> (double) location.getPitch();
            case "playerX" -> location.getX();
            case "playerY" -> location.getY();
            case "playerZ" -> location.getZ();
            default -> null;
        };
        return value == null ? null : new Pair<>(variable, value);
    }

    public String replaceVariables(Player player, String line) {
        StringBuilder builder = new StringBuilder();

        var array = line.toCharArray();
        int startPos = -1;
        StringBuilder variable = new StringBuilder();
        char cast = ' ';
        char last = ' ';
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];
            switch (c) {
                case '{' -> {
                    if (startPos != -1) {
                        return null;
                    }
                    startPos = pos;
                }
                case '}' -> {
                    if (startPos == -1) {
                        return null;
                    }
                    var result = variable.toString();
                    var p = getVariable(player, result);
                    double value;
                    if (p == null) {
                        Double preset = switch (result) {
                            case "TICK" -> (double) TimeKeeper.getTimeElapsed();
                            case "PI" -> Math.PI;
                            case "RANDOM" -> Math.random();
                            default -> null;
                        };
                        if (preset == null) break;
                        value = preset;
                    } else
                        value = p.getValue();
                    switch (cast) {
                        case ' ', 'd' -> builder.append(value);
                        case 'i' -> builder.append((int) value);
                    }
                    cast = ' ';

                    startPos = -1;
                    variable = new StringBuilder();
                }
                case ':' -> {
                    if (startPos != -1)
                        cast = last;
                    else
                        builder.append(c);
                }
                default -> {
                    if (startPos != -1) {
                        if (pos + 1 < array.length && array[pos + 1] == ':')
                            break;
                        variable.append(c);
                    } else
                        builder.append(c);
                }
            }
            last = c;
        }
        return builder.toString();
    }
}
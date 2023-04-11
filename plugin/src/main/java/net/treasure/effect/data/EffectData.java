package net.treasure.effect.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.color.ColorScheme;
import net.treasure.common.Permissions;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.effect.TickHandler;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class EffectData {

    private final Player player;

    @Setter
    private boolean enabled = false, effectsEnabled = true, notificationsEnabled, debugModeEnabled;

    @Setter
    private Map<String, ColorScheme> colorPreferences = new HashMap<>();

    // Fields for current effect
    private Effect currentEffect;
    private final Set<Pair<String, Double>> variables;
    @Setter
    private LinkedHashMap<String, TickHandler> tickHandlers;

    @Setter
    private long lastBoostMillis;

    public EffectData(Player player) {
        this.player = player;
        this.variables = new HashSet<>();
        this.tickHandlers = new LinkedHashMap<>();
    }

    public void setCurrentEffect(Effect currentEffect) {
        var debugModeEnabled = TreasurePlugin.getInstance().isDebugModeEnabled();
        this.currentEffect = currentEffect;
        this.variables.clear();
        this.tickHandlers.clear();
        this.debugModeEnabled = player.hasPermission(Permissions.ADMIN) && debugModeEnabled;

        if (currentEffect != null) {
            if (currentEffect.getColorGroup() != null && getColorPreference(currentEffect) == null)
                setColorPreference(currentEffect, currentEffect.getColorGroup().getAvailableOptions().get(0).colorScheme());
            currentEffect.initialize(player, this, debugModeEnabled);
        } else if (debugModeEnabled)
            TreasurePlugin.logger().info("Reset " + player.getName() + "'s effect data");
    }

    public void setColorPreference(Effect effect, ColorScheme scheme) {
        colorPreferences.put(effect.getKey(), scheme);
    }

    public ColorScheme getColorPreference(Effect effect) {
        return colorPreferences.get(effect.getKey());
    }

    public Pair<String, Double> getVariable(String variable) {
        if (variable == null)
            return null;
        for (var pair : variables)
            if (pair.getKey().equals(variable))
                return pair;
        if (player == null)
            return null;
        var value = switch (variable) {
            case "playerYaw" -> (double) player.getLocation().getYaw();
            case "playerPitch" -> (double) player.getLocation().getPitch();
            case "playerX" -> player.getLocation().getX();
            case "playerY" -> player.getLocation().getY();
            case "playerZ" -> player.getLocation().getZ();
            case "velocityX" -> player.getVelocity().getX();
            case "velocityY" -> player.getVelocity().getY();
            case "velocityZ" -> player.getVelocity().getZ();
            case "velocityLength" -> player.getVelocity().lengthSquared();
            case "lastBoostMillis", "LBM" -> (double) lastBoostMillis;
            default -> null;
        };
        return value == null ? null : new Pair<>(variable, value);
    }

    public String replaceVariables(String line) {
        StringBuilder builder = new StringBuilder();

        var array = line.toCharArray();
        int startPos = -1;
        StringBuilder variable = new StringBuilder();
        StringBuilder format = new StringBuilder();
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];
            switch (c) {
                case '{' -> {
                    if (startPos != -1) return null;
                    startPos = pos;
                }
                case '}' -> {
                    if (startPos == -1) return null;

                    var result = variable.toString();
                    var p = getVariable(result);
                    double value;
                    if (p == null) {
                        Double preset = switch (result) {
                            case "TICK" -> (double) TimeKeeper.getTimeElapsed();
                            case "PI" -> Math.PI;
                            case "RANDOM" -> Math.random();
                            case "currentTimeMillis", "CTM" -> (double) System.currentTimeMillis();
                            default -> null;
                        };
                        if (preset == null) break;
                        value = preset;
                    } else
                        value = p.getValue();

                    if (!format.isEmpty())
                        builder.append(String.format(format.toString(), value));
                    else
                        builder.append(value);

                    startPos = -1;
                    variable = new StringBuilder();
                    format = new StringBuilder();
                }
                case ':' -> {
                    if (startPos != -1) {
                        format = variable;
                        variable = new StringBuilder();
                    } else
                        builder.append(c);
                }
                default -> {
                    if (startPos != -1)
                        variable.append(c);
                    else
                        builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    public boolean canSeeEffects(Player player) {
        return effectsEnabled && (!EffectManager.EFFECTS_VISIBILITY_PERMISSION || player.hasPermission(Permissions.CAN_SEE_EFFECTS));
    }
}
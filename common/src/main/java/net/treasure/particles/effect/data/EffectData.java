package net.treasure.particles.effect.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.particles.color.scheme.ColorScheme;
import net.treasure.particles.constants.Keys;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.effect.mix.MixData;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.util.TimeKeeper;
import net.treasure.particles.util.tuples.Triplet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@RequiredArgsConstructor
public class EffectData {

    public final Player player;

    private boolean effectsEnabled = true, notificationsEnabled;

    private Map<String, ColorScheme> colorPreferences = new HashMap<>();

    // Fields for current effect
    private Effect currentEffect;
    private List<Triplet<String, Double, String>> variables;
    private List<TickHandler> tickHandlers;

    // Handler Event
    private HandlerEvent currentEvent;

    private Vector lastVector;
    private int notMovingInterval;
    private boolean moving;

    // Last time elytra boosted with firework
    private long lastBoostMillis;

    // Target entity
    private Entity targetEntity;

    // Effect Mix Data
    private List<MixData> mixData = new ArrayList<>();

    public EffectData(List<Triplet<String, Double, String>> variables) {
        this.player = null;
        this.variables = variables;
    }

    public boolean isEnabled() {
        return currentEffect != null;
    }

    public void setCurrentEffect(Effect currentEffect) {
        if (player == null) return;

        this.resetEvent();

        this.currentEffect = currentEffect;

        if (currentEffect == null) {
            this.variables = null;
            this.tickHandlers = null;

            return;
        }

        if (currentEffect.getColorGroup() != null && getColorPreference(currentEffect) == null)
            setColorPreference(currentEffect, currentEffect.getColorGroup().getAvailableOptions().get(0).colorScheme());
        currentEffect.initialize(this);
    }

    public void setColorPreference(Effect effect, ColorScheme scheme) {
        colorPreferences.put(effect.getKey(), scheme);
    }

    public ColorScheme getColorPreference(Effect effect) {
        return colorPreferences.get(effect.getKey());
    }

    public ColorScheme getColorPreference() {
        return currentEffect == null ? null : colorPreferences.get(currentEffect.getKey());
    }

    public Triplet<String, Double, String> getVariable(Effect effect, String variable) {
        if (variable == null)
            return null;
        for (var triplet : variables)
            if (triplet.x().equals(variable) && (triplet.z() == null || triplet.z().equals(effect.getKey())))
                return triplet;
        var value = switch (variable) {
            case "isMoving" -> moving ? 1D : 0D;
            case "isStanding" -> !moving ? 1D : 0D;
            case "playerYaw" -> player == null ? 0 : (double) player.getLocation().getYaw();
            case "playerPitch" -> player == null ? 0 : (double) player.getLocation().getPitch();
            case "playerX" -> player == null ? 0 : player.getLocation().getX();
            case "playerY" -> player == null ? 0 : player.getLocation().getY();
            case "playerZ" -> player == null ? 0 : player.getLocation().getZ();
            case "velocityX" -> player == null ? 0 : player.getVelocity().getX();
            case "velocityY" -> player == null ? 0 : player.getVelocity().getY();
            case "velocityZ" -> player == null ? 0 : player.getVelocity().getZ();
            case "velocityLength" -> player == null ? 0 : player.getVelocity().lengthSquared();
            case "currentTimeMillis", "CTM" -> (double) System.currentTimeMillis();
            case "lastBoostMillis", "LBM" -> (double) lastBoostMillis;
            case "RANDOM" -> Math.random();
            case "TICK" -> (double) TimeKeeper.getTimeElapsed();
            case "PI" -> Math.PI;
            default -> null;
        };
        return value == null ? null : new Triplet<>(variable, value, null);
    }

    public String replaceVariables(Effect effect, String line) {
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
                    var p = getVariable(effect, result);
                    if (p == null) break;
                    var value = p.y();

                    if (!format.isEmpty())
                        builder.append(new DecimalFormat(format.toString(), new DecimalFormatSymbols(Locale.ENGLISH)).format(value));
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

    // Mix Data
    public void resetMixDataCache() {
        mixData.forEach(MixData::resetCache);
    }

    public boolean hasMixData(String name) {
        return mixData.stream().anyMatch(data -> data.name().equals(name));
    }

    // Moving
    public void increaseInterval() {
        this.notMovingInterval += 5;
        if (this.notMovingInterval > 20)
            this.moving = false;
    }

    public void resetInterval() {
        this.notMovingInterval = 0;
        this.moving = true;
    }

    // Handler Event
    public void setCurrentEvent(HandlerEvent currentEvent) {
        this.currentEvent = currentEvent;
        this.targetEntity = null;
    }

    public void resetEvent() {
        this.currentEvent = null;
        this.targetEntity = null;
    }

    // Permissions

    public boolean canSeeEffects() {
        return player != null && effectsEnabled && (!Permissions.EFFECTS_VISIBILITY_PERMISSION || player.hasPermission(Permissions.CAN_SEE_EFFECTS));
    }

    public int getMixLimit() {
        return getMax(Keys.NAMESPACE + ".mix_limit.");
    }

    public boolean canCreateAnotherMix() {
        if (!Permissions.MIX_LIMIT_ENABLED) return true;
        var max = getMixLimit();
        return max == -1 || max > mixData.size();
    }

    public int getMixEffectLimit() {
        return getMax(Keys.NAMESPACE + ".mix_effect_limit.");
    }

    private int getMax(String permission) {
        if (player == null) return 0;
        if (player.isOp())
            return -1;

        final AtomicInteger max = new AtomicInteger();

        player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith(permission)).map(value -> value.replace(permission, "")).forEach(value -> {
            if (value.equalsIgnoreCase("*")) {
                max.set(-1);
                return;
            }

            if (max.get() == -1) return;

            try {
                int amount = Integer.parseInt(value);

                if (amount > max.get())
                    max.set(amount);
            } catch (NumberFormatException ignored) {
            }
        });

        return max.get();
    }
}
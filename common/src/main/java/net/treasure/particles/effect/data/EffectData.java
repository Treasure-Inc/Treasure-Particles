package net.treasure.particles.effect.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.scheme.ColorScheme;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.util.TimeKeeper;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.tuples.Triplet;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Setter
@Getter
@RequiredArgsConstructor
public abstract class EffectData {

    private Map<String, ColorScheme> colorPreferences = new HashMap<>();

    // Fields for current effect
    protected Effect currentEffect;
    protected List<Triplet<String, Double, String>> variables;
    private List<TickHandler> tickHandlers;

    // Handler Event
    @Setter
    protected HandlerEvent currentEvent;

    public abstract String getId();

    public abstract Location getLocation();

    public boolean isEnabled() {
        return currentEffect != null;
    }

    public boolean setCurrentEffect(Effect currentEffect) {
        this.resetEvent();

        this.currentEffect = currentEffect;

        if (currentEffect == null) {
            this.variables = null;
            this.tickHandlers = null;
            return true;
        }

        if (currentEffect.getColorGroup() != null && getColorPreference(currentEffect) == null)
            setColorPreference(currentEffect, currentEffect.getColorGroup().getAvailableOptions().get(0).colorScheme());
        currentEffect.initialize(this);

        TreasureParticles.getEffectManager().getData().put(getId(), this);
        return true;
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

    public abstract Double getVariable(String variable);

    public Triplet<String, Double, String> getVariable(Effect effect, String variable) {
        if (variable == null) return null;

        for (var triplet : variables)
            if (triplet.x().equals(variable) && (triplet.z() == null || triplet.z().equals(effect.getKey())))
                return triplet;

        Double value;

        var var = getVariable(variable);
        if (var != null)
            value = var;
        else
            value = switch (variable) {
                case "locationYaw" -> getLocation() == null ? 0 : (double) getLocation().getYaw();
                case "locationPitch" -> getLocation() == null ? 0 : (double) getLocation().getPitch();
                case "locationX" -> getLocation() == null ? 0 : getLocation().getX();
                case "locationY" -> getLocation() == null ? 0 : getLocation().getY();
                case "locationZ" -> getLocation() == null ? 0 : getLocation().getZ();
                case "currentTimeMillis", "CTM" -> (double) System.currentTimeMillis();
                case "RANDOM" -> Math.random();
                case "RANDOM-" -> ThreadLocalRandom.current().nextDouble(-1, 1);
                case "TICK" -> (double) TimeKeeper.getTimeElapsed();
                case "PI" -> Math.PI;
                case "2PI" -> MathUtils.PI2;
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

    // Handler Event
    public void resetEvent() {
        this.currentEvent = null;
    }

    // Permissions
    public boolean canSeeEffects() {
        return true;
    }
}
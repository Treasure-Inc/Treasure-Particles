package net.treasure.effect.script.parkour;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.particle.style.CircleParticle;
import net.treasure.util.TimeKeeper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class Parkour extends Script {

    private static final Vector ZERO = new Vector(0, 0, 0);

    IntArgument interval;
    IntArgument duration;
    CircleParticle style;
    ColorData standby, success, fail;

    Script whenSpawned, whenSucceeded, whenFailed;
    boolean immediate;

    public Parkour(IntArgument interval, IntArgument duration, CircleParticle style, ColorData standby, ColorData success, ColorData fail, Script whenSpawned, Script whenSucceeded, Script whenFailed, boolean immediate) {
        this.interval = interval;
        this.duration = duration;
        this.style = style;
        this.standby = standby;
        this.success = success;
        this.fail = fail;
        this.whenSpawned = whenSpawned;
        this.whenSucceeded = whenSucceeded;
        this.whenFailed = whenFailed;
        this.immediate = immediate;
    }

    boolean completed = false;
    boolean scriptExecuted = false;
    ParticleBuilder builder;
    Vector lastDirection;
    float lastPitch, lastYaw;
    Location lastLocation;
    long lastSpawned = -5;

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        if (lastSpawned == -5) {
            var interval = this.interval.get(player, data);
            if (!TimeKeeper.isElapsed(interval)) return TickResult.NORMAL;
            lastSpawned = System.currentTimeMillis();
            style.colorData(standby);
            var triplet = style.sendParticles(player, data, event, p -> p.equals(player));
            if (triplet == null) return TickResult.NORMAL;
            builder = triplet.a();
            lastLocation = triplet.b();
            lastDirection = triplet.c();
            lastYaw = lastLocation.getYaw();
            lastPitch = lastLocation.getPitch();
            if (whenSpawned != null)
                whenSpawned.tick(player, data, event, times);
            return TickResult.NORMAL;
        }

        if (lastLocation == null) return TickResult.NORMAL;

        var duration = this.duration.get(player, data);
        var timeLeft = duration * 1000L - (System.currentTimeMillis() - lastSpawned);

        if (timeLeft < 0) {
            if (!completed && timeLeft > -5000L) {
                if (!scriptExecuted && whenFailed != null) {
                    scriptExecuted = true;
                    whenFailed.tick(player, data, event, times);
                }
                style.colorData(fail);
                style.sendParticles(player, data, builder, lastLocation, lastDirection, lastPitch, lastYaw, ZERO);
                return TickResult.NORMAL;
            }
            reset();
            return TickResult.NORMAL;
        }

        var radius = style.radius().get(player, data);
        if (!completed && isOnCircle(player, lastLocation, radius)) {
            style.colorData(success);
            if (!completed && whenSucceeded != null) {
                whenSucceeded.tick(player, data, event, times);
                if (immediate) {
                    style.sendParticles(player, data, builder, lastLocation, lastDirection, lastPitch, lastYaw, ZERO);
                    reset();
                    return TickResult.NORMAL;
                }
            }
            completed = true;
        }

        style.sendParticles(player, data, builder, lastLocation, lastDirection, lastPitch, lastYaw, ZERO);
        return TickResult.NORMAL;
    }

    public boolean isOnCircle(Player player, Location location, double radius) {
        var dot = Math.abs(lastDirection.dot(player.getEyeLocation().getDirection()));
        return player.getLocation().distanceSquared(location) < radius * radius && dot >= 0.5D;
    }

    public void reset() {
        completed = false;
        scriptExecuted = false;
        lastLocation = null;
        lastSpawned = -5;
    }

    @Override
    public Script clone() {
        return new Parkour(interval, duration, style.clone(), standby, success, fail, whenSpawned, whenSucceeded.cloneScript(), whenFailed.cloneScript(), immediate);
    }
}
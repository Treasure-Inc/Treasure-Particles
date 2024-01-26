package net.treasure.particles.effect.script.parkour;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.particle.ParticleContext;
import net.treasure.particles.effect.script.particle.style.circle.CircleParticle;
import net.treasure.particles.util.TimeKeeper;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class Parkour extends Script {

    private static final Vector ZERO = new Vector(0, 0, 0);

    private IntArgument interval;
    private IntArgument duration;
    private CircleParticle style;
    private ColorData standby, success, fail;
    private Script whenSpawned, whenSucceeded, whenFailed;
    private boolean immediate;

    private boolean completed = false;
    private boolean scriptExecuted = false;
    private ParticleContext context;
    private long lastSpawned = -5;

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

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        if (lastSpawned == -5) {
            var interval = this.interval.get(player, this, data);
            if (!TimeKeeper.isElapsed(interval)) return TickResult.NORMAL;
            lastSpawned = System.currentTimeMillis();
            style.colorData(standby);
            context = style.sendParticles(player, data, event, p -> p.equals(player));
            if (context == null) return TickResult.NORMAL;
            if (whenSpawned != null)
                whenSpawned.tick(player, data, event, times);
            return TickResult.NORMAL;
        }

        if (context == null) return TickResult.NORMAL;

        var duration = this.duration.get(player, this, data);
        var timeLeft = duration * 1000L - (System.currentTimeMillis() - lastSpawned);

        if (timeLeft < 0) {
            if (!completed && timeLeft > -5000L) {
                if (!scriptExecuted && whenFailed != null) {
                    scriptExecuted = true;
                    whenFailed.tick(player, data, event, times);
                }
                style.colorData(fail);
                style.sendParticles(player, data, context);
                return TickResult.NORMAL;
            }
            reset();
            return TickResult.NORMAL;
        }

        var radius = style.radius().get(player, this, data);
        if (!completed && isOnCircle(player, radius)) {
            style.colorData(success);
            if (!completed && whenSucceeded != null) {
                whenSucceeded.tick(player, data, event, times);
                if (immediate) {
                    style.sendParticles(player, data, context);
                    reset();
                    return TickResult.NORMAL;
                }
            }
            completed = true;
        }

        style.sendParticles(player, data, context);
        return TickResult.NORMAL;
    }

    public boolean isOnCircle(Player player, double radius) {
        var dot = Math.abs(context.direction.dot(player.getEyeLocation().getDirection()));
        return player.getLocation().distanceSquared(context.origin) < radius * radius && dot >= 0.5D;
    }

    public void reset() {
        completed = false;
        scriptExecuted = false;
        context = null;
        lastSpawned = -5;
    }

    @Override
    public Script clone() {
        return new Parkour(interval, duration, style.clone(), standby, success, fail, whenSpawned, whenSucceeded.cloneScript(), whenFailed.cloneScript(), immediate);
    }
}
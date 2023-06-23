package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.color.data.duo.DuoImpl;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.math.MathUtils;
import net.treasure.util.particles.Particles;
import net.treasure.util.tuples.Triplet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class CircleParticle extends ParticleSpawner {

    IntArgument particles = new IntArgument(32);
    RangeArgument radius = new RangeArgument(1f);
    boolean tickData = false;

    public CircleParticle(ParticleEffect particle, ParticleOrigin origin,
                          IntArgument particles, RangeArgument radius, boolean tickData,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
        this.particles = particles;
        this.radius = radius;
        this.tickData = tickData;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        sendParticles(player, data, event, null);
        return TickResult.NORMAL;
    }

    @Nullable
    public Triplet<ParticleBuilder, Location, Vector> sendParticles(Player player, EffectData data, HandlerEvent event, Predicate<Player> viewers) {
        var context = tick(player, data, event);
        if (context == null) return null;
        var origin = context.origin();
        var builder = context.builder();
        var vector = this.position != null ? position.get(player, data) : new Vector(0, 0, 0);

        var direction = player.getLocation().getDirection();
        float pitch = player.getEyeLocation().getPitch(), yaw = player.getEyeLocation().getYaw();

        var clone = origin.clone();
        clone.setPitch(pitch);
        clone.setYaw(yaw);

        if (viewers != null)
            builder.viewers(viewers);

        sendParticles(player, data, builder, origin, direction, pitch, yaw, vector);
        return new Triplet<>(builder, clone.add(vector), direction);
    }

    public void sendParticles(Player player, EffectData data, ParticleBuilder builder, Location origin, Vector direction, float pitch, float yaw, Vector vector) {
        var particles = this.particles.get(player, data);
        var radius = this.radius.get(player, data);

        var particleData = particleData(player, data);

        List<ParticleBuilder> builders = new ArrayList<>();
        for (int i = 0; i < particles; i++) {
            var r = 2 * Math.PI * i / particles;
            var x = MathUtils.cos(r) * radius;
            var y = MathUtils.sin(r) * radius;

            var location = rotate(origin.clone(), direction.clone(), pitch, yaw, new Vector(x, y, 0).add(vector));

            var copy = builder.copy()
                    .location(location)
                    .data(particleData);

            if (particle == ParticleEffect.NOTE && colorData != null && colorData.isNote())
                copy.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());
            else if (particle.hasProperty(ParticleEffect.Property.OFFSET_COLOR) && colorData != null)
                copy.offsetColor(colorData.next(data));

            builders.add(copy);

            if (tickData)
                particleData = particleData(player, data);
        }

        Particles.send(builders);
    }

    public Object particleData(Player player, EffectData data) {
        if (particleData != null) return particleData();

        if (colorData == null || particle.hasProperty(ParticleEffect.Property.OFFSET_COLOR)) {
            particleData = Particles.NMS.getParticleParam(particle);
            return particleData;
        }

        if (particle.hasProperty(ParticleEffect.Property.DUST)) {
            var size = this.size != null ? this.size.get(player, data) : 1;
            if (particle.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    return Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size);
                } else
                    return Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size);
            else
                return Particles.NMS.getDustData(colorData.next(data), size);
        }
        return null;
    }

    @Override
    public CircleParticle clone() {
        return new CircleParticle(particle, origin, particles, radius, tickData, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
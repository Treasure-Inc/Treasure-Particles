package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
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
    boolean vertical = true;

    public CircleParticle(ParticleEffect particle, ParticleOrigin origin,
                          IntArgument particles, RangeArgument radius, boolean tickData, boolean vertical,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
        this.particles = particles;
        this.radius = radius;
        this.tickData = tickData;
        this.vertical = vertical;
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

        if (viewers != null)
            builder.viewers(viewers);

        sendParticles(player, data, builder, origin, origin.getDirection(), origin.getPitch(), origin.getYaw(), vector);
        return new Triplet<>(builder, origin.clone().add(vector), origin.getDirection());
    }

    public void sendParticles(Player player, EffectData data, ParticleBuilder builder, Location origin, Vector direction, float pitch, float yaw, Vector vector) {
        var particles = this.particles.get(player, data);
        var radius = this.radius.get(player, data);

        var particleData = particleData(player, data);

        List<ParticleBuilder> builders = new ArrayList<>();
        for (int i = 0; i < particles; i++) {
            var r = MathUtils.PI2 * i / particles;
            var x = MathUtils.cos(r) * radius;
            var y = MathUtils.sin(r) * radius;

            var location = rotate(origin.clone(), direction.clone(), pitch, yaw, (vertical ? new Vector(x, y, 0) : new Vector(y, 0, x)).add(vector));

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

    @Override
    public CircleParticle clone() {
        return new CircleParticle(particle, origin, particles, radius, tickData, vertical, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
package net.treasure.particles.effect.script.particle.style.sphere;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.effect.script.particle.config.ParticleOrigin;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class SphereParticle extends ParticleSpawner {

    private int particles = 50;
    private RangeArgument radius = new RangeArgument(1f);
    private boolean tickData = false;
    private boolean fullSphere = true, reverse = false;

    public SphereParticle(ParticleEffect particle, ParticleOrigin origin,
                          int particles, RangeArgument radius,
                          boolean tickData, boolean fullSphere, boolean reverse,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size,
                          boolean directionalX, boolean directionalY, boolean longDistance) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance);
        this.particles = particles;
        this.radius = radius;
        this.tickData = tickData;
        this.fullSphere = fullSphere;
        this.reverse = reverse;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var context = tick(data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        var radius = this.radius.get(this, data);

        updateParticleData(builder, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        for (var vector : createSphere()) {
            builders.add(builder.copy().location(location(context, vector.clone().multiply(radius))));

            if (tickData)
                updateParticleData(builder, data);
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    private Vector[] createSphere() {
        var vectors = new Vector[particles];
        for (int i = 0; i < particles; i++) {
            var vector = MathUtils.getRandomVector();
            if (!fullSphere) {
                if (reverse) vector.setY(Math.abs(vector.getY()) * -1);
                else vector.setY(Math.abs(vector.getY()));
            }
            vectors[i] = vector;
        }
        return vectors;
    }

    @Override
    public SphereParticle clone() {
        return new SphereParticle(
                particle, origin,
                particles, radius,
                tickData, fullSphere, reverse,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance
        );
    }
}
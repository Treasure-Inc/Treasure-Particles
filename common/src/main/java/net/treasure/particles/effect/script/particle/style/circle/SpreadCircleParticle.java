package net.treasure.particles.effect.script.particle.style.circle;

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
public class SpreadCircleParticle extends CircleParticle {

    RangeArgument spread = null;

    public SpreadCircleParticle(ParticleEffect particle, ParticleOrigin origin,
                                RangeArgument spread,
                                IntArgument particles, RangeArgument radius, boolean tickData, boolean vertical,
                                VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                                ColorData colorData, Object particleData,
                                IntArgument amount, RangeArgument speed, RangeArgument size,
                                boolean directionalX, boolean directionalY, boolean longDistance) {
        super(particle, origin, particles, radius, tickData, vertical, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance);
        this.spread = spread;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var context = tick(data, event, false, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        // Circle Particle Variables
        var particles = this.particles.get(this, data);
        var radius = this.radius.get(this, data);

        updateParticleData(builder, data);

        // Spread
        var offset = this.offset != null ? this.offset.get(this, data) : new Vector(0, 0, 0);
        var spread = this.spread != null ? this.spread.get(this, data) : null;

        List<ParticleBuilder> builders = new ArrayList<>();
        var p = MathUtils.PI2 / particles;
        for (int i = 0; i < particles; i++) {
            var r = p * i;
            var x = MathUtils.cos(r) * radius;
            var y = MathUtils.sin(r) * radius;

            var location = location(context, vertical ? new Vector(x, y, 0) : new Vector(x, 0, y));

            var copy = builder.copy().location(location);

            // Spread
            var tempOffset = offset.clone();
            if (spread != null) {
                var angle = MathUtils.atan2(y, x);
                tempOffset.add(vertical ? new Vector(MathUtils.cos(angle) * spread, MathUtils.sin(angle) * spread, 0) : new Vector(MathUtils.cos(angle) * spread, 0, MathUtils.sin(angle) * spread));
            }
            copy.offset(rotate(context, tempOffset));

            builders.add(copy);

            if (tickData)
                updateParticleData(builder, data);
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    @Override
    public SpreadCircleParticle clone() {
        return new SpreadCircleParticle(
                particle, origin,
                spread, particles, radius, tickData, vertical,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance
        );
    }
}
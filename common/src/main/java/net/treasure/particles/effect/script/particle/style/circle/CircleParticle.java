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
import net.treasure.particles.effect.script.particle.ParticleContext;
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class CircleParticle extends ParticleSpawner {

    protected IntArgument particles = new IntArgument(32);
    protected RangeArgument radius = new RangeArgument(1f);
    protected boolean tickData = false;
    protected boolean vertical = true;

    public CircleParticle(ParticleEffect particle, LocationOrigin origin,
                          IntArgument particles, RangeArgument radius, boolean tickData, boolean vertical,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size,
                          boolean directionalX, boolean directionalY, boolean longDistance,
                          boolean spawnEffectOnPlayer) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance, spawnEffectOnPlayer);
        this.particles = particles;
        this.radius = radius;
        this.tickData = tickData;
        this.vertical = vertical;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        sendParticles(data, event, null);
        return TickResult.NORMAL;
    }

    @Nullable
    public ParticleContext sendParticles(EffectData data, HandlerEvent event, Predicate<Player> viewers) {
        var context = tick(data, event, true, false);
        if (context == null) return null;

        if (viewers != null)
            context.builder.viewers(viewers);

        sendParticles(data, context);
        return context;
    }

    public void sendParticles(EffectData data, ParticleContext context) {
        var builder = context.builder;

        var particles = this.particles.get(this, data);
        var radius = this.radius.get(this, data);

        updateParticleData(builder, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        var s = MathUtils.PI2 / particles;
        for (int i = 0; i < particles; i++) {
            var r = s * i;
            builders.add(builder.copy().location(location(context, r, radius, vertical)));
            if (tickData)
                updateParticleData(builder, data);
        }

        Particles.send(builders);
    }

    @Override
    public CircleParticle clone() {
        return new CircleParticle(
                particle, origin,
                particles, radius, tickData, vertical,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance,
                spawnEffectOnPlayer
        );
    }
}
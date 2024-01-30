package net.treasure.particles.effect.script.particle.style.spiral;

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
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.Player;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class SpiralParticle extends ParticleSpawner {

    private RangeArgument radius = new RangeArgument(1f);
    private IntArgument steps = new IntArgument(120);
    private boolean vertical = false;
    private int reverse = 1;

    private double step;

    public SpiralParticle(ParticleEffect effect, ParticleOrigin origin,
                          RangeArgument radius, IntArgument steps,
                          boolean vertical, int reverse,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size,
                          boolean directionalX, boolean directionalY, boolean longDistance) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance);
        this.radius = radius;
        this.steps = steps;
        this.vertical = vertical;
        this.reverse = reverse;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        var radius = this.radius.get(player, this, data);
        var steps = this.steps.get(player, this, data);

        var r = reverse * (step / steps) * MathUtils.PI2;
        builder.location(location(context, r, radius, vertical));

        updateParticleData(builder, player, data);

        step++;

        if (step >= steps)
            step = 0;

        Particles.send(builder);
        return TickResult.NORMAL;
    }

    @Override
    public SpiralParticle clone() {
        return new SpiralParticle(
                particle, origin,
                radius, steps,
                vertical, reverse,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance
        );
    }
}

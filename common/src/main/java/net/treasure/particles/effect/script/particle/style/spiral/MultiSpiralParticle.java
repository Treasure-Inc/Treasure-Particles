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
import net.treasure.particles.effect.script.particle.ParticleOrigin;
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class MultiSpiralParticle extends ParticleSpawner {

    private RangeArgument radius = new RangeArgument(1f);
    private IntArgument spirals = new IntArgument(3);
    private IntArgument steps = new IntArgument(120);
    private boolean tickData = false;
    private boolean vertical = false;

    private double step;

    public MultiSpiralParticle(ParticleEffect effect, ParticleOrigin origin,
                               RangeArgument radius, IntArgument spirals, IntArgument steps,
                               boolean tickData, boolean vertical,
                               VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                               ColorData colorData, Object particleData,
                               IntArgument amount, RangeArgument speed, RangeArgument size,
                               boolean directional, boolean longDistance) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional, longDistance);
        this.radius = radius;
        this.spirals = spirals;
        this.steps = steps;
        this.tickData = tickData;
        this.vertical = vertical;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        var spirals = this.spirals.get(player, this, data);
        var radius = this.radius.get(player, this, data);
        var steps = this.steps.get(player, this, data);

        updateParticleData(builder, player, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        var s = (step / steps) * MathUtils.PI2;
        var dP = MathUtils.PI2 / spirals;
        for (int i = 0; i < spirals; i++) {
            var r = s + dP * i;
            builders.add(builder.copy().location(location(context, r, radius, vertical)));

            if (tickData)
                updateParticleData(builder, player, data);
        }

        step++;
        if (step >= steps)
            step = 0;

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    @Override
    public MultiSpiralParticle clone() {
        return new MultiSpiralParticle(
                particle, origin,
                radius, spirals, steps,
                tickData, vertical,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directional, longDistance
        );
    }
}
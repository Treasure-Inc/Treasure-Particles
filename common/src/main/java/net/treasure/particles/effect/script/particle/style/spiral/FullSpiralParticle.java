package net.treasure.particles.effect.script.particle.style.spiral;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.argument.type.DoubleArgument;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.config.ParticleOrigin;
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
public class FullSpiralParticle extends ParticleSpawner {

    private RangeArgument radius = new RangeArgument(1f);
    private IntArgument spirals = new IntArgument(12);
    private IntArgument steps = new IntArgument(90);
    private DoubleArgument gap = new DoubleArgument(4D);
    private boolean tickData = false;
    private boolean vertical = false;
    private int reverse = -1;

    private double stepX;

    public FullSpiralParticle(ParticleEffect effect, ParticleOrigin origin,
                              RangeArgument radius, IntArgument spirals, IntArgument steps, DoubleArgument gap,
                              boolean tickData, boolean vertical,
                              VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                              ColorData colorData, Object particleData,
                              IntArgument amount, RangeArgument speed, RangeArgument size,
                              boolean directionalX, boolean directionalY, boolean longDistance) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance);
        this.radius = radius;
        this.spirals = spirals;
        this.steps = steps;
        this.gap = gap;
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
        var gap = this.gap.get(player, this, data);

        updateParticleData(builder, player, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        for (double stepY = -60; stepY < 60; stepY += 120D / spirals) {
            var r = ((stepX + stepY) / steps) * MathUtils.PI2 * reverse;
            var location = location(context, stepY / steps * gap, r, radius, vertical);
            builders.add(builder.copy().location(location));

            if (tickData)
                updateParticleData(builder, player, data);
        }

        stepX++;

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    @Override
    public FullSpiralParticle clone() {
        return new FullSpiralParticle(
                particle, origin,
                radius, spirals, steps, gap,
                tickData, vertical,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance
        );
    }
}
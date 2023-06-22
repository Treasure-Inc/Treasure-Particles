package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleContext;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.util.Vectors;
import net.treasure.util.math.MathUtils;
import net.treasure.util.particles.Particles;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
                                IntArgument particles, RangeArgument radius, boolean tickData,
                                VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                                ColorData colorData, Object particleData,
                                IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(particle, origin, particles, radius, tickData, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
        this.spread = spread;
    }

    @Override
    public TickResult tick(Player player, EffectData data, int times) {
        var context = tick(player, data);
        var origin = context.origin();
        var builder = context.builder();
        var vector = this.position != null ? position.get(player, data) : new Vector(0, 0, 0);

        var direction = player.getLocation().getDirection();
        float pitch = player.getEyeLocation().getPitch(), yaw = player.getEyeLocation().getYaw();

        // Circle Particle Variables
        var particles = this.particles.get(player, data);
        var radius = this.radius.get(player, data);

        var particleData = particleData(player, data);

        // Spread
        var offset = this.offset != null ? this.offset.get(player, data) : null;
        var spread = this.spread != null ? this.spread.get(player, data) : null;

        List<ParticleBuilder> builders = new ArrayList<>();
        for (int i = 0; i < particles; i++) {
            if (interval != 0 && i % interval != 0) continue;
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

            // Spread
            if (offset != null) {
                var tempOffset = offset.clone();
                if (spread != null) {
                    var angle = Math.atan2(y, x);
                    tempOffset.add(new Vector(MathUtils.cos(angle) / spread, MathUtils.sin(angle) / spread, 0));
                }
                tempOffset = Vectors.rotateAroundAxisX(tempOffset, pitch);
                tempOffset = Vectors.rotateAroundAxisY(tempOffset, yaw);
                tempOffset = tempOffset.add(direction.add(tempOffset));
                copy.offset(tempOffset);
            } else if (spread != null) {
                var angle = Math.atan2(y, x);
                var tempOffset = new Vector(MathUtils.cos(angle) / spread, MathUtils.sin(angle) / spread, 0);
                tempOffset = Vectors.rotateAroundAxisX(tempOffset, pitch);
                tempOffset = Vectors.rotateAroundAxisY(tempOffset, yaw);
                tempOffset = tempOffset.add(direction.add(tempOffset));
                copy.offset(tempOffset);
            }

            builders.add(copy);

            if (tickData)
                particleData = particleData(player, data);
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    @Override
    public ParticleContext tick(Player player, EffectData data) {
        Location origin;

        origin = switch (this.origin) {
            case HEAD -> player.getEyeLocation();
            case FEET -> player.getLocation();
            case WORLD -> new Location(player.getWorld(), 0, 0, 0);
        };

        if (multiplier != null)
            origin = origin.add(player.getLocation().getDirection().multiply(multiplier.get(player, data)));

        ParticleBuilder builder = new ParticleBuilder(particle);

        if (amount != null)
            builder.amount(amount.get(player, data));

        if (speed != null)
            builder.speed(speed.get(player, data));

        var playerManager = TreasurePlugin.getInstance().getPlayerManager();
        builder.viewers(viewer -> playerManager.getEffectData(viewer).canSeeEffects());

        return new ParticleContext(builder, origin);
    }

    @Override
    public SpreadCircleParticle clone() {
        return new SpreadCircleParticle(particle, origin, spread, particles, radius, tickData, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.TreasureParticles;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleContext;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.util.math.MathUtils;
import net.treasure.util.math.Vectors;
import net.treasure.util.nms.particles.ParticleBuilder;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.util.nms.particles.Particles;
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
                                IntArgument particles, RangeArgument radius, boolean tickData, boolean vertical,
                                VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                                ColorData colorData, Object particleData,
                                IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(particle, origin, particles, radius, tickData, vertical, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
        this.spread = spread;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event);
        if (context == null) return TickResult.NORMAL;
        var origin = context.origin();
        var builder = context.builder();
        var vector = this.position != null ? position.get(player, this, data) : new Vector(0, 0, 0);

        var direction = player.getLocation().getDirection();
        float pitch = player.getEyeLocation().getPitch(), yaw = player.getEyeLocation().getYaw();

        // Circle Particle Variables
        var particles = this.particles.get(player, this, data);
        var radius = this.radius.get(player, this, data);

        var particleData = particleData(player, data);

        // Spread
        var offset = this.offset != null ? this.offset.get(player, this, data) : null;
        var spread = this.spread != null ? this.spread.get(player, this, data) : null;

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

            // Spread
            if (offset != null) {
                var tempOffset = offset.clone();
                if (spread != null) {
                    var angle = MathUtils.atan2(y, x);
                    tempOffset.add(vertical ? new Vector(MathUtils.cos(angle) * spread, MathUtils.sin(angle) * spread, 0) : new Vector(MathUtils.sin(angle) * spread, 0, MathUtils.cos(angle) * spread));
                }
                if (directional) {
                    tempOffset = Vectors.rotateAroundAxisX(tempOffset, pitch);
                    tempOffset = Vectors.rotateAroundAxisY(tempOffset, yaw);
                    tempOffset = tempOffset.add(direction.clone().add(tempOffset));
                }
                copy.offset(tempOffset);
            } else if (spread != null) {
                var angle = MathUtils.atan2(y, x);
                var tempOffset = vertical ? new Vector(MathUtils.cos(angle) * spread, MathUtils.sin(angle) * spread, 0) : new Vector(MathUtils.sin(angle) * spread, 0, MathUtils.cos(angle) * spread);
                if (directional) {
                    tempOffset = Vectors.rotateAroundAxisX(tempOffset, pitch);
                    tempOffset = Vectors.rotateAroundAxisY(tempOffset, yaw);
                    tempOffset = tempOffset.add(direction.clone().add(tempOffset));
                }
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
    public ParticleContext tick(Player player, EffectData data, HandlerEvent event) {
        var entity = switch (event) {
            case ELYTRA, STANDING, MOVING, SNEAKING, TAKE_DAMAGE -> player;
            case MOB_KILL, PLAYER_KILL, PROJECTILE, MOB_DAMAGE, PLAYER_DAMAGE -> data.getTargetEntity();
        };
        if (entity == null) return null;
        var origin = switch (this.origin) {
            case HEAD -> entity instanceof Player p ? p.getEyeLocation() : entity.getLocation();
            case FEET -> entity.getLocation();
            case WORLD -> new Location(entity.getWorld(), 0, 0, 0);
        };

        if (multiplier != null)
            origin = origin.add(player.getLocation().getDirection().multiply(multiplier.get(player, this, data)));

        ParticleBuilder builder = new ParticleBuilder(particle);

        if (amount != null)
            builder.amount(amount.get(player, this, data));

        if (speed != null)
            builder.speed(speed.get(player, this, data));

        var playerManager = TreasureParticles.getPlayerManager();
        builder.viewers(viewer -> playerManager.getEffectData(viewer).canSeeEffects());

        return new ParticleContext(builder, origin);
    }

    @Override
    public SpreadCircleParticle clone() {
        return new SpreadCircleParticle(particle, origin, spread, particles, radius, tickData, vertical, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
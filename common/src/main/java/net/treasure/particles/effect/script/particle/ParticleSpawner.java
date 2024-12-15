package net.treasure.particles.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.color.data.RandomNoteColorData;
import net.treasure.particles.color.data.duo.DuoImpl;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.math.Vectors;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.ParticleEffect.Property;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParticleSpawner extends Script {

    protected ParticleEffect particle;
    protected LocationOrigin origin;

    protected VectorArgument position;
    protected VectorArgument offset;
    protected VectorArgument multiplier;

    protected ColorData colorData;
    protected Object particleData;
    protected IntArgument amount;
    protected RangeArgument speed, size;

    protected boolean directionalX = false, directionalY = false;

    protected boolean longDistance = false;

    protected boolean spawnEffectOnPlayer;

    @Nullable
    public ParticleContext tick(EffectData data, HandlerEvent event, boolean configureOffset, boolean rotatePos) {
        Location origin = null;
        var player = data instanceof PlayerEffectData playerEffectData ? playerEffectData.player : null;
        if (player != null && player.getGameMode() == GameMode.SPECTATOR) return null;

        var entity = switch (event) {
            case STATIC -> {
                origin = data.getLocation().clone();
                yield null;
            }
            case ELYTRA, STANDING, MOVING, SNEAKING, TAKE_DAMAGE -> player;
            case MOB_KILL, PLAYER_KILL, PROJECTILE, MOB_DAMAGE, PLAYER_DAMAGE, RIDE_VEHICLE ->
                    data instanceof PlayerEffectData playerEffectData ? (spawnEffectOnPlayer ? player : playerEffectData.getTargetEntity()) : null;
        };
        if (entity == null && origin == null) return null;

        if (origin == null)
            origin = this.origin.getLocation(entity);

        var direction = origin.getDirection();

        if (multiplier != null)
            origin.add(direction.clone().multiply(multiplier.get(this, data)));

        var builder = new ParticleBuilder(particle);

        if (amount != null)
            builder.amount(amount.get(this, data));

        if (speed != null)
            builder.speed(speed.get(this, data));

        builder.longDistance(longDistance);

        // Rotations
        var pitchToRadians = Math.toRadians(origin.getPitch());
        var cosP = MathUtils.cos(pitchToRadians);
        var sinP = MathUtils.sin(pitchToRadians);

        var yawToRadians = Math.toRadians(-origin.getYaw());
        var cosY = MathUtils.cos(yawToRadians);
        var sinY = MathUtils.sin(yawToRadians);

        // Position
        var pos = this.position != null ? this.position.get(this, data) : new Vector(0, 0, 0);
        if (rotatePos)
            origin.add(rotate(direction, pos, cosP, sinP, cosY, sinY));
        else
            origin.add(pos);

        // Offset
        if (configureOffset) {
            var offset = this.offset != null ? this.offset.get(this, data) : null;
            if (offset != null) offset.add(rotate(direction, offset, cosP, sinP, cosY, sinY));
            if (offset != null)
                builder.offset(offset);
        }

        // Viewers
        builder.viewers(TreasureParticles.getPlayerManager().defaultFilter(player));

        return new ParticleContext(builder, origin, direction, cosP, sinP, cosY, sinY);
    }

    public void updateParticleData(ParticleBuilder builder, EffectData data) {
        if (particleData != null) {
            builder.data(particleData);
            return;
        }

        if (colorData == null) {
            particleData = Particles.NMS.getParticleParam(particle);
            builder.data(particleData);
            return;
        }

        if (particle.hasProperty(Property.DUST)) {
            var size = this.size != null ? this.size.get(this, data) : 1;
            if (particle == ParticleEffect.DUST_COLOR_TRANSITION)
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    builder.data(Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size));
                } else
                    builder.data(Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size));
            else
                builder.data(Particles.NMS.getDustData(colorData.next(data), size));
        } else if (particle.hasProperty(Property.OFFSET_COLOR)) {
            builder.data(Particles.NMS.getParticleParam(particle));
            if (particle == ParticleEffect.NOTE && colorData.isNote()) {
                builder.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());
            } else
                builder.offsetColor(colorData.next(data));
        }
    }

    public Location location(ParticleContext context, Vector vector) {
        return context.origin.clone().add(rotate(context, vector));
    }

    public Location location(ParticleContext context, double z, double r, float radius, boolean vertical) {
        var x = MathUtils.cos(r) * radius;
        var y = MathUtils.sin(r) * radius;
        return location(context, vertical ? new Vector(x, y, z) : new Vector(x, z, y));
    }

    public Location location(ParticleContext context, double r, float radius, boolean vertical) {
        var x = MathUtils.cos(r) * radius;
        var y = MathUtils.sin(r) * radius;
        return location(context, vertical ? new Vector(x, y, 0) : new Vector(x, 0, y));
    }

    public Vector rotate(ParticleContext context, Vector vector) {
        return rotate(context.direction, vector, context.cosP, context.sinP, context.cosY, context.sinY);
    }

    public Vector rotate(Vector direction, Vector vector, double cosP, double sinP, double cosY, double sinY) {
        if (directionalX)
            Vectors.rotateAroundAxisX(vector, cosP, sinP);
        if (directionalY)
            Vectors.rotateAroundAxisY(vector, cosY, sinY);
        return directionalX || directionalY ? direction.clone().add(vector) : vector;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        return null;
    }

    @Override
    public Script clone() {
        return null;
    }
}
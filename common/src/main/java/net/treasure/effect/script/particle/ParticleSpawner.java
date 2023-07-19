package net.treasure.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.TreasureParticles;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.color.data.duo.DuoImpl;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.util.math.Vectors;
import net.treasure.util.nms.particles.ParticleBuilder;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.util.nms.particles.Particles;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParticleSpawner extends Script {

    protected ParticleEffect particle;
    protected ParticleOrigin origin;

    protected VectorArgument position;
    protected VectorArgument offset;
    protected VectorArgument multiplier;

    protected ColorData colorData;
    protected Object particleData;
    protected IntArgument amount;
    protected RangeArgument speed, size;
    protected boolean directional = false;

    protected boolean longDistance = false;

    @Nullable
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

        var offset = this.offset != null ? this.offset.get(player, this, data) : null;
        if (directional && offset != null) {
            offset = Vectors.rotateAroundAxisX(offset, origin.getPitch());
            offset = Vectors.rotateAroundAxisY(offset, origin.getYaw());
            offset = offset.add(origin.getDirection().add(offset));
        }

        if (offset != null)
            builder.offset(offset);

        builder.longDistance(longDistance);

        var playerManager = TreasureParticles.getPlayerManager();
        builder.viewers(viewer -> {
            var d = playerManager.getEffectData(viewer);
            return d != null && d.canSeeEffects();
        });

        return new ParticleContext(builder, origin);
    }

    public void updateParticleData(Player player, EffectData data, ParticleBuilder builder) {
        if (particleData != null) {
            builder.data(particleData);
            return;
        }

        if (colorData == null) {
            particleData = Particles.NMS.getParticleParam(particle);
            builder.data(particleData);
            return;
        }

        if (particle.hasProperty(ParticleEffect.Property.DUST)) {
            var size = this.size != null ? this.size.get(player, this, data) : 1;
            if (particle.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    builder.data(Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size));
                } else
                    builder.data(Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size));
            else
                builder.data(Particles.NMS.getDustData(colorData.next(data), size));
        } else if (particle.hasProperty(ParticleEffect.Property.OFFSET_COLOR)) {
            if (particle.equals(ParticleEffect.NOTE) && colorData.isNote()) {
                builder.data(Particles.NMS.getParticleParam(particle));
                builder.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());
            } else
                builder.offsetColor(colorData.next(data));
        }
    }

    public Object particleData(Player player, EffectData data) {
        if (particleData != null) return particleData();

        if (colorData == null || particle.hasProperty(ParticleEffect.Property.OFFSET_COLOR)) {
            particleData = Particles.NMS.getParticleParam(particle);
            return particleData;
        }

        if (particle.hasProperty(ParticleEffect.Property.DUST)) {
            var size = this.size != null ? this.size.get(player, this, data) : 1;
            if (particle.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    return Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size);
                } else
                    return Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size);
            else
                return Particles.NMS.getDustData(colorData.next(data), size);
        }
        return null;
    }

    public Location rotate(Location origin, Vector direction, float pitch, float yaw, Vector vector) {
        if (directional) {
            vector = Vectors.rotateAroundAxisX(vector, pitch);
            vector = Vectors.rotateAroundAxisY(vector, yaw);
            origin.add(direction.add(vector));
        } else {
            origin.add(vector);
        }
        return origin;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        return null;
    }

    @Override
    public Script clone() {
        return null;
    }
}
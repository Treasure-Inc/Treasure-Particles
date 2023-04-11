package net.treasure.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.color.data.duo.DuoImpl;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.util.Vectors;
import net.treasure.util.particles.Particles;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParticleSpawner extends Script {

    protected ParticleEffect particle;
    protected ParticleOrigin origin;
    protected VectorArgument position;
    protected ColorData colorData;
    protected Object particleData;
    protected IntArgument amount;
    protected FloatArgument multiplier;
    protected RangeArgument speed, size;
    protected boolean directional = false;

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
            var size = this.size != null ? this.size.get(player, data) : 1;
            if (particle.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    builder.data(Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size));
                } else
                    builder.data(Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size));
            else
                builder.data(Particles.NMS.getDustData(colorData.next(data), size));
        } else if (particle.hasProperty(ParticleEffect.Property.CAN_BE_COLORED)) {
            if (particle.equals(ParticleEffect.NOTE) && colorData.isNote()) {
                builder.data(Particles.NMS.getParticleParam(particle));
                builder.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());
            } else
                builder.data(Particles.NMS.getColorData(colorData.next(data)));
        }
    }

    public Location rotate(Player player, Location origin, Vector vector) {
        if (directional) {
            vector = Vectors.rotateAroundAxisX(vector, player.getEyeLocation().getPitch());
            vector = Vectors.rotateAroundAxisY(vector, player.getEyeLocation().getYaw());
            origin = origin.add(player.getLocation().getDirection().add(vector));
        } else {
            origin = origin.add(vector);
        }

        return origin;
    }

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        return null;
    }

    @Override
    public Script clone() {
        return null;
    }
}
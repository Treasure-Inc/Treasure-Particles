package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.Vectors;
import net.treasure.util.particles.Particles;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class DotParticle extends ParticleSpawner {

    VectorArgument offset;

    public DotParticle(ParticleEffect effect, ParticleOrigin origin,
                       VectorArgument position, VectorArgument offset,
                       ColorData colorData, Object particleData,
                       IntArgument amount, FloatArgument multiplier, RangeArgument speed, RangeArgument size, boolean directional) {
        super(effect, origin, position, colorData, particleData, amount, multiplier, speed, size, directional);
        this.offset = offset;
    }

    @Override
    public TickResult tick(Player player, EffectData data, int times) {
        var context = tick(player, data);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder();
        var origin = context.origin();

        var vector = position == null ? new Vector(0, 0, 0) : position.get(player, data);
        builder.location(rotate(player, origin, vector));

        var offset = this.offset != null ? this.offset.get(player, data) : null;
        if (directional && offset != null) {
            offset = Vectors.rotateAroundAxisX(offset, player.getEyeLocation().getPitch());
            offset = Vectors.rotateAroundAxisY(offset, player.getEyeLocation().getYaw());
            offset = offset.add(player.getLocation().getDirection().add(offset));
        }

        if (offset != null)
            builder.offset(offset);

        updateParticleData(player, data, builder);

        Particles.send(builder);
        return TickResult.NORMAL;
    }

    @Override
    public DotParticle clone() {
        return new DotParticle(particle, origin, position, offset, colorData, particleData, amount, multiplier, speed, size, directional);
    }
}
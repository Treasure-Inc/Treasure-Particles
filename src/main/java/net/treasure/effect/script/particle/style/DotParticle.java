package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.argument.type.BooleanArgument;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.Vectors;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.ParticleData;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class DotParticle extends ParticleSpawner {

    VectorArgument offset;

    public DotParticle(ParticleEffect effect, ParticleOrigin origin,
                       VectorArgument position, VectorArgument offset,
                       ColorData colorData, ParticleData particleData,
                       IntArgument amount, FloatArgument multiplier, RangeArgument speed, RangeArgument size, BooleanArgument directional) {
        super(effect, origin, position, colorData, particleData, amount, multiplier, speed, size, directional);
        this.offset = offset;
    }

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        var builder = tick(player, data);
        if (builder == null) return TickResult.NORMAL;

        var offset = this.offset != null ? this.offset.get(player, data) : null;

        if (directional.get(player, data) && offset != null) {
            offset = Vectors.rotateAroundAxisX(offset, player.getEyeLocation().getPitch());
            offset = Vectors.rotateAroundAxisY(offset, player.getEyeLocation().getYaw());
            offset = offset.add(player.getLocation().getDirection().add(offset));
        }

        if (offset != null)
            builder.setOffset(offset);

        display(builder);
        return TickResult.NORMAL;
    }

    @Override
    public DotParticle clone() {
        return new DotParticle(effect, origin, position, offset, colorData, particleData, amount, multiplier, speed, size, directional);
    }
}
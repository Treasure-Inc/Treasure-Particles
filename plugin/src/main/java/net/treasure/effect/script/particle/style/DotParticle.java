package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.particles.Particles;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class DotParticle extends ParticleSpawner {

    public DotParticle(ParticleEffect effect, ParticleOrigin origin,
                       VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                       ColorData colorData, Object particleData,
                       IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder();
        var origin = context.origin();

        var vector = position == null ? new Vector(0, 0, 0) : position.get(player, data);
        builder.location(rotate(origin, origin.getDirection(), origin.getPitch(), origin.getYaw(), vector));

        updateParticleData(player, data, builder);

        Particles.send(builder);
        return TickResult.NORMAL;
    }

    @Override
    public DotParticle clone() {
        return new DotParticle(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
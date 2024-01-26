package net.treasure.particles.effect.script.particle.style.single;

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
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.Player;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class SingleParticle extends ParticleSpawner {

    public SingleParticle(ParticleEffect effect, ParticleOrigin origin,
                          VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                          ColorData colorData, Object particleData,
                          IntArgument amount, RangeArgument speed, RangeArgument size,
                          boolean directional, boolean longDistance) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional, longDistance);
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event, true, true);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;
        builder.location(context.origin);

        updateParticleData(builder, player, data);

        Particles.send(builder);
        return TickResult.NORMAL;
    }

    @Override
    public SingleParticle clone() {
        return new SingleParticle(
                particle, origin,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directional, longDistance
        );
    }
}
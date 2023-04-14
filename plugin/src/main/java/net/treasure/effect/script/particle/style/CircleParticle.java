package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.color.data.duo.DuoImpl;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.type.BooleanArgument;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.math.MathUtils;
import net.treasure.util.particles.Particles;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class CircleParticle extends ParticleSpawner {

    IntArgument particles = new IntArgument(32);
    RangeArgument radius = new RangeArgument(1);
    BooleanArgument tickData = new BooleanArgument(false);

    public CircleParticle(ParticleEffect particle, ParticleOrigin origin, VectorArgument position,
                          ColorData colorData, Object particleData,
                          IntArgument particles, RangeArgument radius,
                          IntArgument amount, FloatArgument multiplier, RangeArgument speed, RangeArgument size, boolean directional) {
        super(particle, origin, position, colorData, particleData, amount, multiplier, speed, size, directional);
        this.particles = particles;
        this.radius = radius;
    }

    @Override
    public TickResult tick(Player player, EffectData data, int times) {
        var context = tick(player, data);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder();
        var origin = context.origin();

        var particles = this.particles.get(player, data);
        var radius = this.radius.get(player, data);
        var tickData = this.tickData.get(player, data);

        var playerManager = TreasurePlugin.getInstance().getPlayerManager();

        var particleData = particleData(player, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        var vector = this.position != null ? position.get(player, data) : new Vector(0, 0, 0);

        for (int i = 0; i < particles; i++) {
            var r = 2 * Math.PI * i / particles;
            var x = MathUtils.cos(r) * radius;
            var y = MathUtils.sin(r) * radius;

            var location = rotate(player, origin.clone(), new Vector(x, y, 0).add(vector));

            var copy = builder.copy();
            copy.location(location)
                    .data(particleData)
                    .viewers(viewer -> playerManager.getEffectData(viewer).canSeeEffects(viewer));

            if (particle.equals(ParticleEffect.NOTE) && colorData.isNote())
                copy.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());

            builders.add(copy);

            if (tickData)
                particleData = particleData(player, data);
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    public Object particleData(Player player, EffectData data) {
        if (particleData != null) return particleData();

        if (colorData == null) {
            particleData = Particles.NMS.getParticleParam(particle);
            return particleData;
        }

        if (particle.hasProperty(ParticleEffect.Property.DUST)) {
            var size = this.size != null ? this.size.get(player, data) : 1;
            if (particle.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                if (colorData instanceof DuoImpl duo) {
                    var pair = duo.nextDuo();
                    return Particles.NMS.getColorTransitionData(pair.getKey(), pair.getValue(), size);
                } else
                    return Particles.NMS.getColorTransitionData(colorData.next(data), colorData.tempNext(data), size);
            else
                return Particles.NMS.getDustData(colorData.next(data), size);
        } else if (particle.hasProperty(ParticleEffect.Property.CAN_BE_COLORED))
            return Particles.NMS.getColorData(colorData.next(data));

        return null;
    }

    @Override
    public Script clone() {
        return new CircleParticle(particle, origin, position, colorData, particleData, particles, radius, amount, multiplier, speed, size, directional);
    }
}
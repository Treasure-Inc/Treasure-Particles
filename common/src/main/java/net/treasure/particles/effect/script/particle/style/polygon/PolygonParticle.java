package net.treasure.particles.effect.script.particle.style.polygon;

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
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.math.Vectors;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class PolygonParticle extends ParticleSpawner {

    private RangeArgument radius = new RangeArgument(1f);
    private RangeArgument rotation;
    private int points = 3;
    private float step = 0.1f;
    private boolean tickData = false;
    private boolean vertical = false;

    private Vector[] cache;

    public PolygonParticle(ParticleEffect particle, LocationOrigin origin,
                           RangeArgument radius, RangeArgument rotation, int points, float step,
                           boolean tickData, boolean vertical,
                           Vector[] cache,
                           VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                           ColorData colorData, Object particleData,
                           IntArgument amount, RangeArgument speed, RangeArgument size,
                           boolean directionalX, boolean directionalY, boolean longDistance,
                           EntityType entityTypeFilter, boolean spawnEffectOnPlayer) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance, entityTypeFilter, spawnEffectOnPlayer);
        this.radius = radius;
        this.rotation = rotation;
        this.points = points;
        this.step = step;
        this.tickData = tickData;
        this.vertical = vertical;
        this.cache = cache;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var context = tick(data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        var radius = this.radius.get(this, data);

        var rotation = this.rotation != null ? this.rotation.get(this, data) : null;
        var hasRotation = rotation != null;

        var angle = hasRotation ? Math.toRadians(rotation) : null;
        var cos = hasRotation ? MathUtils.cos(angle) : null;
        var sin = hasRotation ? MathUtils.sin(angle) : null;

        updateParticleData(builder, data);

        List<ParticleBuilder> builders = new ArrayList<>();

        for (var v : cache) {
            var vector = v.clone();
            if (hasRotation)
                if (vertical)
                    Vectors.rotateAroundAxisZ(vector, cos, sin);
                else
                    Vectors.rotateAroundAxisY(vector, cos, sin);

            var location = location(context, vector.multiply(radius));
            builders.add(builder.copy().location(location));

            if (tickData)
                updateParticleData(builder, data);
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    public void initialize() {
        List<Vector> vectors = new ArrayList<>();
        for (int iteration = 0; iteration < points; iteration++) {
            double angle = MathUtils.PI2 / points * iteration;
            double nextAngle = MathUtils.PI2 / points * (iteration + 1);

            double x1 = MathUtils.cos(angle);
            double x2 = MathUtils.cos(nextAngle);
            double deltaX = x2 - x1;

            double y1 = MathUtils.sin(angle);
            double y2 = MathUtils.sin(nextAngle);
            double deltaY = y2 - y1;

            double distance = Math.sqrt((deltaX - x1) * (deltaX - x1) + (deltaY - y1) * (deltaY - y1));
            for (double d = 0; d < distance; d += step)
                vectors.add(vertical ? new Vector(x1 + deltaX * d / distance, y1 + deltaY * d / distance, 0) : new Vector(x1 + deltaX * d / distance, 0, y1 + deltaY * d / distance));
        }
        this.cache = vectors.toArray(Vector[]::new);
    }

    @Override
    public PolygonParticle clone() {
        return new PolygonParticle(
                particle, origin,
                radius, rotation, points, step,
                tickData, vertical,
                cache,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance,
                entityTypeFilter, spawnEffectOnPlayer
        );
    }
}
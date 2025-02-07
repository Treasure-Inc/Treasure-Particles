package net.treasure.particles.effect.script.particle.style.text.animated;

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
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.effect.script.particle.style.text.TextParticle;
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
public class AnimatedTextParticle extends TextParticle {

    private AnimationOrigin animationOrigin;
    private int duration;

    private Vector originVector;

    public AnimatedTextParticle(ParticleEffect particle, LocationOrigin origin,
                                int stepX, int stepY, float scale, boolean tickData, boolean vertical, Float rotateX, Float rotateY,
                                AnimationOrigin animationOrigin, int duration,
                                Vector[] cache, Vector originVector,
                                VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                                ColorData colorData, Object particleData,
                                IntArgument amount, RangeArgument speed, RangeArgument size,
                                boolean directionalX, boolean directionalY, boolean longDistance,
                                EntityType entityTypeFilter, boolean spawnEffectOnPlayer) {
        super(particle, origin,
                stepX, stepY, scale, tickData, vertical, rotateX, rotateY,
                cache,
                position, offset, multiplier,
                colorData, particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance,
                entityTypeFilter, spawnEffectOnPlayer);
        this.originVector = originVector;
        this.animationOrigin = animationOrigin;
        this.duration = duration;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var context = tick(data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;
        var color = colorData != null ? colorData.next(data) : null;

        var origin = context.origin.clone().add(originVector);

        List<ParticleBuilder> builders = new ArrayList<>();
        for (var v : cache) {
            v = v.clone();
            if (directionalX)
                Vectors.rotateAroundAxisX(v, context.cosP, context.sinP);
            if (directionalY)
                Vectors.rotateAroundAxisY(v, context.cosY, context.sinY);

            builders.add(builder.copy()
                    .location(origin.clone())
                    .data(Particles.NMS.getTargetData(
                            particle,
                            data,
                            color,
                            context.origin.clone().add(v),
                            duration
                    ))
            );

            if (tickData)
                color = colorData != null ? colorData.next(data) : null;
        }

        Particles.send(builders);
        return TickResult.NORMAL;
    }

    public void initialize() {
        boolean rX = rotateX != null, rY = rotateY != null;
        double cosRx = 0, sinRx = 0, cosRy = 0, sinRy = 0;

        if (rX) {
            var angleRx = Math.toRadians(rotateX);
            cosRx = MathUtils.cos(angleRx);
            sinRx = MathUtils.sin(angleRx);
        }

        if (rY) {
            var angleRy = Math.toRadians(-rotateY);
            cosRy = MathUtils.cos(angleRy);
            sinRy = MathUtils.sin(angleRy);
        }

        var image = stringToBufferedImage();
        List<Vector> cache = new ArrayList<>();

        var width = image.getWidth();
        var height = image.getHeight();

        int indexX, indexY;
        switch (animationOrigin) {
            case CENTER -> {
                indexX = width / 2;
                indexY = height / 2;
            }
            case TOP_TO_BOTTOM -> {
                indexX = width / 2;
                indexY = 0;
            }
            case BOTTOM_TO_TOP -> {
                indexX = width / 2;
                indexY = height - 1;
            }
            case LEFT_TO_RIGHT -> {
                indexX = 0;
                indexY = height / 2;
            }
            case RIGHT_TO_LEFT -> {
                indexX = width - 1;
                indexY = height / 2;
            }
            default -> {
                indexX = 0;
                indexY = 0;
            }
        }

        for (int y = height - 1; y >= 0; y -= stepY) {
            for (int x = width - 1; x >= 0; x -= stepX) {
                if (indexX == x && indexY == y) {
                    var v = (vertical ?
                            new Vector((float) width / 2 - x, (float) height / 2 - y, 0) :
                            new Vector((float) height / 2 - y, 0, (float) width / 2 - x)
                    ).multiply(scale);

                    if (rX)
                        Vectors.rotateAroundAxisX(v, cosRx, sinRx);
                    if (rY)
                        Vectors.rotateAroundAxisY(v, cosRy, sinRy);

                    originVector = v;
                }

                if (BLACK != image.getRGB(x, y)) continue;

                var v = (vertical ?
                        new Vector((float) width / 2 - x, (float) height / 2 - y, 0) :
                        new Vector((float) height / 2 - y, 0, (float) width / 2 - x)
                ).multiply(scale);

                if (rX)
                    Vectors.rotateAroundAxisX(v, cosRx, sinRx);
                if (rY)
                    Vectors.rotateAroundAxisY(v, cosRy, sinRy);

                cache.add(v);
            }
        }
        this.cache = cache.toArray(Vector[]::new);
    }

    @Override
    public AnimatedTextParticle clone() {
        return new AnimatedTextParticle(
                particle, origin,
                stepX, stepY, scale, tickData, vertical, rotateX, rotateY,
                animationOrigin, duration,
                cache, originVector,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance,
                entityTypeFilter, spawnEffectOnPlayer
        );
    }
}
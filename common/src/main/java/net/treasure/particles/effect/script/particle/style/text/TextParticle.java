package net.treasure.particles.effect.script.particle.style.text;

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

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class TextParticle extends ParticleSpawner {

    protected static final int BLACK = Color.black.getRGB();

    protected int stepX = 1;
    protected int stepY = 1;
    protected float scale = 0.2f;
    protected String fontName = "Tahoma";
    protected String text;
    protected boolean tickData;
    protected boolean vertical = true;

    protected Float rotateX;
    protected Float rotateY;

    protected Vector[] cache;

    public TextParticle(ParticleEffect particle, LocationOrigin origin,
                        int stepX, int stepY, float scale, boolean tickData, boolean vertical, Float rotateX, Float rotateY,
                        Vector[] cache,
                        VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                        ColorData colorData, Object particleData,
                        IntArgument amount, RangeArgument speed, RangeArgument size,
                        boolean directionalX, boolean directionalY, boolean longDistance,
                        EntityType entityTypeFilter, boolean spawnEffectOnPlayer) {
        super(particle, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directionalX, directionalY, longDistance, entityTypeFilter, spawnEffectOnPlayer);
        this.stepX = stepX;
        this.stepY = stepY;
        this.scale = scale;
        this.tickData = tickData;
        this.vertical = vertical;
        this.rotateX = rotateX;
        this.rotateY = rotateY;

        this.cache = cache;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var context = tick(data, event, true, false);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        updateParticleData(builder, data);

        List<ParticleBuilder> builders = new ArrayList<>();
        for (var v : cache) {
            v = v.clone();
            if (directionalX)
                Vectors.rotateAroundAxisX(v, context.cosP, context.sinP);
            if (directionalY)
                Vectors.rotateAroundAxisY(v, context.cosY, context.sinY);

            builders.add(builder.copy().location(context.origin.clone().add(v)));

            if (tickData)
                updateParticleData(builder, data);
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

        for (int y = height - 1; y >= 0; y -= stepY) {
            for (int x = width - 1; x >= 0; x -= stepX) {
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

    public BufferedImage stringToBufferedImage() {
        var font = new Font(fontName, Font.PLAIN, 16);

        var img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        var graphics = img.createGraphics();
        graphics.setFont(font);

        var fm = graphics.getFontMetrics();
        var rect = font.getStringBounds(text, fm.getFontRenderContext());
        graphics.dispose();

        img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
        graphics = img.createGraphics();
        graphics.setColor(Color.black);
        graphics.setFont(font);

        graphics.drawString(text, 0, fm.getAscent());
        graphics.dispose();

        return img;
    }

    @Override
    public TextParticle clone() {
        return new TextParticle(
                particle, origin,
                stepX, stepY, scale, tickData, vertical, rotateX, rotateY,
                cache,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directionalX, directionalY, longDistance,
                entityTypeFilter, spawnEffectOnPlayer
        );
    }
}
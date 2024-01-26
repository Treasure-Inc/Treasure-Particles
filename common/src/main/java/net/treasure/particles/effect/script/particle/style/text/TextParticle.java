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
import net.treasure.particles.effect.script.particle.ParticleOrigin;
import net.treasure.particles.effect.script.particle.ParticleSpawner;
import net.treasure.particles.util.math.MathUtils;
import net.treasure.particles.util.math.Vectors;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class TextParticle extends ParticleSpawner {

    private static final int BLACK = Color.black.getRGB();

    private int stepX = 1;
    private int stepY = 1;
    private float scale = 0.2f;
    private String fontName = "Tahoma";
    private String text;
    private boolean tickData;
    private boolean vertical = true;

    private Float rotateX;
    private Float rotateY;

    private Vector[] cache;
    private double cosRx, sinRx, cosRy, sinRy;

    public TextParticle(ParticleEffect effect, ParticleOrigin origin,
                        int stepX, int stepY, float scale, boolean tickData, boolean vertical, Float rotateX, Float rotateY,
                        Vector[] cache, double cosRx, double sinRx, double cosRy, double sinRy,
                        VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                        ColorData colorData, Object particleData,
                        IntArgument amount, RangeArgument speed, RangeArgument size,
                        boolean directional, boolean longDistance) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional, longDistance);
        this.stepX = stepX;
        this.stepY = stepY;
        this.scale = scale;
        this.tickData = tickData;
        this.vertical = vertical;
        this.rotateX = rotateX;
        this.rotateY = rotateY;

        this.cache = cache;
        this.cosRx = cosRx;
        this.sinRx = sinRx;
        this.cosRy = cosRy;
        this.sinRy = sinRy;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event, true, true);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder;

        updateParticleData(builder, player, data);

        boolean rY = rotateY != null, rX = rotateX != null;

        List<ParticleBuilder> builders = new ArrayList<>();
        for (var v : cache) {
            Vectors.rotateAroundAxisY(v, rY ? cosRy : context.cosY, rY ? sinRy : context.sinY);
            if (rX)
                Vectors.rotateAroundAxisX(v, cosRx, sinRx);

            builders.add(builder.copy().location(context.origin.clone().add(v)));

            if (tickData)
                updateParticleData(builder, player, data);
        }
        Particles.send(builders);
        return TickResult.NORMAL;
    }

    public void initialize() {
        var image = stringToBufferedImage(new Font(fontName, Font.PLAIN, 16), text);
        List<Vector> cache = new ArrayList<>();
        for (int y = image.getHeight() - 1; y >= 0; y -= stepY) {
            for (int x = image.getWidth() - 1; x >= 0; x -= stepX) {
                if (BLACK != image.getRGB(x, y)) continue;

                var v = (vertical ?
                        new Vector((float) image.getWidth() / 2 - x, (float) image.getHeight() / 2 - y, 0) :
                        new Vector((float) image.getHeight() / 2 - y, 0, (float) image.getWidth() / 2 - x)
                ).multiply(scale);
                cache.add(v);
            }
        }
        this.cache = cache.toArray(Vector[]::new);

        if (rotateX != null) {
            var angleRx = Math.toRadians(rotateX);
            this.cosRx = MathUtils.cos(angleRx);
            this.sinRx = MathUtils.sin(angleRx);
        }

        if (rotateY != null) {
            var angleRy = Math.toRadians(-rotateY);
            this.cosRy = MathUtils.cos(angleRy);
            this.sinRy = MathUtils.sin(angleRy);
        }
    }

    public static BufferedImage stringToBufferedImage(Font font, String s) {
        var img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        var graphics = img.createGraphics();
        graphics.setFont(font);

        var frc = graphics.getFontMetrics().getFontRenderContext();
        var rect = font.getStringBounds(s, frc);
        graphics.dispose();

        img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
        graphics = img.createGraphics();
        graphics.setColor(Color.black);
        graphics.setFont(font);

        FontMetrics fm = graphics.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();

        graphics.drawString(s, x, y);
        graphics.dispose();

        return img;
    }

    @Override
    public TextParticle clone() {
        return new TextParticle(
                particle, origin,
                stepX, stepY, scale, tickData, vertical, rotateX, rotateY,
                cache, cosRx, sinRx, cosRy, sinRy,
                position, offset, multiplier,
                colorData == null ? null : colorData.clone(), particleData,
                amount, speed, size,
                directional, longDistance
        );
    }
}
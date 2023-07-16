package net.treasure.effect.script.particle.style;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.util.nms.particles.ParticleBuilder;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.util.math.Vectors;
import net.treasure.util.nms.particles.Particles;
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

    static final int black = Color.black.getRGB();

    int stepX = 1;
    int stepY = 1;
    float scale = 0.2f;
    String fontName = "Tahoma";
    String text;
    boolean tickData;
    boolean vertical = true;

    BufferedImage image;

    public TextParticle(ParticleEffect effect, ParticleOrigin origin,
                        int stepX, int stepY, float scale, boolean tickData, boolean vertical,
                        BufferedImage image,
                        VectorArgument position, VectorArgument offset, VectorArgument multiplier,
                        ColorData colorData, Object particleData,
                        IntArgument amount, RangeArgument speed, RangeArgument size, boolean directional) {
        super(effect, origin, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
        this.stepX = stepX;
        this.stepY = stepY;
        this.scale = scale;
        this.tickData = tickData;
        this.vertical = vertical;
        this.image = image;
    }

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var context = tick(player, data, event);
        if (context == null) return TickResult.NORMAL;

        var builder = context.builder();
        var origin = context.origin();

        var vector = this.position == null ? new Vector(0, 0, 0) : this.position.get(player, data);

        var yaw = origin.getYaw();
        var location = rotate(origin, origin.getDirection(), origin.getPitch(), yaw, vector);

        var particleData = particleData(player, data);

        List<ParticleBuilder> builders = new ArrayList<>();
        for (int y = image.getHeight() - 1; y >= 0; y -= stepY) {
            for (int x = image.getWidth() - 1; x >= 0; x -= stepX) {
                if (black != image.getRGB(x, y)) continue;

                var v = (vertical ?
                        new Vector((float) image.getWidth() / 2 - x, (float) image.getHeight() / 2 - y, 0) :
                        new Vector((float) image.getHeight() / 2 - y, 0, (float) image.getWidth() / 2 - x)
                ).multiply(scale);
                Vectors.rotateAroundAxisY(v, yaw);

                var copy = builder.copy()
                        .location(location.clone().add(v))
                        .data(particleData);

                if (particle == ParticleEffect.NOTE && colorData != null && colorData.isNote())
                    copy.noteColor(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : colorData.index());
                else if (particle.hasProperty(ParticleEffect.Property.OFFSET_COLOR) && colorData != null)
                    copy.offsetColor(colorData.next(data));

                builders.add(copy);

                if (tickData)
                    particleData = particleData(player, data);
            }
        }
        Particles.send(builders);
        return TickResult.NORMAL;
    }

    public void initialize() {
        this.image = stringToBufferedImage(new Font(fontName, Font.PLAIN, 16), text);
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
        return new TextParticle(particle, origin, stepX, stepY, scale, tickData, vertical, image, position, offset, multiplier, colorData, particleData, amount, speed, size, directional);
    }
}
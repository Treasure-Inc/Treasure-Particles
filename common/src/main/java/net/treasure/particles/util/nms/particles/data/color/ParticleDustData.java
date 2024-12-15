package net.treasure.particles.util.nms.particles.data.color;

import lombok.Getter;
import net.treasure.particles.util.nms.particles.data.ParticleData;
import org.bukkit.Color;

@Getter
public abstract class ParticleDustData extends ParticleData {

    protected int asRGB;
    protected float red, green, blue;
    protected float size;

    public ParticleDustData(Color color, float size) {
        this.asRGB = color.asRGB();
        this.red = color.getRed() / 255f;
        this.green = color.getGreen() / 255f;
        this.blue = color.getBlue() / 255f;
        this.size = size;
    }
}
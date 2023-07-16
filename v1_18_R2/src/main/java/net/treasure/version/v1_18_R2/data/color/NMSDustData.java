package net.treasure.version.v1_18_R2.data.color;

import com.mojang.math.Vector3fa;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.treasure.util.nms.particles.data.color.ParticleDustData;
import org.bukkit.Color;

public class NMSDustData extends ParticleDustData {

    public NMSDustData(Color color, float size) {
        super(color, size);
    }

    @Override
    public Object toNMS() {
        return new ParticleParamRedstone(new Vector3fa(getRed(), getGreen(), getBlue()), size);
    }
}
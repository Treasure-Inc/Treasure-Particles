package net.treasure.version.v1_16_R1.data.color;

import net.minecraft.server.v1_16_R1.ParticleParamRedstone;
import net.treasure.common.particles.data.color.ParticleDustData;
import org.bukkit.Color;

public class NMSDustData extends ParticleDustData {

    public NMSDustData(Color color, float size) {
        super(color, size);
    }

    @Override
    public Object toNMS() {
        return new ParticleParamRedstone(getRed(), getGreen(), getBlue(), size);
    }
}
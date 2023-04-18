package net.treasure.version.v1_19_R3.data.color;

import net.minecraft.core.particles.ParticleParamRedstone;
import net.treasure.common.particles.data.color.ParticleDustData;
import org.bukkit.Color;
import org.joml.Vector3f;

public class NMSDustData extends ParticleDustData {

    public NMSDustData(Color color, float size) {
        super(color, size);
    }

    @Override
    public Object toNMS() {
        return new ParticleParamRedstone(new Vector3f(getRed(), getGreen(), getBlue()), size);
    }
}
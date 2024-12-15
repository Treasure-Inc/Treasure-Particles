package net.treasure.particles.version.v1_21_R1.data.color;

import net.minecraft.core.particles.ParticleParamRedstone;
import net.treasure.particles.util.nms.particles.data.color.ParticleDustData;
import org.bukkit.Color;
import org.joml.Vector3f;

public class NMSDustData extends ParticleDustData {

    public NMSDustData(Color color, float size) {
        super(color, size);
    }

    @Override
    public Object toNMS() {
        return new ParticleParamRedstone(new Vector3f(red, green, blue), size);
    }
}
package net.treasure.particles.version.v1_19_R1.data.color;

import com.mojang.math.Vector3fa;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.treasure.particles.util.nms.particles.data.color.ParticleDustData;
import org.bukkit.Color;

public class NMSDustData extends ParticleDustData {

    public NMSDustData(Color color, float size) {
        super(color, size);
    }

    @Override
    public Object toNMS() {
        return new ParticleParamRedstone(new Vector3fa(red, green, blue), size);
    }
}
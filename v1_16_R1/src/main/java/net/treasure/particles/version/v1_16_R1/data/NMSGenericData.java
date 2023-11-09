package net.treasure.particles.version.v1_16_R1.data;

import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.data.ParticleGenericData;
import org.bukkit.craftbukkit.v1_16_R1.CraftParticle;

public class NMSGenericData extends ParticleGenericData {

    public NMSGenericData(ParticleEffect effect, Object object) {
        super(effect, object);
    }

    @Override
    public Object toNMS() {
        var particle = particleEffect.bukkit();
        if (particle == null) return null;
        return CraftParticle.toNMS(particle, object);
    }
}
package net.treasure.particles.version.v1_21_R4.data;

import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.data.ParticleGenericData;
import org.bukkit.craftbukkit.v1_21_R4.CraftParticle;

public class NMSGenericData extends ParticleGenericData {

    public NMSGenericData(ParticleEffect effect, Object object) {
        super(effect, object);
    }

    @Override
    public Object toNMS() {
        var particle = particleEffect.bukkit();
        if (particle == null) return null;
        return CraftParticle.createParticleParam(particle, object);
    }
}
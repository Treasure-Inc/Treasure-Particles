package net.treasure.version.v1_20_R1.data;

import net.treasure.common.particles.ParticleEffect;
import net.treasure.common.particles.data.ParticleGenericData;
import org.bukkit.craftbukkit.v1_20_R1.CraftParticle;

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
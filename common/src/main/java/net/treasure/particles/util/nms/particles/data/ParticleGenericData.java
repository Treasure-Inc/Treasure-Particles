package net.treasure.particles.util.nms.particles.data;

import net.treasure.particles.util.nms.particles.ParticleEffect;

public abstract class ParticleGenericData extends ParticleData {
    protected ParticleEffect particleEffect;
    protected Object object;

    public ParticleGenericData(ParticleEffect particleEffect, Object object) {
        this.particleEffect = particleEffect;
        this.object = object;
    }
}
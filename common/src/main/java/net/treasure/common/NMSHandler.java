package net.treasure.common;

import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import org.bukkit.Color;

import java.util.List;

public abstract class NMSHandler {

    public abstract void sendParticle(ParticleBuilder builder);

    public abstract void sendParticles(List<ParticleBuilder> builders);

    public abstract Object getParticleParam(ParticleEffect effect);

    public Object getColorData(Color color) {
        return getDustData(color, 1f);
    }

    public abstract Object getDustData(Color color, float size);

    public abstract Object getColorTransitionData(Color color, Color transition, float size);

    public abstract Object getGenericData(ParticleEffect effect, Object data);
}
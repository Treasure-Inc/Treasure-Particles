package net.treasure.particles.util.nms;

import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractNMSHandler {

    public abstract void sendParticle(ParticleBuilder builder);

    public abstract void sendParticles(List<ParticleBuilder> builders);

    public abstract Object getParticleParam(ParticleEffect effect);

    public Object getColorData(Color color) {
        return getDustData(color, 1f);
    }

    public abstract Object getDustData(Color color, float size);

    public abstract Object getColorTransitionData(Color color, Color transition, float size);

    public abstract Object getGenericData(ParticleEffect effect, Object data);

    public abstract void strikeLightning(Location location, Predicate<Player> filter);
}
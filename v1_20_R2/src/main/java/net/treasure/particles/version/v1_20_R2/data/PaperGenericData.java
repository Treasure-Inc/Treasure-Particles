package net.treasure.particles.version.v1_20_R2.data;

import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.util.nms.particles.data.ParticleGenericData;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R2.CraftParticle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PaperGenericData extends ParticleGenericData {

    private static Method method;

    static {
        try {
            method = CraftParticle.class.getMethod("createParticleParam", Particle.class, Object.class);
        } catch (NoSuchMethodException ignored) {
        }
    }

    public PaperGenericData(ParticleEffect effect, Object object) {
        super(effect, object);
    }

    @Override
    public Object toNMS() {
        if (method == null) return null;
        var particle = particleEffect.bukkit();
        if (particle == null) return null;
        try {
            return method.invoke(null, particle, object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

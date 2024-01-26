package net.treasure.particles.effect.script.particle;

import lombok.AllArgsConstructor;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class ParticleContext {
    public ParticleBuilder builder;
    public Location origin;
    public Vector direction;

    public double cosP, sinP, cosY, sinY;
}
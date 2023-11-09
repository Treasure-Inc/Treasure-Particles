package net.treasure.particles.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class ParticleContext {
    ParticleBuilder builder;
    Location origin;
}
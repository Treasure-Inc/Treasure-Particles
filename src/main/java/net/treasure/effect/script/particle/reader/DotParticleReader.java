package net.treasure.effect.script.particle.reader;

import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.style.DotParticle;
import xyz.xenondevs.particle.PropertyType;

public class DotParticleReader extends ParticleReader<DotParticle> {

    public DotParticleReader() {
        super();

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.hasProperty(PropertyType.DIRECTIONAL) && !particle.hasProperty(PropertyType.DUST)) {
                error(c, "You cannot use 'offset' with this particle effect: " + particle.name());
                return;
            }
            c.script().offset(VectorArgument.read(c));
        }, "offset");
    }
}
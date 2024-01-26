package net.treasure.particles.effect.script.particle.reader.spiral;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.spiral.SpiralParticle;

public class SpiralParticleReader extends ParticleReader<SpiralParticle> {

    public SpiralParticleReader() {
        super();

        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
        addValidArgument(c -> c.script().steps(IntArgument.read(c)), "steps");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<SpiralParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new SpiralParticle());
        }
    }
}
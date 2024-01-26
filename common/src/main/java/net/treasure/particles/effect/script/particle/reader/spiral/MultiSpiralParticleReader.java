package net.treasure.particles.effect.script.particle.reader.spiral;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.spiral.MultiSpiralParticle;

public class MultiSpiralParticleReader extends ParticleReader<MultiSpiralParticle> {

    public MultiSpiralParticleReader() {
        super();

        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
        addValidArgument(c -> c.script().spirals(IntArgument.read(c)), "spirals");
        addValidArgument(c -> c.script().steps(IntArgument.read(c)), "steps");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<MultiSpiralParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new MultiSpiralParticle());
        }
    }
}
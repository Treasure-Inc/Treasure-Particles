package net.treasure.particles.effect.script.particle.reader.spiral;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.DoubleArgument;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.spiral.FullSpiralParticle;

public class FullSpiralParticleReader extends ParticleReader<FullSpiralParticle> {

    public FullSpiralParticleReader() {
        super();

        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
        addValidArgument(c -> c.script().spirals(IntArgument.read(c)), "spirals");
        addValidArgument(c -> c.script().steps(IntArgument.read(c)), "steps");
        addValidArgument(c -> c.script().gap(DoubleArgument.read(c)), "gap");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        addValidArgument(c -> c.script().reverse(StaticArgument.asBoolean(c) ? -1 : 1), "reverse");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<FullSpiralParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new FullSpiralParticle());
        }
    }
}
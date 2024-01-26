package net.treasure.particles.effect.script.particle.reader.circle;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.circle.SpreadCircleParticle;

public class SpreadCircleParticleReader extends ParticleReader<SpreadCircleParticle> {

    public SpreadCircleParticleReader() {
        super();

        addValidArgument(c -> c.script().particles(IntArgument.read(c, 1)), "particles");
        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
        addValidArgument(c -> c.script().spread(RangeArgument.read(c)), "spread");
        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<SpreadCircleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new SpreadCircleParticle());
            script.directional(true);
        }
    }
}
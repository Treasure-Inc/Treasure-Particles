package net.treasure.particles.effect.script.particle.reader.circle;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.circle.CircleParticle;

public class CircleParticleReader extends ParticleReader<CircleParticle> {

    public CircleParticleReader() {
        super();

        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        addValidArgument(c -> c.script().particles(IntArgument.read(c, 1)), "particles");
        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<CircleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new CircleParticle());
            script.directionalX(true).directionalY(true);
        }
    }
}
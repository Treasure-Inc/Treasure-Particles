package net.treasure.particles.effect.script.particle.reader.circle;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.circle.SpreadCircleParticle;

public class SpreadCircleParticleReader extends ParticleReader<SpreadCircleParticle> {

    public SpreadCircleParticleReader() {
        super();

        addValidArgument(c -> c.script().spread(RangeArgument.read(c)), true, "spread");
        CircleParticleReader.addValidArguments(this);
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<SpreadCircleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new SpreadCircleParticle());
            script.directionalX(true).directionalY(true);
        }
    }
}
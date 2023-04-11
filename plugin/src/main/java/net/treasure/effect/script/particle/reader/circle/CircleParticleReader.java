package net.treasure.effect.script.particle.reader.circle;

import net.treasure.effect.Effect;
import net.treasure.effect.script.argument.type.BooleanArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.particle.reader.ParticleReader;
import net.treasure.effect.script.particle.style.CircleParticle;

public class CircleParticleReader extends ParticleReader<CircleParticle> {

    public CircleParticleReader() {
        super();

        removeArguments("direction", "directional");

        addValidArgument(c -> c.script().tickData(BooleanArgument.read(c)), "tick-data");
        addValidArgument(c -> c.script().particles(IntArgument.read(c)), "particles");
        addValidArgument(c -> c.script().radius(RangeArgument.read(c)), "radius");
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<CircleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new CircleParticle());
            script.directional(true);
        }
    }
}
package net.treasure.particles.effect.script.particle.reader.target;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.RangeArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.reader.circle.CircleParticleReader;
import net.treasure.particles.effect.script.particle.style.target.circle.TargetCircleParticle;
import net.treasure.particles.effect.script.particle.style.target.circle.TargetPoint;

public class TargetCircleParticleReader extends ParticleReader<TargetCircleParticle> {

    public TargetCircleParticleReader() {
        super();
        CircleParticleReader.addValidArguments(this);
        addValidArgument(c -> c.script().spread(RangeArgument.read(c)), true, "spread");
        addValidArgument(c -> c.script().duration(IntArgument.read(c, 1)), true, "duration");
        addValidArgument(c -> c.script().targetPoint(StaticArgument.asEnum(c, TargetPoint.class)), true, "target-point", "target");
    }

    @Override
    public boolean validate(ParticleReader.Context<TargetCircleParticle> context) throws ReaderException {
        if (!super.validate(context)) return false;
        return TargetParticleReader.validateContext(this, context);
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<TargetCircleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new TargetCircleParticle());
        }
    }
}
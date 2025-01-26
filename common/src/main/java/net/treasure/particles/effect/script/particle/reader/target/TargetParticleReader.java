package net.treasure.particles.effect.script.particle.reader.target;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.target.TargetParticle;
import net.treasure.particles.util.nms.particles.ParticleEffect;

import java.util.stream.Collectors;

public class TargetParticleReader extends ParticleReader<TargetParticle> {

    public TargetParticleReader() {
        super();
        addValidArgument(c -> c.script().target(VectorArgument.read(c)), true, "target");
        addValidArgument(c -> c.script().duration(IntArgument.read(c, 1)), true, "duration");
    }

    public static boolean validateContext(ParticleReader<?> reader, ParticleReader.Context<?> c) throws ReaderException {
        if (!c.script().particle().hasProperty(ParticleEffect.Property.REQUIRES_TARGET)) {
            reader.error(c.effect(), c.type(), c.line(), "You can only use these particles for target script: " + ParticleEffect.byProperty(ParticleEffect.Property.REQUIRES_TARGET).stream().map(ParticleEffect::getFieldName).collect(Collectors.joining(", ")));
            return false;
        }
        return true;
    }

    @Override
    public boolean validate(ParticleReader.Context<TargetParticle> c) throws ReaderException {
        if (!super.validate(c)) return false;
        return validateContext(this, c);
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<TargetParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new TargetParticle());
        }
    }
}
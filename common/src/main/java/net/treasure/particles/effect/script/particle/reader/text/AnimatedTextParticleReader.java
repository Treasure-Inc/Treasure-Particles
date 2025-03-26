package net.treasure.particles.effect.script.particle.reader.text;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.reader.target.TargetParticleReader;
import net.treasure.particles.effect.script.particle.style.text.animated.AnimatedTextParticle;
import net.treasure.particles.effect.script.particle.style.text.animated.AnimationOrigin;

public class AnimatedTextParticleReader extends ParticleReader<AnimatedTextParticle> {

    public AnimatedTextParticleReader() {
        super();
        TextParticleReader.addValidArguments(this);
        addValidArgument(c -> c.script().duration(StaticArgument.asInt(c, 1)), true, "duration");
        addValidArgument(c -> c.script().animationOrigin(StaticArgument.asEnum(c, AnimationOrigin.class)), true, "animation-origin");
    }

    @Override
    public boolean validate(ParticleReader.Context<AnimatedTextParticle> c) throws ReaderException {
        if (!super.validate(c)) return false;
        return TextParticleReader.validateContext(this, c) && TargetParticleReader.validateContext(this, c);
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<AnimatedTextParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new AnimatedTextParticle());
        }
    }
}
package net.treasure.particles.effect.script.particle.reader.dot;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.DotParticle;

public class DotParticleReader extends ParticleReader<DotParticle> {

    public DotParticleReader() {
        super();
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<DotParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new DotParticle());
        }
    }
}
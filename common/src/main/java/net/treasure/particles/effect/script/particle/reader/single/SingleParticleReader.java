package net.treasure.particles.effect.script.particle.reader.single;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.single.SingleParticle;

public class SingleParticleReader extends ParticleReader<SingleParticle> {

    public SingleParticleReader() {
        super();
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<SingleParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new SingleParticle());
        }
    }
}
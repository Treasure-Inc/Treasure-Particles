package net.treasure.effect.script.particle.reader;

import net.treasure.effect.Effect;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.particle.style.DotParticle;

public class DotParticleReaderContext extends ReaderContext<DotParticle> {
    public DotParticleReaderContext(Effect effect, String type, String line) {
        super(effect, type, line, new DotParticle());
    }
}
package net.treasure.particles.effect.script.visual.reader;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;
import net.treasure.particles.effect.script.visual.Lightning;

public class LightningReader extends ScriptReader<LightningReader.Context, Lightning> {

    public LightningReader() {
        addValidArgument(c -> c.script().origin(StaticArgument.asEnum(c, LocationOrigin.class)), "origin");
        addValidArgument(c -> c.script().position(VectorArgument.read(c)), "position", "pos");
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ReaderContext<Lightning> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new Lightning());
        }
    }
}
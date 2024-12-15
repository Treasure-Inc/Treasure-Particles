package net.treasure.particles.effect.script.delay.reader;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.delay.DelayScript;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;

public class DelayReader extends ScriptReader<DelayReader.Context, DelayScript> {

    public DelayReader() {
        addValidArgument(c -> c.script().delay(StaticArgument.asInt(c, 1)), true, "value");
        addValidArgument(c -> c.script().action(StaticArgument.asEnum(c, Script.TickResult.class)), "action");
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ReaderContext<DelayScript> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new DelayScript());
        }
    }
}
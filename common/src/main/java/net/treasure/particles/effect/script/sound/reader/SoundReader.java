package net.treasure.particles.effect.script.sound.reader;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.sound.PlaySound;
import org.bukkit.SoundCategory;

public class SoundReader extends ScriptReader<SoundReader.Context, PlaySound> {

    public SoundReader() {
        addValidArgument(c -> c.script().sound(c.value()), "name");
        addValidArgument(c -> c.script().clientSide(Boolean.parseBoolean(c.value())), "client", "client-side");
        addValidArgument(c -> c.script().volume(StaticArgument.asFloat(c)), "volume");
        addValidArgument(c -> c.script().pitch(StaticArgument.asFloat(c)), "pitch");
        addValidArgument(c -> c.script().category(StaticArgument.asEnum(c, SoundCategory.class)));
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    @Override
    public boolean validate(Context context) throws ReaderException {
        if (context.script().sound() == null) {
            error(context.effect(), context.type(), context.line(), "Sound script must have a sound name");
            return false;
        }
        return true;
    }

    public static class Context extends ReaderContext<PlaySound> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new PlaySound());
        }
    }
}
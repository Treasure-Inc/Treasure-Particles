package net.treasure.effect.script.sound.reader;

import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.sound.PlaySound;
import org.bukkit.SoundCategory;

public class SoundReader extends ScriptReader<SoundReaderContext, PlaySound> {

    public SoundReader() {
        addValidArgument(c -> c.script().sound(c.value()), "name");
        addValidArgument(c -> c.script().clientSide(Boolean.parseBoolean(c.value())), "clientside");
        addValidArgument(c -> c.script().volume(StaticArgument.asFloat(c)), "volume");
        addValidArgument(c -> c.script().pitch(StaticArgument.asFloat(c)), "pitch");
        addValidArgument(c -> c.script().category(StaticArgument.asEnum(c, SoundCategory.class)));
    }

    @Override
    public SoundReaderContext createContext(Effect effect, String type, String line) {
        return new SoundReaderContext(effect, type, line);
    }

    @Override
    public boolean validate(SoundReaderContext context) throws ReaderException {
        if (context.script().sound() == null) {
            error(context.effect(), context.type(), context.line(), "Sound script must have a sound name");
            return false;
        }
        return true;
    }
}
package net.treasure.effect.script.particle.reader.text;

import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.particle.reader.ParticleReader;
import net.treasure.effect.script.particle.style.TextParticle;

public class TextParticleReader extends ParticleReader<TextParticle> {

    public TextParticleReader() {
        super();

        addValidArgument(c -> c.script().stepX(StaticArgument.asInt(c)), "step-x");
        addValidArgument(c -> c.script().stepY(StaticArgument.asInt(c)), "step-y");
        addValidArgument(c -> c.script().scale(StaticArgument.asFloat(c)), "scale");
        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
        addValidArgument(c -> c.script().fontName(StaticArgument.asString(c)), "font");
        addValidArgument(c -> c.script().text(StaticArgument.asString(c)), "text");
    }

    @Override
    public boolean validate(ParticleReader.Context<TextParticle> context) throws ReaderException {
        super.validate(context);

        if (context.script().text() == null) {
            error(context.effect(), context.type(), context.line(), "You must define a 'text' value");
            return false;
        }

        context.script().initialize();
        return true;
    }

    @Override
    public Context createParticleReaderContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ParticleReader.Context<TextParticle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new TextParticle());
        }
    }
}
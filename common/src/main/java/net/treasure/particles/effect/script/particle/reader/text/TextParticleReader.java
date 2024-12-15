package net.treasure.particles.effect.script.particle.reader.text;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.particle.reader.ParticleReader;
import net.treasure.particles.effect.script.particle.style.text.TextParticle;

import java.awt.Font;

public class TextParticleReader extends ParticleReader<TextParticle> {

    public TextParticleReader() {
        super();

        addValidArgument(c -> c.script().stepX(StaticArgument.asInt(c, 1)), "step-x");
        addValidArgument(c -> c.script().stepY(StaticArgument.asInt(c, 1)), "step-y");
        addValidArgument(c -> c.script().scale(StaticArgument.asFloat(c)), "scale");
        addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
        addValidArgument(c -> c.script().fontName(StaticArgument.asString(c)), "font");
        addValidArgument(c -> c.script().text(StaticArgument.asString(c)), true, "text");

        addValidArgument(c -> c.script().rotateX(StaticArgument.asFloat(c, 0, 360)), "rotate-x");
        addValidArgument(c -> c.script().rotateY(StaticArgument.asFloat(c, 0, 360)), "rotate-y");
    }

    @Override
    public boolean validate(ParticleReader.Context<TextParticle> c) throws ReaderException {
        if (!super.validate(c)) return false;
        var script = c.script();

        var font = new Font(script.fontName(), Font.PLAIN, 16);
        if (font.canDisplayUpTo(script.text()) != script.text().length()) {
            error(c.effect(), c.type(), c.line(), "This text cannot be displayed properly with '" + script.fontName() + "' font");
            return false;
        }

        c.script().initialize();
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
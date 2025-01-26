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
        addValidArguments(this);
    }

    public static void addValidArguments(ParticleReader<? extends TextParticle> reader) {
        reader.addValidArgument(c -> c.script().stepX(StaticArgument.asInt(c, 1)), "step-x");
        reader.addValidArgument(c -> c.script().stepY(StaticArgument.asInt(c, 1)), "step-y");
        reader.addValidArgument(c -> c.script().scale(StaticArgument.asFloat(c)), "scale");
        reader.addValidArgument(c -> c.script().tickData(StaticArgument.asBoolean(c)), "tick-data", "tick");
        reader.addValidArgument(c -> c.script().vertical(StaticArgument.asBoolean(c)), "vertical");
        reader.addValidArgument(c -> c.script().fontName(StaticArgument.asString(c)), "font");
        reader.addValidArgument(c -> c.script().text(StaticArgument.asString(c)), true, "text");

        reader.addValidArgument(c -> c.script().rotateX(StaticArgument.asFloat(c, 0, 360)), "rotate-x");
        reader.addValidArgument(c -> c.script().rotateY(StaticArgument.asFloat(c, 0, 360)), "rotate-y");
    }

    public static boolean validateContext(ParticleReader<? extends TextParticle> reader, ParticleReader.Context<? extends TextParticle> c) throws ReaderException {
        var script = c.script();

        var font = new Font(script.fontName(), Font.PLAIN, 16);
        if (font.canDisplayUpTo(script.text()) != -1) {
            reader.error(c.effect(), c.type(), c.line(), "This text cannot be displayed properly with '" + script.fontName() + "' font");
            return false;
        }

        c.script().initialize();
        return true;
    }

    @Override
    public boolean validate(ParticleReader.Context<TextParticle> c) throws ReaderException {
        if (!super.validate(c)) return false;
        return validateContext(this, c);
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
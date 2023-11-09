package net.treasure.particles.effect.script.conditional.reader;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.constants.Patterns;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.conditional.ConditionalScript;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;

import java.util.regex.Matcher;

public class ConditionalScriptReader extends ScriptReader<ReaderContext<ConditionalScript>, ConditionalScript> {

    @Override
    public ConditionalScript read(Effect effect, String type, String line) throws ReaderException {
        var manager = TreasureParticles.getEffectManager();

        Matcher matcher = Patterns.CONDITIONAL.matcher(line);
        if (matcher.matches()) {
            try {
                var condition = matcher.group("condition");
                var parent = new ConditionReader().read(effect, type, condition);
                if (parent == null)
                    error(effect, type, line, matcher.start("condition"), matcher.end("condition"), "Invalid condition: " + condition);

                var first = matcher.group("first");
                var firstExpr = manager.readLine(effect, first);

                var second = matcher.group("second");
                var secondExpr = manager.readLine(effect, second);

                if (firstExpr == null)
                    error(effect, type, line, matcher.start("first"), matcher.end("first"), "First expression is null: " + first);

                if (secondExpr == null)
                    error(effect, type, line, matcher.start("second"), matcher.end("second"), "Second expression is null: " + second);

                if (firstExpr == null || secondExpr == null)
                    throw new ReaderException("Invalid expression");

                return new ConditionalScript(
                        parent,
                        firstExpr,
                        secondExpr
                );
            } catch (ReaderException e) {
                if (e.getMessage() != null)
                    error(effect, line, e.getMessage());
                throw new ReaderException();
            }
        }
        error(effect, type, line, "Incorrect conditional script usage");
        return null;
    }
}
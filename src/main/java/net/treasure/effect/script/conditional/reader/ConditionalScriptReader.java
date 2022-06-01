package net.treasure.effect.script.conditional.reader;

import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.conditional.ConditionalScript;

import java.util.regex.Matcher;

public class ConditionalScriptReader implements ScriptReader<ConditionalScript> {

    @Override
    public ConditionalScript read(Effect effect, String line) {
        var inst = TreasurePlugin.getInstance();
        var manager = inst.getEffectManager();

        Matcher matcher = Patterns.CONDITIONAL.matcher(line);
        if (matcher.matches()) {
            try {
                var condition = matcher.group("condition");
                var parent = new ConditionReader(inst).read(effect, condition);
                if (parent == null)
                    return null;

                var first = matcher.group("first");
                var firstExpr = manager.readLine(effect, first);

                var second = matcher.group("second");
                var secondExpr = manager.readLine(effect, second);

                if (firstExpr == null)
                    error(line, "First expression is null: " + first);

                if (secondExpr == null)
                    error(line, "Second expression is null: " + second);

                if (firstExpr == null || secondExpr == null)
                    throw new Exception();

                return new ConditionalScript(
                        parent,
                        firstExpr,
                        secondExpr
                );
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
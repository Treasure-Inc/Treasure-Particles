package net.treasure.effect.script.variable.reader;

import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.variable.Variable;

public class VariableReader extends ScriptReader<ReaderContext<?>, Variable> {

    @Override
    public Variable read(Effect effect, String type, String line) throws ReaderException {
        var evalMatcher = Patterns.EVAL.matcher(line);

        if (evalMatcher.matches()) {
            var variable = evalMatcher.group(1);
            int start = evalMatcher.start(), end = evalMatcher.end();

            if (!effect.hasVariable(variable)) {
                error(effect, type, line, start, end, (effect.checkPredefinedVariable(variable) ? "Unknown variable" : "You cannot edit pre-defined variables") + " (" + variable + ")");
                return null;
            }

            var operator = evalMatcher.group(2);
            var eval = evalMatcher.group(3);

            start = evalMatcher.start();
            end = evalMatcher.end();

            var builder = Variable.builder();
            builder.variable(variable);
            builder.eval(eval);
            switch (operator) {
                case "" -> builder.operator(Variable.Operator.EQUAL);
                case "+" -> builder.operator(Variable.Operator.ADD);
                case "-" -> builder.operator(Variable.Operator.SUBTRACT);
                case "*" -> builder.operator(Variable.Operator.MULTIPLY);
                case "/" -> builder.operator(Variable.Operator.DIVIDE);
                default -> {
                    error(effect, type, line, start, end, "Invalid operator (" + operator + ")");
                    return null;
                }
            }
            return builder.build();
        }
        return null;
    }
}
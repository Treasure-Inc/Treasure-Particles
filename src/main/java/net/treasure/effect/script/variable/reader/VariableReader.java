package net.treasure.effect.script.variable.reader;

import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.variable.Variable;

import java.util.regex.Matcher;

public class VariableReader implements ScriptReader<Variable> {

    @Override
    public Variable read(Effect effect, String line) {
        Matcher evalMatcher = Patterns.EVAL.matcher(line);

        if (evalMatcher.matches()) {
            var variable = evalMatcher.group(1);
            if (effect.hasVariable(variable)) {
                var operator = evalMatcher.group(2);
                var eval = evalMatcher.group(3);

                Variable.VariableBuilder builder = Variable.builder();
                builder.variable(variable);
                builder.eval(eval);
                switch (operator) {
                    case "" -> builder.operator(Variable.Operator.EQUAL);
                    case "+" -> builder.operator(Variable.Operator.PLUS);
                    case "-" -> builder.operator(Variable.Operator.MINUS);
                    case "*" -> builder.operator(Variable.Operator.MULTIPLY);
                    case "/" -> builder.operator(Variable.Operator.DIVISION);
                    default -> {
                        error(line, "Invalid operator (" + operator + ")");
                        return null;
                    }
                }
                return builder.build();
            }
        }
        return null;
    }
}
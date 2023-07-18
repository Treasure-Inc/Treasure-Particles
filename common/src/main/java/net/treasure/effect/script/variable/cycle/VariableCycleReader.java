package net.treasure.effect.script.variable.cycle;

import net.treasure.effect.Effect;
import net.treasure.effect.script.argument.type.DoubleArgument;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.reader.ScriptReader;

public class VariableCycleReader extends ScriptReader<VariableCycleReader.Context, VariableCycle> {

    public VariableCycleReader() {
        addValidArgument(c -> {
            var variable = c.value();
            if (!c.effect().hasVariable(variable)) {
                error(c, (!c.effect().isPredefinedVariable(variable) ? "Unknown variable" : "You cannot edit pre-defined variables") + ": " + variable);
                return;
            }
            c.script().variable = variable;
        }, "variable");

        addValidArgument(c -> c.script().operator = StaticArgument.asEnum(c, VariableCycle.Operator.class), "operator");

        addValidArgument(c -> c.script().step = DoubleArgument.read(c), "step");
        addValidArgument(c -> c.script().min = DoubleArgument.read(c), "min");
        addValidArgument(c -> c.script().max = DoubleArgument.read(c), "max");

        addValidArgument(c -> c.script().revertWhenDone = StaticArgument.asBoolean(c), "revert");
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    public static class Context extends ReaderContext<VariableCycle> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new VariableCycle());
        }
    }
}
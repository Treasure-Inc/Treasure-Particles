package net.treasure.effect.script.argument.type;

import lombok.AllArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.argument.ScriptArgument;
import net.treasure.effect.script.variable.Variable;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BooleanArgument implements ScriptArgument<Boolean> {

    Object value;

    public static BooleanArgument read(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        if (arg.equals("true") || arg.equals("false"))
            return new BooleanArgument(Boolean.parseBoolean(arg)).validate(context);
        else
            return new BooleanArgument(arg).validate(context);
    }

    @Override
    public Boolean get(Player player, EffectData data) {
        if (value == null) return null;
        else if (value instanceof Boolean b) return b;
        else if (value instanceof String s) {
            var i = Integer.parseInt(data.replaceVariables(s));
            return i != 1 && i != 0 ? null : (i == 1);
        } else return null;
    }

    @Override
    public BooleanArgument validate(ReaderContext<?> context) throws ReaderException {
        var arg = context.value();
        if (arg.equals("true") || arg.equals("false") || context.effect().hasVariable(Variable.replace(arg)))
            return this;
        else
            throw new ReaderException("Valid values for Boolean argument: true, false, {variable}");
    }
}
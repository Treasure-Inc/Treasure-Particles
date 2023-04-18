package net.treasure.effect.script.variable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Cached;
import net.treasure.effect.script.Script;
import net.treasure.util.math.MathUtils;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Variable extends Script implements Cached {

    public static final String I = "i";
    public static final String TIMES = "TIMES";

    @Setter
    protected int index;
    protected final String variable;
    protected final Operator operator;
    protected final String eval;

    @Override
    public TickResult tick(Player player, EffectData data, int times) {
        var pair = data.getVariable(variable);
        if (pair == null) return TickResult.NORMAL;
        var effect = data.getCurrentEffect();
        if (effect.isEnableCaching()) {
            pair.setValue(effect.getCache().get(tickHandler.key())[times][index]);
            return TickResult.NORMAL;
        }
        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(eval));
        } catch (Exception e) {
            TreasurePlugin.logger().warning("Invalid evaluation: " + eval);
            return TickResult.NORMAL;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case ADD -> pair.setValue(pair.getValue() + val);
            case SUBTRACT -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVIDE -> pair.setValue(pair.getValue() / val);
        }
        return TickResult.NORMAL;
    }

    public void preTick(Effect effect, EffectData data, int times) {
        var pair = data.getVariable(variable);
        if (pair == null) return;

        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(eval));
        } catch (Exception e) {
            TreasurePlugin.logger().warning("Invalid evaluation: " + eval);
            return;
        }

        pair.setValue(switch (operator) {
            case EQUAL -> val;
            case ADD -> pair.getValue() + val;
            case SUBTRACT -> pair.getValue() - val;
            case MULTIPLY -> pair.getValue() * val;
            case DIVIDE -> pair.getValue() / val;
        });
        effect.getCache().get(tickHandler.key())[times][index] = pair.getValue();
    }

    @Override
    public Variable clone() {
        return new Variable(index, variable, operator, eval);
    }

    public enum Operator {
        EQUAL,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }

    public static String replace(String variable) {
        return variable.replaceAll("\\{|}", "");
    }
}
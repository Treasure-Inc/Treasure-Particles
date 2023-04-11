package net.treasure.effect.script.variable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.treasure.util.Pair;
import net.treasure.util.math.MathUtils;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
public class Variable extends Script {

    public static final String I = "i";
    public static final String TIMES = "TIMES";

    @Setter
    private int index;
    private String variable;
    private Operator operator;
    private String eval;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        var pair = data.getVariable(variable);
        if (pair == null) return TickResult.NORMAL;
        var effect = data.getCurrentEffect();
        if (effect.isEnableCaching()) {
            pair.setValue(effect.getCache().get(tickHandlerKey)[times][index]);
            return TickResult.NORMAL;
        }
        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(eval));
        } catch (Exception e) {
            e.printStackTrace();
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

    public double preTick(Set<Pair<String, Double>> variables) {
        Pair<String, Double> pair = null;
        for (var var : variables)
            if (var.getKey().equals(variable)) {
                pair = var;
                break;
            }
        if (pair == null) return 0;
        double val;
        try {
            var data = new EffectData(null, variables);
            val = MathUtils.eval(data.replaceVariables(eval));
        } catch (Exception e) {
            TreasurePlugin.logger().warning("Invalid evaluation: " + eval);
            return 0;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case ADD -> pair.setValue(pair.getValue() + val);
            case SUBTRACT -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVIDE -> pair.setValue(pair.getValue() / val);
        }
        return pair.getValue();
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
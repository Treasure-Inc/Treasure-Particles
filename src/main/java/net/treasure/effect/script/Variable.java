package net.treasure.effect.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.effect.Effect;
import net.treasure.effect.player.EffectData;
import net.treasure.util.MathUtil;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

import java.util.Set;

@Builder
@AllArgsConstructor
public class Variable extends Script {

    @Getter
    private int index;
    private String variable;
    private Operator operator;
    private String eval;

    @Override
    public void tick(Player player, EffectData data, int times) {
        Pair<String, Double> pair = data.getVariable(variable);
        if (pair == null) return;
        Effect effect = data.getCurrentEffect();
        if (effect.isEnableCaching()) {
            pair.setValue(effect.getCache()[times][index]);
            return;
        }
        double val;
        try {
            String _eval = eval
                    .replaceAll("\\{TICK}", String.valueOf(TimeKeeper.getTimeElapsed()))
                    .replaceAll("\\{PI}", String.valueOf(Math.PI));
            if (_eval.contains("{")) {
                for (Pair<String, Double> p : data.getVariables()) {
                    if (_eval.contains("{" + p.getKey() + "}")) {
                        _eval = _eval.replaceAll("\\{" + p.getKey() + "}", String.valueOf(p.getValue()));
                    }
                }
            }
            val = MathUtil.eval(_eval);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case PLUS -> pair.setValue(pair.getValue() + val);
            case MINUS -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVISION -> pair.setValue(pair.getValue() / val);
        }
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
            String _eval = eval
                    .replaceAll("\\{TICK}", String.valueOf(TimeKeeper.getTimeElapsed()))
                    .replaceAll("\\{PI}", String.valueOf(Math.PI));
            if (_eval.contains("{")) {
                for (Pair<String, Double> p : variables) {
                    if (_eval.contains("{" + p.getKey() + "}")) {
                        _eval = _eval.replaceAll("\\{" + p.getKey() + "}", String.valueOf(p.getValue()));
                    }
                }
            }
            val = MathUtil.eval(_eval);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case PLUS -> pair.setValue(pair.getValue() + val);
            case MINUS -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVISION -> pair.setValue(pair.getValue() / val);
        }
        return pair.getValue();
    }

    @Override
    public Variable clone() {
        return new Variable(index, variable, operator, eval);
    }

    public enum Operator {
        EQUAL,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVISION;

        public static Operator toOperator(String s) {
            return switch (s) {
                case "+" -> EQUAL;
                case "+=" -> PLUS;
                case "-=" -> MINUS;
                case "*=" -> MULTIPLY;
                case "/=" -> DIVISION;
                default -> null;
            };
        }
    }
}
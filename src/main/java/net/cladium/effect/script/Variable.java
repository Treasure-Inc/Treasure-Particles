package net.cladium.effect.script;

import lombok.Builder;
import net.cladium.effect.player.EffectData;
import net.cladium.util.MathUtil;
import net.cladium.util.Pair;
import net.cladium.util.TimeKeeper;
import org.bukkit.entity.Player;

@Builder
public class Variable extends Script {

    private String variable;
    private Operator operator;
    private String eval;

    @Override
    public void tick(Player player, EffectData data) {
        Pair<String, Double> pair = data.getVariable(variable);
        if (pair == null) return;
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
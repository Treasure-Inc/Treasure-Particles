package net.treasure.effect.script.conditional;

import lombok.Getter;
import lombok.ToString;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.util.math.MathUtils;
import org.bukkit.entity.Player;

@Getter
@ToString
public class Condition implements Predicate {

    String variable;
    Operator operator;
    double value;
    boolean hasEquation, defaultValue;

    public Condition(String variable, Operator operator, double value, boolean hasEquation) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
        this.hasEquation = hasEquation;
    }

    public Condition(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean test(Player player, Effect effect, EffectData data) {
        if (data == null)
            return defaultValue;
        double current;
        if (hasEquation) {
            current = MathUtils.eval(data.replaceVariables(effect, variable));
        } else current = data.getVariable(effect, variable).y();
        return switch (operator) {
            case EQUAL -> current == value;
            case NOT_EQUAL -> current != value;
            case GREATER_THAN -> current > value;
            case GREATER_THAN_OR_EQUAL -> current >= value;
            case LESS_THAN -> current < value;
            case LESS_THAN_OR_EQUAL -> current <= value;
        };
    }

    public enum Operator {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL;
    }
}
package net.treasure.effect.script.conditional;

import lombok.Getter;
import lombok.ToString;
import net.treasure.effect.player.EffectData;

@Getter
@ToString
public class Condition implements Predicate {

    String variable;
    Operator operator;
    double value;
    boolean defaultValue;

    public Condition(String variable, Operator operator, double value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    public Condition(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean test(EffectData data) {
        if (data == null)
            return defaultValue;
        var current = data.getVariable(variable).getValue();
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

        public static Operator toOperator(String s) {
            return switch (s) {
                case "==" -> EQUAL;
                case ">" -> GREATER_THAN;
                case ">=" -> GREATER_THAN_OR_EQUAL;
                case "<" -> LESS_THAN;
                case "<=" -> LESS_THAN_OR_EQUAL;
                default -> null;
            };
        }
    }
}
package net.treasure.effect.script.conditional;

import lombok.Data;
import net.treasure.effect.player.EffectData;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConditionGroup implements Predicate {

    public boolean multiGroup, hasParent;
    public List<Condition> conditions = new ArrayList<>();
    public List<Operator> operators = new ArrayList<>();
    public List<ConditionGroup> inner = new ArrayList<>();
    public transient ConditionGroup parent;

    @Override
    public boolean test(EffectData data) {
        boolean result = false;
        Result last = Result.NONE;

        List<? extends Predicate> predicates = inner.isEmpty() ? conditions : inner;
        var size = predicates.size();

        for (int i = 0; i < size; i++) {
            var predicate = predicates.get(i);
            if (last.equals(Result.NONE)) {
                last = predicate.test(data) ? Result.SUCCESS : Result.FAIL;
                if (size == 1)
                    result = last.success();
                continue;
            }
            var operator = operators.get(i - 1);
            var current = predicate.test(data) ? Result.SUCCESS : Result.FAIL;
            var success = switch (operator) {
                case AND -> last.success() && current.success();
                case OR -> last.success() || current.success();
            };
            last = predicate.test(data) ? Result.SUCCESS : Result.FAIL;
            result = success;
        }
        return result;
    }

    public enum Operator {
        AND,
        OR
    }

    public enum Result {
        NONE,
        SUCCESS,
        FAIL;

        boolean success() {
            return this.equals(SUCCESS);
        }
    }
}
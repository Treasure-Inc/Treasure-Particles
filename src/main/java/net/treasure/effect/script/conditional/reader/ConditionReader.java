package net.treasure.effect.script.conditional.reader;

import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.script.conditional.Condition;
import net.treasure.effect.script.conditional.ConditionGroup;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ConditionReader {

    TreasurePlugin plugin;

    public List<ConditionGroup> read(String line) {
        List<ConditionGroup> groups = new ArrayList<>();

        ConditionGroup current = null, last = null;
        char lastChar = ' ';

        // Condition Group Stuffs
        ConditionGroup.Operator operator = null; // AND, OR
        boolean foundOperator = false; // For single groups

        // Condition Stuffs
        Condition.Operator variableOperator = null;
        StringBuilder variable = new StringBuilder();
        double value;
        int valuePos = -1;

        var array = line.toCharArray();
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];

            // Check condition group operator
            if (operator != null && !(c == '&' || c == '|')) {
                plugin.getLogger().warning("Couldn't find second operator char");
                return null;
            }

            // Check condition operator (greater,less -single ones)
            if ((lastChar == '>' || lastChar == '<') && c != '=')
                variableOperator = lastChar == '>' ? Condition.Operator.GREATER_THAN : Condition.Operator.LESS_THAN;

            // Check condition value
            if (valuePos != -1 && !((c >= '0' && c <= '9') || c == '.' || c == '-')) {
                value = Double.parseDouble(line.substring(valuePos, pos));
                if (variable.isEmpty() || variableOperator == null) {
                    plugin.getLogger().warning("No variable or operator but value");
                    return null;
                }
                // Create a new condition group if current is null
                if (current == null)
                    current = new ConditionGroup();

                // Add condition to current
                current.conditions.add(new Condition(variable.toString(), variableOperator, value));

                // Reset condition stuffs
                valuePos = -1;
                variableOperator = null;
                variable = new StringBuilder();
            }

            if (Character.isAlphabetic(c)) // Check if char is alphabetic (condition variable)
                variable.append(c);
            else if ((c >= '0' && c <= '9') || c == '.' || c == '-') { // Check char is digit (condition value start position)
                if (valuePos == -1)
                    valuePos = pos;
            } else // others
                switch (c) {
                    case '(' -> {
                        if (current != null) {
                            if (last != null) {
                                current = new ConditionGroup();
                                current.multiGroup = true;
                                current.parent = last;
                                current.hasParent = true;
                                last.inner.add(current);
                                if (array[pos + 1] == '(')
                                    last = current;
                                break;
                            } else {
                                if (!current.conditions.isEmpty() && !current.operators.isEmpty()) { // Single condition group
                                    groups.add(current);
                                } else {
                                    plugin.getLogger().warning("Unexpected situation, please report this to ");
                                }
                            }
                        }
                        current = new ConditionGroup();
                        current.multiGroup = true;
                        if (last == null)
                            last = current;
                        else {
                            current.parent = last;
                            current.hasParent = true;
                            last.inner.add(current);
                        }
                    }
                    case ')' -> {
                        if (current == null) {
                            if (last == null) {
                                plugin.getLogger().warning("End bracket but current is null pos=" + pos);
                                return null;
                            }
                            foundOperator = true;
                            break;
                        }
                        if (last != null && !last.inner.isEmpty()) {
                            last = current.parent;
                            current = null;
                            if (!groups.contains(last)) {
                                groups.add(last);
                            }
                            break;
                        }

                        groups.add(current);
                        last = current;
                        current = null;
                    }
                    case '&' -> {
                        if (operator != null) {
                            if (!operator.equals(ConditionGroup.Operator.AND)) {
                                plugin.getLogger().warning("Different Operator");
                                return null;
                            }

                            if (current == null && foundOperator) {
                                foundOperator = false;
                                last.operators.add(operator);
                                last = null;
                                operator = null;
                                break;
                            }

                            if (current == null) {
                                if (last == null) {
                                    plugin.getLogger().warning("Current is null (AND)");
                                    return null;
                                }

                                last.operators.add(ConditionGroup.Operator.AND);
                                operator = null;
                                break;
                            }

                            current.operators.add(ConditionGroup.Operator.AND);
                            operator = null;
                        } else
                            operator = ConditionGroup.Operator.AND;
                    }
                    case '|' -> {
                        if (operator != null) {
                            if (!operator.equals(ConditionGroup.Operator.OR)) {
                                plugin.getLogger().warning("Different Operator");
                                return null;
                            }

                            if (current == null && foundOperator) {
                                foundOperator = false;
                                last.operators.add(operator);
                                last = null;
                                operator = null;
                                break;
                            }

                            if (current == null) {
                                if (last == null) {
                                    plugin.getLogger().warning("Current is null (OR)");
                                    return null;
                                }

                                last.operators.add(ConditionGroup.Operator.OR);
                                operator = null;
                                break;
                            }

                            current.operators.add(ConditionGroup.Operator.OR);
                            operator = null;
                        } else
                            operator = ConditionGroup.Operator.OR;
                    }
                    case '=' -> {
                        if (variable.isEmpty()) {
                            plugin.getLogger().warning("No variable but operator");
                            return null;
                        }
                        variableOperator = switch (lastChar) {
                            case '=' -> Condition.Operator.EQUAL;
                            case '!' -> Condition.Operator.NOT_EQUAL;
                            case '>' -> Condition.Operator.GREATER_THAN_OR_EQUAL;
                            case '<' -> Condition.Operator.LESS_THAN_OR_EQUAL;
                            default -> null;
                        };
                    }
                }
            lastChar = c;
        }
        return groups;
    }
}

package net.treasure.particles.effect.script.conditional.reader;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.conditional.Condition;
import net.treasure.particles.effect.script.conditional.ConditionGroup;
import net.treasure.particles.effect.script.reader.DefaultReader;
import net.treasure.particles.util.logging.ComponentLogger;

@AllArgsConstructor
public class ConditionReader extends DefaultReader<ConditionGroup> {

    @Override
    public ConditionGroup read(Effect effect, String type, String line) {
        ConditionGroup parent = null;

        ConditionGroup current = null, last = null;
        char lastChar = ' ';

        // Condition Group Stuffs
        ConditionGroup.Operator operator = null; // AND, OR

        // Condition Stuffs
        Condition.Operator variableOperator = null;
        StringBuilder variable = new StringBuilder();
        double value;
        int valuePos = -1;
        boolean hasEquation = false;

        var array = line.toCharArray();
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];

            // Check condition group operator
            if (operator != null && !(c == '&' || c == '|')) {
                ComponentLogger.error(effect, type, line, pos, pos + 1, "Couldn't find second operator char");
                return null;
            }

            // Check condition operator (greater,less -single ones)
            if ((lastChar == '>' || lastChar == '<') && c != '=')
                variableOperator = lastChar == '>' ? Condition.Operator.GREATER_THAN : Condition.Operator.LESS_THAN;

            // Check condition value
            if (valuePos != -1 && !((c >= '0' && c <= '9') || c == '.' || c == '-')) {
                value = Double.parseDouble(line.substring(valuePos, pos));
                if (variable.isEmpty() || variableOperator == null) {
                    ComponentLogger.error(effect, type, line, pos, pos + 1, "No variable or operator but value");
                    return null;
                }
                // Create a new condition group if current is null
                if (current == null)
                    current = new ConditionGroup();

                // Add condition to current
                current.conditions.add(new Condition(variable.toString(), variableOperator, value, hasEquation));

                // Reset condition stuffs
                valuePos = -1;
                variableOperator = null;
                variable = new StringBuilder();
                hasEquation = false;
            }

            if (variableOperator == null && c != '(' && c != ')' && c != '<' && c != '>' && c != '!' && c != '=' && c != '&' && c != '|' && c != ' ') { // Check if char is alphabetic (condition variable)
                variable.append(c);
                if (!Character.isAlphabetic(c))
                    hasEquation = true;
            } else if (variableOperator != null && ((c >= '0' && c <= '9') || c == '.' || c == '-')) { // Check char is digit (condition value start position)
                if (valuePos == -1)
                    valuePos = pos;
            } else // others
                switch (c) {
                    case '(' -> {
                        if (parent == null) {
                            parent = new ConditionGroup();
                            parent.multiGroup = true;
                            last = parent;
                            //System.out.println("Parent ID: " + parent.uuid);
                            break;
                        }
                        if (current != null) {
                            if (last != null) {
                                current = new ConditionGroup();
                                //System.out.println("-------------------------");
                                //System.out.println("Added " + current.uuid + " to inner " + pos);
                                current.multiGroup = true;
                                current.parent = last;
                                //current.parentId = last.uuid;
                                //System.out.println("Set current parent to " + current.parentId);
                                last.inner.add(current);
                                if (array[pos + 1] == '(') {
                                    last = current;
                                    //System.out.println("Set last to " + last.uuid);
                                }
                                break;
                            } else {
                                if (!current.conditions.isEmpty() && !current.operators.isEmpty()) { // Single condition group
                                    parent.inner.add(current);
                                } else {
                                    ComponentLogger.error(effect, "Unexpected situation, please report this");
                                }
                            }
                        }
                        current = new ConditionGroup();
                        current.multiGroup = true;
                        if (last == null)
                            last = current;
                        else {
                            //System.out.println("-------------------------");
                            //System.out.println("Added " + current.uuid + " to inner " + pos);
                            current.parent = last;
                            //current.parentId = last.uuid;
                            //System.out.println("Set current's parent to " + current.parentId);
                            last.inner.add(current);
                            if (array[pos + 1] == '(') {
                                last = current;
                                //System.out.println("Set last to " + last.uuid);
                            }
                        }
                    }
                    case ')' -> {
                        if (current == null) {
                            if (last == null) {
                                ComponentLogger.error(effect, type, line, pos, pos + 1, "End bracket but current is null");
                                return null;
                            }
                            if (last.parent != null) {
                                last = last.parent;
                                //System.out.println("Changed last to " + last.uuid);
                            }
                            break;
                        }
                        if (last != null && !last.inner.isEmpty()) {
                            //System.out.println("End " + current.uuid + " " + pos);
                            last = current.parent;
                            //System.out.println("Set last to " + last.uuid);
                            current = null;
                            //System.out.println("-------------------------");
                            break;
                        }

                        parent.inner.add(current);
                        last = current;
                        current = null;
                    }
                    case '&' -> {
                        if (operator != null) {
                            if (operator != ConditionGroup.Operator.AND) {
                                ComponentLogger.error(effect, type, line, pos, pos + 1, "Different operators (Possible solution: &&)");
                                return null;
                            }

                            if (current == null) {
                                if (last == null) {
                                    ComponentLogger.error(effect, type, line, pos, pos + 1, "Current is null (AND)");
                                    return null;
                                }

                                last.operators.add(ConditionGroup.Operator.AND);
                                //System.out.println("Add AND operator to " + last.uuid);
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
                            if (operator != ConditionGroup.Operator.OR) {
                                ComponentLogger.error(effect, type, line, pos, pos + 1, "Different operators (Possible solution: ||)");
                                return null;
                            }

                            if (current == null) {
                                if (last == null) {
                                    ComponentLogger.error(effect, type, line, pos, pos + 1, "Current is null (OR)");
                                    return null;
                                }

                                last.operators.add(ConditionGroup.Operator.OR);
                                //System.out.println("Add OR operator to " + last.uuid);
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
                            ComponentLogger.error(effect, type, line, pos, pos + 1, "No variable but operator");
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
        return parent;
    }
}
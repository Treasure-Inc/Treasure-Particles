package net.treasure.particles.effect.script.variable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Cached;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.math.MathUtils;

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
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        var pair = data.getVariable(effect, variable);
        if (pair == null) return TickResult.NORMAL;
        if (effect.isCachingEnabled()) {
            pair.setValue(effect.getCache().get(tickHandler.key)[times][index]);
            return TickResult.NORMAL;
        }
        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(effect, eval));
        } catch (Exception e) {
            e.printStackTrace();
            ComponentLogger.log("Invalid evaluation: " + eval);
            return TickResult.NORMAL;
        }
        switch (operator) {
            case SET -> pair.setValue(val);
            case ADD -> pair.setValue(pair.getValue() + val);
            case SUBTRACT -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVIDE -> pair.setValue(pair.getValue() / val);
        }
        return TickResult.NORMAL;
    }

    public void preTick(Effect effect, EffectData data, int times) {
        var pair = data.getVariable(effect, variable);
        if (pair == null) return;

        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(effect, eval));
        } catch (Exception e) {
            ComponentLogger.log("Invalid evaluation: " + eval);
            return;
        }

        pair.setValue(switch (operator) {
            case SET -> val;
            case ADD -> pair.getValue() + val;
            case SUBTRACT -> pair.getValue() - val;
            case MULTIPLY -> pair.getValue() * val;
            case DIVIDE -> pair.getValue() / val;
        });
        effect.getCache().get(tickHandler.key)[times][index] = pair.getValue();
    }

    @Override
    public Variable clone() {
        return new Variable(index, variable, operator, eval);
    }

    public enum Operator {
        SET,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }

    public static String replace(String variable) {
        return variable.replaceAll("[{}]", "");
    }
}
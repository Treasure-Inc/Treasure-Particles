package net.treasure.effect.script.variable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.treasure.TreasureParticles;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
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
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        var pair = data.getVariable(effect, variable);
        if (pair == null) return TickResult.NORMAL;
        if (effect.isCachingEnabled()) {
            pair.y(effect.getCache().get(tickHandler.key)[times][index]);
            return TickResult.NORMAL;
        }
        double val;
        try {
            val = MathUtils.eval(data.replaceVariables(effect, eval));
        } catch (Exception e) {
            e.printStackTrace();
            TreasureParticles.logger().warning("Invalid evaluation: " + eval);
            return TickResult.NORMAL;
        }
        switch (operator) {
            case EQUAL -> pair.y(val);
            case ADD -> pair.y(pair.y() + val);
            case SUBTRACT -> pair.y(pair.y() - val);
            case MULTIPLY -> pair.y(pair.y() * val);
            case DIVIDE -> pair.y(pair.y() / val);
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
            TreasureParticles.logger().warning("Invalid evaluation: " + eval);
            return;
        }

        pair.y(switch (operator) {
            case EQUAL -> val;
            case ADD -> pair.y() + val;
            case SUBTRACT -> pair.y() - val;
            case MULTIPLY -> pair.y() * val;
            case DIVIDE -> pair.y() / val;
        });
        effect.getCache().get(tickHandler.key)[times][index] = pair.y();
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
        return variable.replaceAll("[{}]", "");
    }
}
package net.treasure.effect.player;

import lombok.Getter;
import lombok.Setter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.script.Script;
import net.treasure.util.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class EffectData {

    private Effect currentEffect;

    @Setter
    private boolean enabled = false, effectsEnabled = true, debugModeEnabled;

    private final Set<Pair<String, Double>> variables;

    @Setter
    private List<Script> lines, postLines;

    @Setter
    private long startedGliding;

    public EffectData(Set<Pair<String, Double>> variables) {
        this.variables = variables;
    }

    public EffectData() {
        this.variables = new HashSet<>();
        this.lines = new ArrayList<>();
        this.postLines = new ArrayList<>();
    }

    public void setCurrentEffect(Player player, Effect currentEffect) {
        this.currentEffect = currentEffect;
        this.variables.clear();
        this.lines.clear();
        this.postLines.clear();
        this.debugModeEnabled = player.isOp() && TreasurePlugin.getInstance().isDebugModeEnabled();
        if (this.currentEffect != null)
            this.currentEffect.initialize(player, this);
    }

    public Pair<String, Double> getVariable(String variable) {
        if (variable == null)
            return null;
        for (var pair : variables)
            if (pair.getKey().equals(variable))
                return pair;
        return null;
    }
}
package net.cladium.effect.player;

import lombok.Getter;
import lombok.Setter;
import net.cladium.effect.Effect;
import net.cladium.effect.script.Script;
import net.cladium.util.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EffectData {

    @Getter
    private Effect currentEffect;

    @Getter
    @Setter
    private boolean enabled = false;

    @Getter
    private final Set<Pair<String, Double>> variables;

    @Getter
    private final List<Script> lines, postLines;

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
        if (this.currentEffect != null)
            this.currentEffect.initialize(player, this);
    }

    public Pair<String, Double> getVariable(String variable) {
        if (variable == null)
            return null;
        return variables.stream().filter(pair -> pair.getKey().equals(variable)).findFirst().orElse(null);
    }
}
package net.treasure.particles.effect.data;

import net.treasure.particles.util.tuples.Triplet;
import org.bukkit.Location;

import java.util.List;

public class EmptyEffectData extends EffectData {

    public EmptyEffectData(List<Triplet<String, Double, String>> variables) {
        this.variables = variables;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public Double getVariable(String variable) {
        return null;
    }
}
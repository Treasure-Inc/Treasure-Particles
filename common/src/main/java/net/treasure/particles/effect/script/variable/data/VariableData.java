package net.treasure.particles.effect.script.variable.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class VariableData {

    private String effect, name;
    @Setter
    private double value;

    public VariableData clone() {
        return new VariableData(effect, name, value);
    }
}
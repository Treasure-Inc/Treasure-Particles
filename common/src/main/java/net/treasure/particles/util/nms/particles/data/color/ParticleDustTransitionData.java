package net.treasure.particles.util.nms.particles.data.color;

import lombok.Getter;
import org.bukkit.Color;

@Getter
public abstract class ParticleDustTransitionData extends ParticleDustData {
    protected float transitionRed, transitionGreen, transitionBlue;

    public ParticleDustTransitionData(Color color, Color transition) {
        this(color, transition, 1f);
    }

    public ParticleDustTransitionData(Color color, Color transition, float size) {
        super(color, size);
        this.transitionRed = transition.getRed() / 255f;
        this.transitionGreen = transition.getGreen() / 255f;
        this.transitionBlue = transition.getBlue() / 255f;
    }
}
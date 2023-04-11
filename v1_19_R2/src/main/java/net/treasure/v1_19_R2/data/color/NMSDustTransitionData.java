package net.treasure.v1_19_R2.data.color;

import net.minecraft.core.particles.DustColorTransitionOptions;
import net.treasure.common.particles.data.color.ParticleDustTransitionData;
import org.bukkit.Color;
import org.joml.Vector3f;

public class NMSDustTransitionData extends ParticleDustTransitionData {

    public NMSDustTransitionData(Color color, Color transition, float size) {
        super(color, transition, size);
    }

    @Override
    public Object toNMS() {
        return new DustColorTransitionOptions(new Vector3f(red, green, blue), new Vector3f(transitionRed, transitionGreen, transitionBlue), size);
    }
}
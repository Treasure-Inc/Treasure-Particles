package net.treasure.v1_16_R1.data.color;

import net.treasure.common.particles.data.color.ParticleDustTransitionData;
import org.bukkit.Color;

public class NMSDustTransitionData extends ParticleDustTransitionData {

    public NMSDustTransitionData(Color color, Color transition, float size) {
        super(color, transition, size);
    }

    @Override
    public Object toNMS() {
        return null;
    }
}
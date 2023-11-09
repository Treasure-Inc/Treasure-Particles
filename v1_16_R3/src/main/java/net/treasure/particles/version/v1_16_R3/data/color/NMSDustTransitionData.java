package net.treasure.particles.version.v1_16_R3.data.color;

import net.treasure.particles.util.nms.particles.data.color.ParticleDustTransitionData;
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
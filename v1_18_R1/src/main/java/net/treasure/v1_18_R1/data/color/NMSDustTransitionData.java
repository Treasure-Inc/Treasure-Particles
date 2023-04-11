package net.treasure.v1_18_R1.data.color;

import com.mojang.math.Vector3fa;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.treasure.common.particles.data.color.ParticleDustTransitionData;
import org.bukkit.Color;

public class NMSDustTransitionData extends ParticleDustTransitionData {

    public NMSDustTransitionData(Color color, Color transition, float size) {
        super(color, transition, size);
    }

    @Override
    public Object toNMS() {
        return new DustColorTransitionOptions(new Vector3fa(red, green, blue), new Vector3fa(transitionRed, transitionGreen, transitionBlue), size);
    }
}
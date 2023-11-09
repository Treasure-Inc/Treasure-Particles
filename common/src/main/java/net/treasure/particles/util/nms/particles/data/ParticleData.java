package net.treasure.particles.util.nms.particles.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ParticleData {
    public abstract Object toNMS();
}
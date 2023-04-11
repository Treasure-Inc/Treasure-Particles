package net.treasure.util.particles;

import net.treasure.common.NMSHandler;
import net.treasure.common.particles.ParticleBuilder;

import java.util.List;

public class Particles {

    public static final NMSHandler NMS = new NMSMatcher().match();

    public static void send(ParticleBuilder builder) {
        NMS.sendParticle(builder);
    }

    public static void send(List<ParticleBuilder> builders) {
        NMS.sendParticles(builders);
    }
}
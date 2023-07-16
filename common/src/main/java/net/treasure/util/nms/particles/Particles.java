package net.treasure.util.nms.particles;

import net.treasure.util.nms.AbstractNMSHandler;
import net.treasure.util.nms.NMSMatcher;

import java.util.List;

public class Particles {

    public static final AbstractNMSHandler NMS = new NMSMatcher().match();

    public static void send(ParticleBuilder builder) {
        NMS.sendParticle(builder);
    }

    public static void send(List<ParticleBuilder> builders) {
        NMS.sendParticles(builders);
    }
}
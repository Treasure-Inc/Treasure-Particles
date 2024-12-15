package net.treasure.particles.util.nms.particles;

import net.treasure.particles.util.nms.AbstractNMSHandler;
import net.treasure.particles.util.nms.NMSMatcher;

import java.util.List;

public class Particles {

    public static AbstractNMSHandler NMS;

    public static boolean initialize() {
        NMS = NMSMatcher.match();
        return NMS != null;
    }

    public static void send(ParticleBuilder builder) {
        NMS.sendParticle(builder);
    }

    public static void send(List<ParticleBuilder> builders) {
        NMS.sendParticles(builders);
    }
}
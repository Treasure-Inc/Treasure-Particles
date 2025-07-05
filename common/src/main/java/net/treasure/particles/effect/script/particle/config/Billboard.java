package net.treasure.particles.effect.script.particle.config;

public enum Billboard {
    VERTICAL,
    HORIZONTAL,
    BOTH;

    public boolean x() {
        return this != VERTICAL;
    }

    public boolean y() {
        return this != HORIZONTAL;
    }
}
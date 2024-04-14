package net.treasure.particles.effect.script.particle.config;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public enum LocationOrigin {
    HEAD,
    FEET,
    WORLD;

    public Location getLocation(Entity entity) {
        return switch (this) {
            case HEAD -> entity instanceof LivingEntity livingEntity ? livingEntity.getEyeLocation() : entity.getLocation();
            case FEET -> entity.getLocation();
            case WORLD -> new Location(entity.getWorld(), 0, 0, 0);
        };
    }
}
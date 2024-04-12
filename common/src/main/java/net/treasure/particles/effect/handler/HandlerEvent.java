package net.treasure.particles.effect.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum HandlerEvent {
    STATIC("static", false, true),

    ELYTRA("elytra", false),

    MOVING("moving", false),

    STANDING("standing", false),

    SNEAKING("sneaking", false),

    PROJECTILE("projectile"),

    MOB_KILL("mob-kill"),

    MOB_DAMAGE("mob-damage"),

    PLAYER_KILL("player-kill"),

    PLAYER_DAMAGE("player-damage"),

    TAKE_DAMAGE("take-damage"),

    RIDE_VEHICLE("ride-vehicle");

    private final String translationKey;
    private final boolean isSpecial;
    private final boolean onlyStatic;

    HandlerEvent(String translationKey) {
        this(translationKey, true, false);
    }

    HandlerEvent(String translationKey, boolean isSpecial) {
        this(translationKey, isSpecial, false);
    }
}
package net.treasure.particles.effect.data;

import lombok.Getter;
import lombok.Setter;
import net.treasure.particles.constants.Keys;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.mix.MixData;
import net.treasure.particles.permission.Permissions;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PlayerEffectData extends EffectData {

    public final Player player;
    private boolean notificationsEnabled, effectsEnabled = true;

    private Vector lastVector;
    private int notMovingInterval;
    private boolean moving;

    // Last time elytra boosted with firework
    private long lastBoostMillis;

    // Target entity
    private Entity targetEntity;

    // Effect Mix Data
    private List<MixData> mixData = new ArrayList<>();

    public PlayerEffectData(Player player) {
        this.player = player;
    }

    @Override
    public String getId() {
        return player.getUniqueId().toString();
    }

    @Override
    public Location getLocation() {
        return player == null ? null : player.getLocation();
    }

    @Override
    public Double getVariable(String variable) {
        return switch (variable) {
            case "isMoving" -> moving ? 1D : 0D;
            case "isStanding" -> !moving ? 1D : 0D;
            case "velocityX" -> player == null ? 0 : player.getVelocity().getX();
            case "velocityY" -> player == null ? 0 : player.getVelocity().getY();
            case "velocityZ" -> player == null ? 0 : player.getVelocity().getZ();
            case "velocityLength" -> player == null ? 0 : player.getVelocity().lengthSquared();
            case "lastBoostMillis", "LBM" -> (double) lastBoostMillis;
            default -> null;
        };
    }

    // Moving
    public void increaseInterval() {
        this.notMovingInterval += 5;
        if (this.notMovingInterval > 20)
            this.moving = false;
    }

    public void resetInterval() {
        this.notMovingInterval = 0;
        this.moving = true;
    }

    // Mix Data
    public void resetMixDataCache() {
        mixData.forEach(MixData::resetCache);
    }

    public boolean hasMixData(String name) {
        return mixData.stream().anyMatch(data -> data.name().equals(name));
    }

    // Handler Event
    @Override
    public void setCurrentEvent(HandlerEvent currentEvent) {
        super.setCurrentEvent(currentEvent);
        this.targetEntity = null;
    }

    @Override
    public void resetEvent() {
        super.resetEvent();
        this.targetEntity = null;
    }

    // Permissions
    public boolean canSeeEffects() {
        return player != null && effectsEnabled && (!Permissions.EFFECTS_VISIBILITY_PERMISSION || player.hasPermission(Permissions.CAN_SEE_EFFECTS));
    }

    public int getMixLimit() {
        return getMax(Keys.NAMESPACE + ".mix_limit.");
    }

    public boolean canCreateAnotherMix() {
        if (!Permissions.MIX_LIMIT_ENABLED) return true;
        var max = getMixLimit();
        return max == -1 || max > mixData.size();
    }

    public int getMixEffectLimit() {
        return getMax(Keys.NAMESPACE + ".mix_effect_limit.");
    }

    private int getMax(String permission) {
        if (player == null) return 0;
        if (player.isOp())
            return -1;

        final AtomicInteger max = new AtomicInteger();

        player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith(permission)).map(value -> value.replace(permission, "")).forEach(value -> {
            if (value.equalsIgnoreCase("*")) {
                max.set(-1);
                return;
            }

            if (max.get() == -1) return;

            try {
                int amount = Integer.parseInt(value);

                if (amount > max.get())
                    max.set(amount);
            } catch (NumberFormatException ignored) {
            }
        });

        return max.get();
    }
}
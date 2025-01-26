package net.treasure.particles.effect.data;

import lombok.Getter;
import lombok.Setter;
import net.treasure.particles.constants.Keys;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.mix.MixData;
import net.treasure.particles.permission.Permissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PlayerEffectData extends EffectData {

    @NotNull
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

    public PlayerEffectData(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public String getId() {
        return player.getUniqueId().toString();
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Double getVariable(String variable) {
        return switch (variable) {
            case "isMoving" -> moving ? 1D : 0D;
            case "isStanding" -> !moving ? 1D : 0D;
            case "velocityX" -> player.getVelocity().getX();
            case "velocityY" -> player.getVelocity().getY();
            case "velocityZ" -> player.getVelocity().getZ();
            case "velocityLength" -> player.getVelocity().lengthSquared();
            case "lastBoostMillis", "LBM" -> (double) lastBoostMillis;
            default -> null;
        };
    }

    @Override
    public boolean setCurrentEffect(Effect currentEffect) {
        if (!checkElytra(currentEffect))
            return false;
        return super.setCurrentEffect(currentEffect);
    }

    public boolean checkElytra(Effect effect) {
        var chestplate = player.getInventory().getChestplate();
        return effect == null || !effect.isOnlyElytra() || (chestplate != null && chestplate.getType() == Material.ELYTRA && chestplate.getDurability() < Material.ELYTRA.getMaxDurability() - 1);
    }

    // Moving
    public void increaseMovingInterval() {
        this.notMovingInterval += 5;
        if (this.notMovingInterval > 15)
            this.moving = false;
    }

    public void resetMovingInterval() {
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
        return effectsEnabled && (!Permissions.EFFECTS_VISIBILITY_PERMISSION || player.hasPermission(Permissions.CAN_SEE_EFFECTS));
    }

    public int getMixLimit() {
        return getMaxPermissionValue(Keys.NAMESPACE + ".mix_limit.");
    }

    public boolean canCreateAnotherMix() {
        if (!Permissions.MIX_LIMIT_ENABLED) return true;
        var max = getMixLimit();
        return max == -1 || max > mixData.size();
    }

    public int getMixEffectLimit() {
        return !Permissions.MIX_EFFECT_LIMIT_ENABLED ? -1 : getMaxPermissionValue(Keys.NAMESPACE + ".mix_effect_limit.");
    }

    private int getMaxPermissionValue(String permission) {
        if (player.isOp()) return -1;

        var max = new AtomicInteger();

        player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith(permission)).map(value -> value.replace(permission, "")).forEach(value -> {
            if (value.equals("*")) {
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
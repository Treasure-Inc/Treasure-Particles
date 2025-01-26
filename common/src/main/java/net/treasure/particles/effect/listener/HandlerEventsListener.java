package net.treasure.particles.effect.listener;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import lombok.AllArgsConstructor;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.constants.Keys;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.metadata.FixedMetadataValue;

@AllArgsConstructor
public class HandlerEventsListener implements Listener {

    private final PlayerManager playerManager;

    @EventHandler
    public void on(ArmorEquipEvent event) {
        if (event.getOldArmorPiece() == null || event.getOldArmorPiece().getType() != Material.ELYTRA) return;

        var data = playerManager.getEffectData(event.getPlayer());
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        if (!effect.isOnlyElytra()) return;
        data.setCurrentEffect(null);
    }

    @EventHandler
    public void on(EntityToggleGlideEvent event) {
        if (event.isGliding() || !(event.getEntity() instanceof Player player)) return;

        var data = playerManager.getEffectData(player);
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        if (!effect.isOnlyElytra()) return;
        var chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.ELYTRA || chestplate.getDurability() >= chestplate.getType().getMaxDurability() - 1)
            data.setCurrentEffect(null);
    }


    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var data = playerManager.getEffectData(player);
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        if (!effect.getEvents().contains(HandlerEvent.TAKE_DAMAGE)) return;
        data.setCurrentEvent(HandlerEvent.TAKE_DAMAGE);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        var data = playerManager.getEffectData(player);
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        var type = event.getEntity() instanceof Player ? HandlerEvent.PLAYER_DAMAGE : HandlerEvent.MOB_DAMAGE;
        if (!effect.getEvents().contains(type)) return;

        data.setCurrentEvent(type);
        data.setTargetEntity(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDeathEvent event) {
        var killer = event.getEntity().getKiller();
        if (killer == null) return;

        var data = playerManager.getEffectData(killer);
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        var type = event.getEntity() instanceof Player ? HandlerEvent.PLAYER_KILL : HandlerEvent.MOB_KILL;
        if (!effect.getEvents().contains(type)) return;

        data.setCurrentEvent(type);
        data.setTargetEntity(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        if (event.getEntity() instanceof Firework || event.getEntity() instanceof FishHook) return;
        var data = playerManager.getEffectData(player);

        if (data == null) return;
        var effect = data.getCurrentEffect();

        if (effect == null) return;
        if (!effect.getEvents().contains(HandlerEvent.PROJECTILE)) return;

        data.setCurrentEvent(HandlerEvent.PROJECTILE);
        data.setTargetEntity(event.getEntity());
        event.getEntity().setMetadata(Keys.NAMESPACE, new FixedMetadataValue(TreasureParticles.getPlugin(), data));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ProjectileHitEvent event) {
        if (!event.getEntity().hasMetadata(Keys.NAMESPACE)) return;
        try {
            var data = (PlayerEffectData) event.getEntity().getMetadata(Keys.NAMESPACE).get(0).value();
            if (data != null && event.getEntity().equals(data.getTargetEntity()))
                data.resetEvent();
        } catch (Exception ignored) {
        }
        event.getEntity().removeMetadata(Keys.NAMESPACE, TreasureParticles.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;

        var data = playerManager.getEffectData(player);
        if (data == null) return;

        var effect = data.getCurrentEffect();
        if (effect == null) return;

        if (!effect.getEvents().contains(HandlerEvent.RIDE_VEHICLE)) return;

        data.setCurrentEvent(HandlerEvent.RIDE_VEHICLE);
        data.setTargetEntity(event.getVehicle());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player player)) return;

        var data = playerManager.getEffectData(player);
        if (data == null) return;

        if (data.getCurrentEvent() != HandlerEvent.RIDE_VEHICLE) return;
        data.resetEvent();
    }
}
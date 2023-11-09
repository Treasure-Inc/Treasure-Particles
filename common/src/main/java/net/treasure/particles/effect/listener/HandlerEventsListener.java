package net.treasure.particles.effect.listener;

import lombok.AllArgsConstructor;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.player.PlayerManager;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

@AllArgsConstructor
public class HandlerEventsListener implements Listener {

    PlayerManager playerManager;

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            var data = playerManager.getEffectData(player);
            if (data == null) return;
            var effect = data.getCurrentEffect();
            if (effect == null) return;
            if (!effect.getEvents().contains(HandlerEvent.TAKE_DAMAGE)) return;
            data.setCurrentEvent(HandlerEvent.TAKE_DAMAGE);
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            var data = playerManager.getEffectData(player);
            if (data == null) return;
            var effect = data.getCurrentEffect();
            if (effect == null) return;
            var type = event.getEntity() instanceof Player ? HandlerEvent.PLAYER_DAMAGE : HandlerEvent.MOB_DAMAGE;
            if (!effect.getEvents().contains(type)) return;
            data.setCurrentEvent(type);
            data.setTargetEntity(event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDeathEvent event) {
        var killer = event.getEntity().getKiller();
        if (killer != null) {
            var data = playerManager.getEffectData(killer);
            if (data == null) return;
            var effect = data.getCurrentEffect();
            if (effect == null) return;
            var type = event.getEntity() instanceof Player ? HandlerEvent.PLAYER_KILL : HandlerEvent.MOB_KILL;
            if (!effect.getEvents().contains(type)) return;
            data.setCurrentEvent(type);
            data.setTargetEntity(event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            if (event.getEntity() instanceof Firework) return;
            var data = playerManager.getEffectData(player);
            if (data == null) return;
            var effect = data.getCurrentEffect();
            if (effect == null) return;
            if (!effect.getEvents().contains(HandlerEvent.PROJECTILE)) return;
            data.setCurrentEvent(HandlerEvent.PROJECTILE);
            data.setTargetEntity(event.getEntity());
            event.getEntity().setMetadata("TreasureParticles", new FixedMetadataValue(TreasureParticles.getPlugin(), data));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("TreasureParticles")) {
            try {
                var data = (EffectData) event.getEntity().getMetadata("TreasureParticles").get(0).value();
                data.resetEvent();
            } catch (Exception ignored) {
            }
            event.getEntity().removeMetadata("TreasureParticles", TreasureParticles.getPlugin());
        }
    }
}
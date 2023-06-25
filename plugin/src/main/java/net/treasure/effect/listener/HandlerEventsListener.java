package net.treasure.effect.listener;

import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.player.PlayerManager;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
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
            if (player.isGliding() && event.getEntity() instanceof Firework) return;
            var data = playerManager.getEffectData(player);
            if (data == null) return;
            var effect = data.getCurrentEffect();
            if (effect == null) return;
            if (!effect.getEvents().contains(HandlerEvent.PROJECTILE)) return;
            data.setCurrentEvent(HandlerEvent.PROJECTILE);
            data.setTargetEntity(event.getEntity());
            event.getEntity().setMetadata("TreasureParticles", new FixedMetadataValue(TreasurePlugin.getInstance(), data));
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
            event.getEntity().removeMetadata("TreasureParticles", TreasurePlugin.getInstance());
        }
    }
}
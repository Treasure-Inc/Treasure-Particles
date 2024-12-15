package net.treasure.particles.effect.data;

import lombok.Getter;
import lombok.Setter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import org.bukkit.Location;

@Getter
public class LocationEffectData extends EffectData {

    private final String id;
    private Location location;
    @Setter
    private boolean persistent = true;

    public LocationEffectData(String id, Location location) {
        this.id = id;
        this.location = location;
        this.currentEvent = HandlerEvent.STATIC;
    }

    public LocationEffectData(String id, Location location, boolean persistent) {
        this.id = id;
        this.location = location;
        this.currentEvent = HandlerEvent.STATIC;
        this.persistent = persistent;
    }

    @Override
    public void resetEvent() {
        this.currentEvent = HandlerEvent.STATIC;
    }

    @Override
    public Double getVariable(String variable) {
        return null;
    }

    @Override
    public boolean setCurrentEffect(Effect currentEffect) {
        var success = super.setCurrentEffect(currentEffect);
        if (success && persistent)
            TreasureParticles.getEffectManager().getStaticEffects().set(id, currentEffect == null ? null : currentEffect.getKey(), location);
        return success;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (persistent)
            TreasureParticles.getEffectManager().getStaticEffects().set(id, currentEffect == null ? null : currentEffect.getKey(), location);
    }

    public void stop() {
        var effectManager = TreasureParticles.getEffectManager();
        if (persistent)
            effectManager.getStaticEffects().set(id, null, null);
        effectManager.getData().remove(id);
    }
}
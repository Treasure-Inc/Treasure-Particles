package net.treasure.particles.effect.data;

import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import org.bukkit.Location;

@Getter
public class LocationEffectData extends EffectData {

    private final String id;
    private Location location;

    public LocationEffectData(String id, Location location) {
        this.id = id;
        this.location = location;
        this.currentEvent = HandlerEvent.STATIC;
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
    public void setCurrentEffect(Effect currentEffect) {
        super.setCurrentEffect(currentEffect);
        TreasureParticles.getEffectManager().getStaticEffects().set(id, currentEffect == null ? null : currentEffect.getKey(), location);
    }

    public void setLocation(Location location) {
        this.location = location;
        TreasureParticles.getEffectManager().getStaticEffects().set(id, currentEffect == null ? null : currentEffect.getKey(), location);
    }

    public void stop() {
        var effectManager = TreasureParticles.getEffectManager();
        effectManager.getStaticEffects().set(id, null, null);
        effectManager.getData().remove(id);
    }
}
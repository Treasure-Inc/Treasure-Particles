package net.treasure.particles.gui.type.mixer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.gui.type.effects.EffectsHolder;
import net.treasure.particles.util.tuples.Pair;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Getter
@Setter
public class MixerHolder extends EffectsHolder {
    private final List<Pair<Effect, TickHandler>> selected;
    private final EnumMap<HandlerEvent, Pair<Effect, TickHandler>> locked;
    private String prefColorGroup;
    @Accessors(fluent = true)
    private boolean needsColorGroup;
    @Accessors(fluent = true)
    private boolean canSelectAnotherEffect;

    public MixerHolder() {
        this.selected = new ArrayList<>();
        this.locked = new EnumMap<>(HandlerEvent.class);
    }

    public long selectedEffectsSize() {
        return selected.stream().map(Pair::getKey).distinct().count();
    }

    public boolean isSelected(TickHandler handler) {
        return selected.stream().anyMatch(pair -> pair.getValue().equals(handler));
    }

    public boolean isSelected(Effect effect) {
        return selected.stream().anyMatch(pair -> pair.getKey().equals(effect));
    }

    public void remove(Effect effect) {
        selected.removeIf(pair -> pair.getKey().equals(effect));
        locked.values().removeIf(pair -> pair.getKey().equals(effect));
    }

    public void remove(TickHandler handler) {
        selected.removeIf(pair -> pair.getValue().equals(handler));
        locked.values().removeIf(pair -> pair.getValue().equals(handler));
    }

    public void add(Effect effect, TickHandler handler) {
        selected.add(new Pair<>(effect, handler));
        if (handler.event != null && handler.mixerOptions.lockEvent)
            locked.put(handler.event, new Pair<>(effect, handler));
        if (effect.getColorGroup() != null)
            needsColorGroup = true;
    }

    public boolean needsColorGroup() {
        return needsColorGroup && prefColorGroup == null;
    }

    public boolean isLocked(HandlerEvent event) {
        return locked.containsKey(event);
    }

    public void reset() {
        selected.clear();
        locked.clear();
    }
}
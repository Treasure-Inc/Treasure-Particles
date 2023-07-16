package net.treasure.gui.type.mixer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.effect.Effect;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.handler.TickHandler;
import net.treasure.gui.type.effects.EffectsHolder;
import net.treasure.util.tuples.Pair;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
public class MixerHolder extends EffectsHolder {
    private final List<Pair<Effect, TickHandler>> selected;
    private final EnumSet<HandlerEvent> locked;
    @Setter
    private String prefColorGroup;
    @Accessors(fluent = true)
    private boolean needsColorGroup;

    public MixerHolder() {
        this.selected = new ArrayList<>();
        this.locked = EnumSet.noneOf(HandlerEvent.class);
    }


    public boolean isSelected(TickHandler handler) {
        return selected.stream().anyMatch(pair -> pair.getValue().equals(handler));
    }

    public boolean isSelected(Effect effect) {
        return selected.stream().anyMatch(pair -> pair.getKey().equals(effect));
    }

    public void remove(Effect effect) {
        selected.removeIf(pair -> pair.getKey().equals(effect));
    }

    public void remove(TickHandler handler) {
        selected.removeIf(pair -> pair.getValue().equals(handler));
    }

    public void add(Effect effect, TickHandler handler) {
        selected.add(new Pair<>(effect, handler));
        if (handler.mixerOptions.lockEvent)
            locked.add(handler.event);
        if (effect.getColorGroup() != null)
            needsColorGroup = true;
    }

    public boolean needsColorGroup() {
        return needsColorGroup && prefColorGroup == null;
    }

    public boolean isLocked(HandlerEvent event) {
        return locked.contains(event);
    }

    public void reset() {
        selected.clear();
        locked.clear();
    }
}
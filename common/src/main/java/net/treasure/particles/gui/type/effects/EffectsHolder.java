package net.treasure.particles.gui.type.effects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.gui.GUIHolder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EffectsHolder extends GUIHolder {
    private HandlerEvent filter;
    private List<HandlerEvent> availableFilters;
    private boolean playerMixGUI;
}
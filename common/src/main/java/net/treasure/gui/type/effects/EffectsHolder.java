package net.treasure.gui.type.effects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.gui.GUIHolder;

@Getter
@Setter
@NoArgsConstructor
public class EffectsHolder extends GUIHolder {
    private HandlerEvent filter;
    private boolean playerMixGUI;
}
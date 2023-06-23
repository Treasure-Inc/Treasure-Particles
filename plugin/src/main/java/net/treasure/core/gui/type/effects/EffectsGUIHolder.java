package net.treasure.core.gui.type.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.core.gui.GUIHolder;
import net.treasure.effect.handler.HandlerEvent;

@Getter
@AllArgsConstructor
public class EffectsGUIHolder extends GUIHolder {
    HandlerEvent filter;
}
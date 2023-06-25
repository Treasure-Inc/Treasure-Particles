package net.treasure.core.gui.type.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.core.gui.GUIHolder;
import net.treasure.effect.handler.HandlerEvent;

@Getter
@AllArgsConstructor
public class AdminGUIHolder extends GUIHolder {
    FilterCategory category;
    HandlerEvent event;
}
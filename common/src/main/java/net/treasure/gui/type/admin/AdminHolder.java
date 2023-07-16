package net.treasure.gui.type.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.gui.GUIHolder;
import net.treasure.effect.handler.HandlerEvent;

@Getter
@AllArgsConstructor
public class AdminHolder extends GUIHolder {
    FilterCategory category;
    HandlerEvent event;
}
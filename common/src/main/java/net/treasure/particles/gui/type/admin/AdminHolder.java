package net.treasure.particles.gui.type.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.gui.GUIHolder;
import net.treasure.particles.effect.handler.HandlerEvent;

@Getter
@AllArgsConstructor
public class AdminHolder extends GUIHolder {
    FilterCategory category;
    HandlerEvent event;
}
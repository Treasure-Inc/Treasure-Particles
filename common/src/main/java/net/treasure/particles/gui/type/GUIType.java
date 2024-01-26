package net.treasure.particles.gui.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum GUIType {
    EFFECTS("effects"),
    COLORS("colors"),
    MIXER("mixer"),
    HANDLERS("handlers");
    private final String id;
}
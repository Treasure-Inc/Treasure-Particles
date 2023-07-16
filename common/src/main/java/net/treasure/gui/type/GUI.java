package net.treasure.gui.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum GUI {
    EFFECTS("effects"),
    COLORS("colors"),
    MIXER("mixer"),
    HANDLERS("handlers");
    final String id;
}
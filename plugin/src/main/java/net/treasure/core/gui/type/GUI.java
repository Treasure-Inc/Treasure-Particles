package net.treasure.core.gui.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum GUI {
    EFFECTS("effects"),
    COLORS("colors");
    final String id;
}
package net.treasure.core.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum ElementType {
    // COMMON
    BORDERS("borders"),
    PREVIOUS_PAGE("previous-page"),
    NEXT_PAGE("next-page"),
    // EFFECTS
    DEFAULT_ICON("default-icon"),
    RANDOM_EFFECT("random-effect"),
    RESET("reset"),
    CLOSE("close"),
    FILTER("filter"),
    // COLORS
    COLOR_ICON("color-icon"),
    RANDOM_COLOR("random-color"),
    BACK("back");
    final String id;
}
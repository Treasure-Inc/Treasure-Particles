package net.treasure.particles.gui.config;

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
    FILTER("filter"),
    // EFFECTS
    DEFAULT_ICON("default-icon"),
    RANDOM_EFFECT("random-effect"),
    RESET("reset"),
    CLOSE("close"),
    MIXER("mix"),
    // COLORS
    COLOR_ICON("color-icon"),
    RANDOM_COLOR("random-color"),
    BACK("back"),
    // MIXER
    CONFIRM("confirm");
    private final String id;
}
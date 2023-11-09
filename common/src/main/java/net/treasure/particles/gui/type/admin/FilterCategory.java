package net.treasure.particles.gui.type.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum FilterCategory {
    HAS_PERMISSION("Has Permission"),
    NO_PERMISSION("No Permission"),
    SUPPORTED_EVENTS("Supported Events");
    final String translation;
}

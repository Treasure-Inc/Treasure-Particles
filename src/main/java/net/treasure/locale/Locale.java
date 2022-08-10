package net.treasure.locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Locale {
    GERMAN("de"),
    ENGLISH("en"),
    TURKISH("tr");
    final String key;
}
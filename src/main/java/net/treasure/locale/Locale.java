package net.treasure.locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Locale {
    SPANISH("es"),
    GERMAN("de"),
    ENGLISH("en"),
    TURKISH("tr");
    final String key;
}
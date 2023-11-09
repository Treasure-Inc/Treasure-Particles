package net.treasure.particles.locale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.configuration.ConfigurationGenerator;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Locale {
    ENGLISH("en"),
    TURKISH("tr");
    final String key;

    public void generate() {
        new ConfigurationGenerator("translations_" + key + ".yml", "translations").generate();
    }

    public static boolean isSupported(String key) {
        return Stream.of(values()).anyMatch(l -> l.key.equals(key));
    }
}
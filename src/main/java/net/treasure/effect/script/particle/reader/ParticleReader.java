package net.treasure.effect.script.particle.reader;

import net.treasure.color.data.ColorData;
import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Locale;
import java.util.regex.Matcher;

public class ParticleReader implements ScriptReader<ParticleSpawner> {

    @Override
    public ParticleSpawner read(Effect effect, String line) {
        ParticleEffect particle = null;
        ParticleOrigin origin = null;

        ParticleSpawner.ParticleSpawnerBuilder builder = ParticleSpawner.builder();

        Matcher particleMatcher = Patterns.SCRIPT.matcher(line);
        while (particleMatcher.find()) {
            String key = particleMatcher.group("type");
            String value = particleMatcher.group("value");
            if (key == null || value == null)
                continue;
            if (key.equalsIgnoreCase("effect")) {
                try {
                    particle = ParticleEffect.valueOf(value.toUpperCase(Locale.ENGLISH));
                    builder.effect(particle);
                } catch (Exception | ExceptionInInitializerError | NoClassDefFoundError ignored) {
                }
            } else if (key.equalsIgnoreCase("from")) {
                if (value.startsWith("head")) {
                    origin = ParticleOrigin.HEAD;
                } else if (value.startsWith("feet")) {
                    origin = ParticleOrigin.FEET;
                } else continue;

                builder.origin(origin);
                String[] s = value.split("\\*");
                if (s.length == 2) {
                    try {
                        builder.multiplier(Float.parseFloat(s[1]));
                    } catch (Exception ignored) {
                    }
                }
            } else if (key.equalsIgnoreCase("colorScheme")) {
                builder.colorData(ColorData.initialize(value));
            } else if (key.equalsIgnoreCase("offset")) {
                Matcher offsetMatcher = Patterns.OFFSET.matcher(value);
                while (offsetMatcher.find()) {
                    String _type = offsetMatcher.group("type");
                    String _offsetValue = offsetMatcher.group("value");
                    try {
                        if (_type.equalsIgnoreCase("x"))
                            builder.offsetX(_offsetValue);
                        else if (_type.equalsIgnoreCase("y"))
                            builder.offsetY(_offsetValue);
                        else if (_type.equalsIgnoreCase("z"))
                            builder.offsetZ(_offsetValue);
                    } catch (Exception ignored) {
                    }
                }
            } else if (key.equalsIgnoreCase("direction")) {
                builder.direction(Boolean.parseBoolean(value));
            } else if (key.equalsIgnoreCase("amount")) {
                try {
                    builder.amount(Integer.parseInt(value));
                } catch (Exception ignored) {
                }
            } else if (key.equalsIgnoreCase("speed")) {
                try {
                    builder.speed(Float.parseFloat(value));
                } catch (Exception ignored) {
                }
            } else {
                if (key.equalsIgnoreCase("x"))
                    builder.x(value);
                else if (key.equalsIgnoreCase("y"))
                    builder.y(value);
                else if (key.equalsIgnoreCase("z"))
                    builder.z(value);
            }
        }
        return particle != null && origin != null ? builder.build() : null;
    }
}
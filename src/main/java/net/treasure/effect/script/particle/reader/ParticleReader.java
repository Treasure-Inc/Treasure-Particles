package net.treasure.effect.script.particle.reader;

import net.treasure.color.data.ColorData;
import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleReader implements ScriptReader<ParticleSpawner> {

    @Override
    public ParticleSpawner read(Effect effect, String line) throws ReaderException {
        ParticleEffect particle = null;
        ParticleOrigin origin = null;

        ParticleSpawner.ParticleSpawnerBuilder builder = ParticleSpawner.builder();

        Matcher particleMatcher = Patterns.SCRIPT.matcher(line);
        while (particleMatcher.find()) {
            String key = particleMatcher.group("type");
            String value = particleMatcher.group("value");
            int start = particleMatcher.start(), end = particleMatcher.end();
            if (key == null || value == null)
                continue;
            switch (key.toLowerCase(Locale.ENGLISH)) {
                case "effect" -> {
                    try {
                        particle = ParticleEffect.valueOf(value.toUpperCase(Locale.ENGLISH));
                        builder.effect(particle);
                    } catch (Exception | ExceptionInInitializerError | NoClassDefFoundError ignored) {
                        error(effect, line, start, end, "Unexpected effect value: " + value);
                    }
                }
                case "origin" -> {

                    if (value.startsWith("head")) {
                        origin = ParticleOrigin.HEAD;
                    } else if (value.startsWith("feet")) {
                        origin = ParticleOrigin.FEET;
                    } else {
                        error(effect, line, start, end, "Unexpected origin value: " + value);
                        continue;
                    }

                    builder.origin(origin);
                    String[] s = value.split("\\*");
                    if (s.length == 2) {
                        try {
                            builder.multiplier(Float.parseFloat(s[1]));
                        } catch (Exception ignored) {
                            error(effect, line, start, end, "Invalid origin multiplier usage: " + value);
                        }
                    }
                }
                case "colorscheme" -> {
                    try {
                        var colorData = ColorData.initialize(value);
                        builder.colorData(colorData);
                    } catch (ReaderException e) {
                        error(effect, line, start, end, e.getMessage());
                    }
                }
                case "offset" -> {
                    Matcher offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
                    while (offsetMatcher.find()) {
                        String _type = offsetMatcher.group("type");
                        String _value = offsetMatcher.group("value");
                        try {
                            if (_type.equalsIgnoreCase("x"))
                                builder.offsetX(_value);
                            else if (_type.equalsIgnoreCase("y"))
                                builder.offsetY(_value);
                            else if (_type.equalsIgnoreCase("z"))
                                builder.offsetZ(_value);
                            else
                                error(effect, line, start, end, "Unexpected offset value: " + _type);
                        } catch (ReaderException ignored) {
                        } catch (Exception ignored) {
                            error(effect, line, start, end, "Unexpected offset value: " + _value);
                        }
                    }
                }
                case "direction" -> builder.direction(Boolean.parseBoolean(value));
                case "amount" -> {
                    try {
                        builder.amount(Integer.parseInt(value));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected particle amount value: " + value);
                    }
                }
                case "speed" -> {
                    try {
                        builder.speed(Float.parseFloat(value));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected particle speed value: " + value);
                    }
                }
                case "size" -> {
                    try {
                        builder.size(Float.parseFloat(value));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected particle size value: " + value);
                    }
                }
                case "block" -> {
                    Material material = null;
                    byte data = 0;
                    Matcher offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
                    while (offsetMatcher.find()) {
                        String _type = offsetMatcher.group("type");
                        String _value = offsetMatcher.group("value");
                        try {
                            if (_type.equalsIgnoreCase("material"))
                                material = Material.valueOf(_value.toUpperCase(Locale.ENGLISH));
                            else if (_type.equalsIgnoreCase("data"))
                                data = Byte.parseByte(_value);
                        } catch (Exception ignored) {
                            error(effect, line, offsetMatcher.start(), offsetMatcher.end(), "Unexpected value for " + _type + ": " + _value);
                        }
                    }
                    if (material != null)
                        builder.particleData(new BlockTexture(material, data));
                    else
                        error(effect, line, start, end, "Material is null");
                }
                case "item" -> {
                    Material material = null;
                    int data = 0;
                    Matcher offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
                    while (offsetMatcher.find()) {
                        String _type = offsetMatcher.group("type");
                        String _value = offsetMatcher.group("value");
                        try {
                            if (_type.equalsIgnoreCase("material"))
                                material = Material.valueOf(_value.toUpperCase(Locale.ENGLISH));
                            else if (_type.equalsIgnoreCase("data"))
                                data = Integer.parseInt(_value);
                        } catch (Exception ignored) {
                            error(effect, line, offsetMatcher.start(), offsetMatcher.end(), "Unexpected value for " + _type + ": " + _value);
                        }
                    }
                    if (material != null) {
                        var item = new ItemStack(material);
                        var meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setCustomModelData(data);
                            item.setItemMeta(meta);
                        }
                        builder.particleData(new ItemTexture(item));
                    } else
                        error(effect, line, start, end, "Material is null");
                }
                case "x" -> builder.x(value);
                case "y" -> builder.y(value);
                case "z" -> builder.z(value);
                default -> error(effect, line, start, end, "Unexpected type: " + key);
            }
        }

        if (particle == null)
            error(effect, line, "You must define an 'effect' value");

        if (origin == null)
            error(effect, line, "You must define an 'origin' value (" + Stream.of(ParticleOrigin.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).collect(Collectors.joining(",")) + ")");

        return particle != null && origin != null ? builder.build() : null;
    }
}
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
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.SculkChargeData;
import xyz.xenondevs.particle.data.ShriekData;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleReader implements ScriptReader<ParticleSpawner> {

    @Override
    public ParticleSpawner read(Effect effect, String line) throws ReaderException {
        ParticleEffect particle = null;
        ParticleOrigin origin = null;

        var builder = ParticleSpawner.builder();

        var particleMatcher = Patterns.SCRIPT.matcher(line);
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
                    } catch (Exception | Error ignored) {
                        error(effect, line, start, end, "Unexpected effect value: " + value);
                    }
                }
                case "origin" -> {
                    if (value.startsWith("head")) {
                        origin = ParticleOrigin.HEAD;
                    } else if (value.startsWith("feet")) {
                        origin = ParticleOrigin.FEET;
                    } else if (value.startsWith("world")) {
                        origin = ParticleOrigin.WORLD;
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
                case "colorscheme", "color" -> {
                    if (particle != null && !particle.hasProperty(PropertyType.COLORABLE)) {
                        error(effect, line, start, end, "You cannot use 'colorScheme' with this particle effect: " + particle.name());
                        continue;
                    }
                    try {
                        var colorData = ColorData.initialize(value);
                        builder.colorData(colorData);
                    } catch (ReaderException e) {
                        error(effect, line, start, end, e.getMessage());
                    }
                }
                case "offset" -> {
                    if (particle != null && !particle.hasProperty(PropertyType.DIRECTIONAL) && !particle.hasProperty(PropertyType.DUST)) {
                        error(effect, line, start, end, "You cannot use 'offset' with this particle effect: " + particle.name());
                        continue;
                    }
                    var offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
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
                case "direction", "directional" -> builder.direction(Boolean.parseBoolean(value));
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
                    if (particle != null && !particle.hasProperty(PropertyType.DUST)) {
                        error(effect, line, start, end, "You cannot use 'size' with this particle effect: " + particle.name());
                        continue;
                    }
                    try {
                        builder.size(Float.parseFloat(value));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected particle size value: " + value);
                    }
                }
                case "block" -> {
                    if (particle != null && !particle.hasProperty(PropertyType.REQUIRES_BLOCK)) {
                        error(effect, line, start, end, "You cannot use 'block' with this particle effect: " + particle.name());
                        continue;
                    }
                    Material material = null;
                    byte data = 0;
                    var offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
                    while (offsetMatcher.find()) {
                        String _type = offsetMatcher.group("type");
                        String _value = offsetMatcher.group("value");
                        try {
                            switch (_type) {
                                case "material" -> material = Material.valueOf(_value.toUpperCase(Locale.ENGLISH));
                                case "data" -> data = Byte.parseByte(_value);
                            }
                        } catch (Exception ignored) {
                            error(effect, line, offsetMatcher.start(), offsetMatcher.end(), "Unexpected value for " + _type + ": " + _value);
                        }
                    }
                    if (material != null)
                        builder.particleData(new BlockTexture(material, data));
                    else
                        error(effect, line, start, end, "Material cannot be null");
                }
                case "item" -> {
                    if (particle != null && !particle.hasProperty(PropertyType.REQUIRES_ITEM)) {
                        error(effect, line, start, end, "You cannot use 'item' with this particle effect: " + particle.name());
                        continue;
                    }
                    Material material = null;
                    int data = 0;
                    var offsetMatcher = Patterns.INNER_SCRIPT.matcher(value);
                    while (offsetMatcher.find()) {
                        String _type = offsetMatcher.group("type");
                        String _value = offsetMatcher.group("value");
                        try {
                            switch (_type) {
                                case "material" -> material = Material.valueOf(_value.toUpperCase(Locale.ENGLISH));
                                case "data" -> data = Integer.parseInt(_value);
                            }
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
                        error(effect, line, start, end, "Material cannot be null");
                }
                case "delay" -> {
                    if (particle != null && !particle.equals(ParticleEffect.SHRIEK)) {
                        error(effect, line, start, end, "You can only use 'delay' with SHRIEK effect.");
                        continue;
                    }
                    try {
                        builder.particleData(new ShriekData(Integer.parseInt(value)));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected delay value: " + value);
                    }
                }
                case "roll" -> {
                    if (particle != null && !particle.equals(ParticleEffect.SCULK_CHARGE)) {
                        error(effect, line, start, end, "You can only use 'roll' with SCULK_CHARGE effect.");
                        continue;
                    }
                    try {
                        builder.particleData(new SculkChargeData(Float.parseFloat(value)));
                    } catch (Exception ignored) {
                        error(effect, line, start, end, "Unexpected roll value: " + value);
                    }
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
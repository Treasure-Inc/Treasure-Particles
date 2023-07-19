package net.treasure.effect.script.particle.reader;

import net.treasure.color.data.ColorData;
import net.treasure.color.data.duo.DuoImpl;
import net.treasure.constants.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.argument.type.DoubleArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.ItemStackArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.reader.ScriptReader;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.util.nms.particles.Particles;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ParticleReader<T extends ParticleSpawner> extends ScriptReader<ParticleReader.Context<T>, T> {

    public ParticleReader() {
        addValidArgument(c -> {
            var args = Patterns.COLON.split(c.value());
            if (args.length > 2) {
                error(c, "Incorrect particle effect input: " + c.value());
                return;
            }
            if (args.length == 2 && !args[0].equals("minecraft")) {
                error(c, "Incorrect particle effect input: " + c.value());
                return;
            }

            ParticleEffect particle;
            if (args.length == 2) {
                particle = ParticleEffect.MINECRAFT_KEYS.get(args[1]);
            } else {
                particle = StaticArgument.asEnum(c, ParticleEffect.class);
            }

            if (particle == null || particle.bukkit() == null) {
                if (c.value().equalsIgnoreCase("vibration")) {
                    error(c, "Vibration effect is not supported");
                    return;
                }
                error(c, "Unknown particle effect: " + c.value());
                return;
            }

            c.script().particle(particle);
        }, "effect", "particle");

        addValidArgument(c -> {
            var args = Patterns.ASTERISK.split(c.value());
            var origin = StaticArgument.asEnumArgument(c, ParticleOrigin.class).get(args[0]);
            c.script().origin(origin);

            if (args.length == 2) {
                try {
                    try {
                        var multiplier = VectorArgument.read(c, args[1]);
                        c.script().multiplier(multiplier);
                    } catch (Exception e) {
                        var multiplier = DoubleArgument.read(c, args[1]);
                        c.script().multiplier(new VectorArgument(multiplier.value, multiplier.value, multiplier.value));
                    }
                } catch (Exception e) {
                    error(c, "Invalid origin multiplier usage: " + c.value(), e.getMessage());
                }
            }
        }, "origin");

        addValidArgument(c -> c.script().position(VectorArgument.read(c)), "pos", "position");

        addValidArgument(c -> c.script().offset(VectorArgument.read(c)), "offset");

        addValidArgument(c -> {
            var particle = c.script().particle();
            if (particle != null && !particle.hasProperty(ParticleEffect.Property.CAN_BE_COLORED)) {
                error(c, "You cannot use '" + c.key() + "' with this particle effect: " + particle.name());
                return;
            }
            c.script().colorData(ColorData.fromString(c));
        }, "color", "color-scheme");

        addValidArgument(c -> c.script().directional(StaticArgument.asBoolean(c)), "direction", "directional");

        addValidArgument(c -> c.script().longDistance(StaticArgument.asBoolean(c)), "long-distance", "long");

        addValidArgument(c -> c.script().amount(IntArgument.read(c)), "amount");

        addValidArgument(c -> c.script().speed(RangeArgument.read(c)), "speed");

        addValidArgument(c -> c.script().size(RangeArgument.read(c)), "size");

        addValidArgument(c -> {
            var particle = c.script().particle();
            if (particle != null && !particle.hasProperty(ParticleEffect.Property.REQUIRES_ITEM)) {
                error(c, "You cannot use '" + c.key() + "' with this particle effect: " + particle.name());
                return;
            }
            var item = ItemStackArgument.read(c);
            if (item == null) return;
            c.script().particleData(Particles.NMS.getGenericData(particle, item));
        }, "item");

        addValidArgument(c -> {
            var particle = c.script().particle();
            if (particle != null && !particle.hasProperty(ParticleEffect.Property.REQUIRES_BLOCK)) {
                error(c, "You cannot use '" + c.key() + "' with this particle effect: " + particle.name());
                return;
            }
            var item = ItemStackArgument.read(c);
            if (item == null) return;
            c.script().particleData(Particles.NMS.getGenericData(particle, Bukkit.createBlockData(item.getType())));
        }, "block");

        addValidArgument(c -> {
            var particle = c.script().particle();
            if (particle != null && !particle.equals(ParticleEffect.SHRIEK)) {
                error(c, "You can only use '" + c.key() + "' with SHRIEK particle effect.");
                return;
            }
            try {
                c.script().particleData(Particles.NMS.getGenericData(particle, StaticArgument.asInt(c)));
            } catch (ReaderException e) {
                error(c, "Unexpected '" + c.key() + "' value: " + c.value(), e.getMessage());
            } catch (Exception ignored) {
                error(c, "Unexpected '" + c.key() + "' value: " + c.value());
            }
        }, "delay");

        addValidArgument(c -> {
            var particle = c.script().particle();
            if (particle != null && !particle.equals(ParticleEffect.SCULK_CHARGE)) {
                error(c, "You can only use '" + c.key() + "' with SCULK_CHARGE particle effect.");
                return;
            }
            try {
                c.script().particleData(Particles.NMS.getGenericData(particle, StaticArgument.asFloat(c)));
            } catch (ReaderException e) {
                error(c, "Unexpected '" + c.key() + "' value: " + c.value(), e.getMessage());
            } catch (Exception ignored) {
                error(c, "Unexpected '" + c.key() + "' value: " + c.value());
            }
        }, "roll");
    }

    public abstract Context<T> createParticleReaderContext(Effect effect, String type, String line);

    @Override
    public Context<T> createContext(Effect effect, String type, String line) {
        return createParticleReaderContext(effect, type, line);
    }

    @Override
    public boolean validate(Context<T> context) throws ReaderException {
        var particle = context.script().particle();
        if (particle == null) {
            error(context.effect(), context.type(), context.line(), "You must define an 'effect' value");
            return false;
        }

        if (context.script().origin() == null) {
            error(context.effect(), context.type(), context.line(), "You must define an 'origin' value (" + Stream.of(ParticleOrigin.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).collect(Collectors.joining(",")) + ")");
            return false;
        }

        var colorData = context.script().colorData();
        if (particle.hasProperty(ParticleEffect.Property.DUST) && colorData == null) {
            error(context.effect(), context.type(), context.line(), "You must define a 'color' value");
            return false;
        }

        if (particle != ParticleEffect.DUST_COLOR_TRANSITION && colorData instanceof DuoImpl) {
            error(context.effect(), context.type(), context.line(), "You can only use duo color scheme with 'dust_color_transition' particle");
            return false;
        }

        return true;
    }

    public static abstract class Context<S extends ParticleSpawner> extends ReaderContext<S> {
        public Context(Effect effect, String type, String line, S particleSpawner) {
            super(effect, type, line, particleSpawner);
        }
    }
}
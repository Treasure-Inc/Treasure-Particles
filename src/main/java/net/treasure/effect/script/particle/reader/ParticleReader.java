package net.treasure.effect.script.particle.reader;

import net.treasure.color.data.ColorData;
import net.treasure.color.data.SingleColorData;
import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.argument.type.BooleanArgument;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.ItemStackArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
import net.treasure.effect.script.particle.ParticleOrigin;
import net.treasure.effect.script.particle.ParticleSpawner;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.SculkChargeData;
import xyz.xenondevs.particle.data.ShriekData;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.awt.*;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleReader<T extends ParticleSpawner> extends ScriptReader<DotParticleReaderContext, T> {

    public ParticleReader() {
        addValidArgument(c -> c.script().effect(StaticArgument.asEnum(c, ParticleEffect.class)), "effect");

        addValidArgument(c -> {
            var args = Patterns.ASTERISK.split(c.value());
            var origin = StaticArgument.asEnumArgument(c, ParticleOrigin.class).get(args[0]);
            if (origin == null) return;
            c.script().origin(origin);

            if (args.length == 2) {
                try {
                    var multiplier = FloatArgument.read(c, args[1]);
                    c.script().multiplier(multiplier);
                } catch (Exception e) {
                    error(c, "Invalid origin multiplier usage: " + c.value(), e.getMessage());
                }
            }
        }, "origin");

        addValidArgument(c -> c.script().position(VectorArgument.read(c)), "pos");

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.hasProperty(PropertyType.COLORABLE)) {
                error(c, "You cannot use 'colorScheme' with this particle effect: " + particle.name());
                return;
            }
            try {
                c.script().colorData(ColorData.fromString(c.value()));
            } catch (ReaderException e) {
                try {
                    c.script().colorData(new SingleColorData(Color.decode("#" + c.value())));
                    return;
                } catch (Exception ignored) {
                }
                error(c, e.getMessage());
            }
        }, "color", "colorscheme");

        addValidArgument(c -> c.script().directional(BooleanArgument.read(c)), "direction", "directional");

        addValidArgument(c -> c.script().amount(IntArgument.read(c)), "amount");

        addValidArgument(c -> c.script().speed(RangeArgument.read(c)), "speed");

        addValidArgument(c -> c.script().size(RangeArgument.read(c)), "size");

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.hasProperty(PropertyType.REQUIRES_ITEM)) {
                error(c, "You cannot use 'item' with this particle effect: " + particle.name());
                return;
            }
            var item = ItemStackArgument.read(c);
            if (item == null) return;
            c.script().particleData(new ItemTexture(item));
        }, "item");

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.hasProperty(PropertyType.REQUIRES_BLOCK)) {
                error(c, "You cannot use 'block' with this particle effect: " + particle.name());
                return;
            }
            var item = ItemStackArgument.read(c);
            if (item == null) return;
            c.script().particleData(new BlockTexture(item.getType()));
        }, "block");

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.equals(ParticleEffect.SHRIEK)) {
                error(c, "You can only use 'delay' with SHRIEK effect.");
                return;
            }
            try {
                c.script().particleData(new ShriekData(Integer.parseInt(c.value())));
            } catch (Exception ignored) {
                error(c, "Unexpected delay value: " + c.value());
            }
        }, "delay");

        addValidArgument(c -> {
            var particle = c.script().effect();
            if (particle != null && !particle.equals(ParticleEffect.SCULK_CHARGE)) {
                error(c, "You can only use 'roll' with SCULK_CHARGE effect.");
                return;
            }
            try {
                c.script().particleData(new SculkChargeData(Float.parseFloat(c.value())));
            } catch (Exception ignored) {
                error(c, "Unexpected roll value: " + c.value());
            }
        }, "roll");
    }

    @Override
    public DotParticleReaderContext createContext(Effect effect, String type, String line) {
        return new DotParticleReaderContext(effect, type, line);
    }

    @Override
    public boolean validate(DotParticleReaderContext context) throws ReaderException {
        if (context.script().effect() == null) {
            error(context.effect(), context.type(), context.line(), "You must define an 'effect' value");
            return false;
        }

        if (context.script().origin() == null) {
            error(context.effect(), context.type(), context.line(), "You must define an 'origin' value (" + Stream.of(ParticleOrigin.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).collect(Collectors.joining(",")) + ")");
            return false;
        }
        return true;
    }
}
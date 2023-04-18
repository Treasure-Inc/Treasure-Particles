package net.treasure.effect.script.parkour.reader;

import net.treasure.color.data.ColorData;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.StaticArgument;
import net.treasure.effect.script.parkour.Parkour;
import net.treasure.effect.script.particle.style.CircleParticle;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.reader.ScriptReader;

public class ParkourReader extends ScriptReader<ParkourReader.Context, Parkour> {

    public ParkourReader() {
        addValidArgument(c -> c.script().interval(IntArgument.read(c)), "interval");
        addValidArgument(c -> c.script().duration(IntArgument.read(c)), "duration");
        addValidArgument(c -> {
            var presets = TreasurePlugin.getInstance().getEffectManager().getPresets();
            var script = presets.read(c.effect(), c.value());
            if (!(script instanceof CircleParticle circleParticle)) return;
            c.script().style(circleParticle);
        }, "style");

        addValidArgument(c -> c.script().standby(ColorData.fromString(c)), "standby");
        addValidArgument(c -> c.script().success(ColorData.fromString(c)), "success");
        addValidArgument(c -> c.script().fail(ColorData.fromString(c)), "fail");

        addValidArgument(c -> c.script().whenSpawned(TreasurePlugin.getInstance().getEffectManager().read(c.effect(), "preset", c.value())), "when-spawned");
        addValidArgument(c -> c.script().whenSucceeded(TreasurePlugin.getInstance().getEffectManager().read(c.effect(), "preset", c.value())), "when-succeeded");
        addValidArgument(c -> c.script().whenFailed(TreasurePlugin.getInstance().getEffectManager().read(c.effect(), "preset", c.value())), "when-failed");

        addValidArgument(c -> c.script().immediate(StaticArgument.asBoolean(c)), "immediate");
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    @Override
    public boolean validate(Context c) throws ReaderException {
        var s = c.script();

        if (s.style() == null) {
            error(c.effect(), c.type(), c.line(), "You must define an 'style' value");
            return false;
        }

        if (s.interval() == null) {
            error(c.effect(), c.type(), c.line(), "You must define an 'interval' value");
            return false;
        }

        if (s.duration() == null) {
            error(c.effect(), c.type(), c.line(), "You must define an 'duration' value");
            return false;
        }

        if (s.style().particle().hasProperty(ParticleEffect.Property.DUST)) {
            if (s.standby() == null)
                error(c.effect(), c.type(), c.line(), "You must define a 'standby' color value");
            else if (s.success() == null)
                error(c.effect(), c.type(), c.line(), "You must define a 'success' color value");
            else if (s.fail() == null)
                error(c.effect(), c.type(), c.line(), "You must define a 'fail' color value");
            else
                return true;
            return false;
        }
        return true;
    }

    public static class Context extends ReaderContext<Parkour> {
        public Context(Effect effect, String type, String line) {
            super(effect, type, line, new Parkour());
        }
    }
}
package net.treasure.particles.effect.script.parkour.reader;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.argument.type.IntArgument;
import net.treasure.particles.effect.script.argument.type.StaticArgument;
import net.treasure.particles.effect.script.parkour.Parkour;
import net.treasure.particles.effect.script.particle.style.circle.CircleParticle;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;
import net.treasure.particles.util.nms.particles.ParticleEffect;

public class ParkourReader extends ScriptReader<ParkourReader.Context, Parkour> {

    public ParkourReader() {
        addValidArgument(c -> c.script().interval(IntArgument.read(c)), true, "interval");
        addValidArgument(c -> c.script().duration(IntArgument.read(c)), true, "duration");
        addValidArgument(c -> {
            var presets = TreasureParticles.getEffectManager().getPresets();
            var script = presets.read(c.effect(), c.value());
            if (!(script instanceof CircleParticle circleParticle)) return;
            c.script().style(circleParticle);
        }, true, "style");

        addValidArgument(c -> c.script().standby(ColorData.fromString(c)), "standby");
        addValidArgument(c -> c.script().success(ColorData.fromString(c)), "success");
        addValidArgument(c -> c.script().fail(ColorData.fromString(c)), "fail");

        addValidArgument(c -> c.script().configure(TreasureParticles.getEffectManager().read(c.effect(), "preset", c.value())), "configure");
        addValidArgument(c -> c.script().whenSpawned(TreasureParticles.getEffectManager().read(c.effect(), "preset", c.value())), "when-spawned");
        addValidArgument(c -> c.script().whenSucceeded(TreasureParticles.getEffectManager().read(c.effect(), "preset", c.value())), "when-succeeded");
        addValidArgument(c -> c.script().whenFailed(TreasureParticles.getEffectManager().read(c.effect(), "preset", c.value())), "when-failed");
        addValidArgument(c -> c.script().whenStarted(TreasureParticles.getEffectManager().read(c.effect(), "preset", c.value())), "when-started");

        addValidArgument(c -> c.script().immediate(StaticArgument.asBoolean(c)), "immediate");
    }

    @Override
    public Context createContext(Effect effect, String type, String line) {
        return new Context(effect, type, line);
    }

    @Override
    public boolean validate(Context c) throws ReaderException {
        if (!super.validate(c)) return false;
        var s = c.script();

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
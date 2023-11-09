package net.treasure.particles.effect.script.preset.reader;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.reader.DefaultReader;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.preset.Preset;

import java.util.ArrayList;
import java.util.List;

public class PresetReader extends DefaultReader<Script> {

    @Override
    public Script read(Effect effect, String type, String line) throws ReaderException {
        var lines = TreasureParticles.getEffectManager().getPresets().get(line);
        if (lines == null || lines.isEmpty())
            throw new ReaderException("Couldn't find any preset by name '" + line + "'");
        if (lines.size() == 1)
            return TreasureParticles.getEffectManager().readLine(effect, lines.get(0));
        else {
            List<Script> scripts = new ArrayList<>();
            for (String s : lines)
                scripts.add(TreasureParticles.getEffectManager().readLine(effect, s));
            return new Preset(scripts);
        }
    }
}
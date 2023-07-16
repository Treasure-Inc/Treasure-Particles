package net.treasure.effect.script.preset.reader;

import net.treasure.TreasureParticles;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.reader.DefaultReader;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.preset.Preset;

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
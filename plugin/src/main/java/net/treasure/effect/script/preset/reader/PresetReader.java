package net.treasure.effect.script.preset.reader;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.reader.DefaultReader;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.reader.ScriptReader;
import net.treasure.effect.script.preset.Preset;

import java.util.ArrayList;
import java.util.List;

public class PresetReader extends DefaultReader<Script> {

    @Override
    public Script read(Effect effect, String type, String line) throws ReaderException {
        var inst = TreasurePlugin.getInstance();
        var lines = inst.getEffectManager().getPresets().get(line);
        if (lines == null || lines.isEmpty())
            throw new ReaderException("Couldn't find any preset by name '" + line + "'");
        if (lines.size() == 1)
            return inst.getEffectManager().readLine(effect, lines.get(0));
        else {
            List<Script> scripts = new ArrayList<>();
            for (String s : lines)
                scripts.add(inst.getEffectManager().readLine(effect, s));
            return new Preset(scripts);
        }
    }
}
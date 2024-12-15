package net.treasure.particles.effect.script.message.reader;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.script.message.Title;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;
import net.treasure.particles.util.logging.ComponentLogger;

public class TitleReader extends ScriptReader<ReaderContext<Title>, Title> {

    @Override
    public Title read(Effect effect, String scriptType, String line) {
        var array = line.toCharArray();
        var builder = Title.builder();
        StringBuilder type = new StringBuilder(), message = new StringBuilder();
        boolean typeFound = false, split = false;
        int ignore = 0;

        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            switch (c) {
                case '&' -> {
                    if (ignore > 0) {
                        ignore--;
                        continue;
                    }
                    if (!typeFound) {
                        ComponentLogger.error(effect, "Incorrect title expression: " + line);
                        return null;
                    }
                    try {
                        if (array[i + 1] == '&' && array[i + 2] == '&')
                            split = true;
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
                case '=' -> {
                    if (!typeFound) {
                        typeFound = true;
                        continue;
                    }
                }
            }

            if (typeFound && !split)
                message.append(c);
            else if (!typeFound && c != ' ')
                type.append(c);

            if (split || i + 1 == array.length) {
                var value = message.toString().trim();
                switch (type.toString()) {
                    case "title" -> builder.title(value);
                    case "subtitle" -> builder.subtitle(value);
                    case "fadeIn" -> builder.fadeIn(Integer.parseInt(value));
                    case "stay" -> builder.stay(Integer.parseInt(value));
                    case "fadeOut" -> builder.fadeOut(Integer.parseInt(value));
                }
                ignore = 2;
                typeFound = false;
                split = false;
                type = new StringBuilder();
                message = new StringBuilder();
            }
        }
        return builder.build();
    }
}
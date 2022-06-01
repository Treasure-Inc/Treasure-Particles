package net.treasure.effect.script.sound.reader;

import net.treasure.common.Patterns;
import net.treasure.effect.Effect;
import net.treasure.effect.script.ScriptReader;
import net.treasure.effect.script.sound.PlaySound;

import java.util.regex.Matcher;

public class SoundReader implements ScriptReader<PlaySound> {

    @Override
    public PlaySound read(Effect effect, String line) {
        PlaySound script = null;

        String sound = null;
        var builder = PlaySound.builder();

        Matcher particleMatcher = Patterns.SCRIPT.matcher(line);
        while (particleMatcher.find()) {
            String key = particleMatcher.group("type");
            String _value = particleMatcher.group("value");
            if (key == null || _value == null)
                continue;
            if (key.equalsIgnoreCase("name")) {
                sound = _value;
                builder.sound(sound);
            } else if (key.equalsIgnoreCase("clientside")) {
                builder.clientSide(Boolean.parseBoolean(_value));
            } else if (key.equalsIgnoreCase("volume")) {
                try {
                    builder.volume(Float.parseFloat(_value));
                } catch (Exception ignored) {
                }
            } else if (key.equalsIgnoreCase("pitch")) {
                try {
                    builder.pitch(Float.parseFloat(_value));
                } catch (Exception ignored) {
                }
            }
        }

        if (sound != null)
            script = builder.build();

        return script;
    }
}
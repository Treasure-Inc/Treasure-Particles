package net.cladium.effect;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import lombok.Setter;
import net.cladium.color.player.ColorData;
import net.cladium.core.CladiumPlugin;
import net.cladium.effect.player.EffectData;
import net.cladium.effect.script.ParticleSpawner;
import net.cladium.effect.script.PlaySound;
import net.cladium.effect.script.Script;
import net.cladium.effect.script.Variable;
import net.cladium.util.Pair;
import net.cladium.util.TimeKeeper;
import net.cladium.util.locale.Messages;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Effect {

    private static final Pattern EVAL = Pattern.compile("^([a-zA-Z0-9]+)(\\X?)=(.+)$");
    private static final Pattern SCRIPT = Pattern.compile("(?:particle \\[|sound \\[|(?<=\\,))(?<type>\\w+)(?:=)(?<value>[a-zA-Z0-9{}=*.;_  -]+)(?:(?=\\,)|\\])");

    @Getter
    private final String key, displayName, armorColor, permission;

    @Getter
    @Setter
    private int interval = 1, times = 1;


    @Getter
    private final List<String> _lines, _postLines;

    @Getter
    private final Set<String> variables;

    public Effect(String key, String displayName, String armorColor, String permission, List<String> lines, List<String> postLines) {
        this.key = key;
        this.displayName = displayName;
        this.armorColor = armorColor;
        this.permission = permission;
        this._lines = lines;
        this._postLines = postLines;
        this.variables = new HashSet<>();
    }

    public void initialize(Player player, EffectData data) {
        for (String variable : variables)
            data.getVariables().add(new Pair<>(variable, 0D));
        readFromFile(player, data, _lines, data.getLines());
        readFromFile(player, data, _postLines, data.getPostLines());
    }

    public void doTick(Player player, EffectData data) {
        if (!TimeKeeper.isElapsed(interval))
            return;
        for (int i = 0; i < times; i++) {
            for (Script script : data.getLines())
                script.doTick(player, data);
            for (Script script : data.getPostLines())
                script.doTick(player, data);
        }
    }

    public void readFromFile(Player player, EffectData data, List<String> _lines, List<Script> lines) {
        for (String _line : _lines) {
            String line = _line;
            int interval = line.lastIndexOf("~");
            if (interval != -1) {
                String _value = line.substring(interval + 1);
                try {
                    interval = Integer.parseInt(_value);
                } catch (Exception ignored) {
                }
                line = line.substring(0, interval);
            }

            if (line.startsWith("variable ")) {

                String eval = line.substring("variable ".length());
                Matcher evalMatcher = EVAL.matcher(eval);

                if (evalMatcher.matches()) {
                    String variable = evalMatcher.group(1);
                    Pair<String, Double> pair = data.getVariable(variable);
                    if (pair != null) {
                        String operator = evalMatcher.group(2);
                        String s = evalMatcher.group(3);

                        Variable.VariableBuilder builder = Variable.builder();
                        builder.variable(variable);
                        builder.eval(s);
                        if (operator.isEmpty()) {
                            builder.operator(Variable.Operator.EQUAL);
                        } else if (operator.equalsIgnoreCase("+")) {
                            builder.operator(Variable.Operator.PLUS);
                        } else if (operator.equalsIgnoreCase("-")) {
                            builder.operator(Variable.Operator.MINUS);
                        } else if (operator.equalsIgnoreCase("*")) {
                            builder.operator(Variable.Operator.MULTIPLY);
                        } else if (operator.equalsIgnoreCase("/")) {
                            builder.operator(Variable.Operator.DIVISION);
                        }
                        lines.add(builder.build());
                    }
                }

            } else if (line.startsWith("particle ")) {

                ParticleEffect particle = null;
                String origin = null;

                ParticleSpawner.ParticleSpawnerBuilder builder = ParticleSpawner.builder();

                Matcher particleMatcher = SCRIPT.matcher(line);
                while (particleMatcher.find()) {
                    String key = particleMatcher.group("type");
                    String _value = particleMatcher.group("value");
                    if (key == null || _value == null)
                        continue;
                    if (key.equalsIgnoreCase("effect")) {
                        try {
                            particle = ParticleEffect.valueOf(_value.toUpperCase(Locale.ROOT));
                            builder.effect(particle);
                        } catch (Exception ignored) {
                        }
                    } else if (key.equalsIgnoreCase("from")) {
                        if (_value.startsWith("head")) {
                            origin = "head";
                            builder.from(origin);
                            String[] s = _value.split("\\*");
                            if (s.length == 2) {
                                try {
                                    builder.multiplier(Float.parseFloat(s[1]));
                                } catch (Exception ignored) {
                                }
                            }
                        } else if (_value.startsWith("feet")) {
                            origin = "feet";
                            builder.from(origin);
                            String[] s = _value.split("\\*");
                            if (s.length == 2) {
                                try {
                                    builder.multiplier(Float.parseFloat(s[1]));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } else if (key.equalsIgnoreCase("colorScheme")) {
                        builder.colorData(ColorData.initialize(_value));
                    } else if (key.equalsIgnoreCase("offset")) {
                        Matcher offsetMatcher = Pattern.compile("(|(?<=;))(?<type>offsetX|offsetY|offsetZ)(?:=)(?<value>[a-zA-Z0-9{}.]+)(?:(?=;)|)").matcher(_value);
                        while (offsetMatcher.find()) {
                            String _type = offsetMatcher.group("type");
                            String _offsetValue = offsetMatcher.group("value");
                            try {
                                if (_type.equalsIgnoreCase("offsetX"))
                                    builder.offsetX(Double.parseDouble(_offsetValue));
                                else if (_type.equalsIgnoreCase("offsetY"))
                                    builder.offsetY(Double.parseDouble(_offsetValue));
                                else if (_type.equalsIgnoreCase("offsetZ"))
                                    builder.offsetZ(Double.parseDouble(_offsetValue));
                            } catch (Exception ignored) {
                            }
                        }
                    } else if (key.equalsIgnoreCase("direction")) {
                        builder.direction(Boolean.parseBoolean(_value));
                    } else if (key.equalsIgnoreCase("x"))
                        builder.x(_value.substring(1, _value.length() - 1));
                    else if (key.equalsIgnoreCase("y"))
                        builder.y(_value.substring(1, _value.length() - 1));
                    else if (key.equalsIgnoreCase("z"))
                        builder.z(_value.substring(1, _value.length() - 1));
                    else if (key.equalsIgnoreCase("amount")) {
                        try {
                            builder.amount(Integer.parseInt(_value));
                        } catch (Exception ignored) {
                        }
                    } else if (key.equalsIgnoreCase("speed")) {
                        try {
                            builder.speed(Float.parseFloat(_value));
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (particle != null && origin != null)
                    lines.add(builder.build());

            } else if (line.startsWith("sound ")) {

                String sound = null;
                PlaySound.PlaySoundBuilder builder = PlaySound.builder();

                Matcher particleMatcher = SCRIPT.matcher(line);
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
                    lines.add(builder.build());
            }
        }
    }

    public static ContextResolver<Effect, BukkitCommandExecutionContext> getContextResolver() {
        return (c) -> {
            String key = c.popFirstArg();
            Effect effect = CladiumPlugin.getInstance().getEffectManager().get(key);
            if (effect != null) {
                return effect;
            } else {
                throw new InvalidCommandArgument(String.format(Messages.EFFECT_UNKNOWN, key));
            }
        };
    }
}
package net.treasure.effect;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import lombok.Setter;
import net.treasure.color.player.ColorData;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.player.EffectData;
import net.treasure.effect.script.ParticleSpawner;
import net.treasure.effect.script.PlaySound;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.Variable;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import net.treasure.util.locale.Messages;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;

public class Effect {

    @Getter
    private final String key, displayName, armorColor, permission;

    @Getter
    @Setter
    private int interval = 1, times = 1, postTimes = 1;


    @Getter
    private final List<String> _lines, _postLines;

    @Getter
    private final Set<Pair<String, Double>> variables;

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
        for (var pair : getVariables())
            data.getVariables().add(new Pair<>(pair.getKey(), pair.getValue()));
        readFromFile(player, data, _lines, data.getLines());
        readFromFile(player, data, _postLines, data.getPostLines());
    }

    public void doTick(Player player, EffectData data) {
        if (!TimeKeeper.isElapsed(interval))
            return;
        try {
            for (int i = 0; i < times; i++)
                for (Script script : data.getLines())
                    script.doTick(player, data);
        } finally {
            for (int i = 0; i < postTimes; i++)
                for (Script script : data.getPostLines())
                    script.doTick(player, data);
        }
    }

    public void addVariable(String var) {
        Matcher matcher = Patterns.VARIABLE.matcher(var);
        if (matcher.matches()) {
            String key = matcher.group("name");
            try {
                double value = Double.parseDouble(matcher.group("default"));
                this.variables.add(new Pair<>(key, value));
            } catch (NumberFormatException e) {
                this.getVariables().add(new Pair<>(key, 0d));
            }
        } else
            this.getVariables().add(new Pair<>(var, 0d));
    }

    public void readFromFile(Player player, EffectData data, List<String> _lines, List<Script> lines) {
        for (String _line : _lines) {
            String line = _line;
            int interval = 1;
            int intervalIndex = line.lastIndexOf("~");
            if (intervalIndex != -1) {
                String _value = line.substring(intervalIndex + 1);
                try {
                    interval = Integer.parseInt(_value);
                } catch (Exception ignored) {
                }
                line = line.substring(0, intervalIndex);
            }

            if (line.startsWith("variable ")) {

                String eval = line.substring("variable ".length());
                Matcher evalMatcher = Patterns.EVAL.matcher(eval);

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
                        Variable v = builder.build();
                        v.setInterval(interval);
                        lines.add(v);
                    }
                }

            } else if (line.startsWith("particle ")) {

                ParticleEffect particle = null;
                String origin = null;

                ParticleSpawner.ParticleSpawnerBuilder builder = ParticleSpawner.builder();

                Matcher particleMatcher = Patterns.SCRIPT.matcher(line);
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
                        Matcher offsetMatcher = Patterns.OFFSET.matcher(_value);
                        while (offsetMatcher.find()) {
                            String _type = offsetMatcher.group("type");
                            String _offsetValue = offsetMatcher.group("value");
                            try {
                                Double.parseDouble(_offsetValue);
                            } catch (NumberFormatException e) {
                                _offsetValue = _offsetValue.substring(1, _offsetValue.length() - 1);
                            }
                            try {
                                if (_type.equalsIgnoreCase("x"))
                                    builder.offsetX(_offsetValue);
                                else if (_type.equalsIgnoreCase("y"))
                                    builder.offsetY(_offsetValue);
                                else if (_type.equalsIgnoreCase("z"))
                                    builder.offsetZ(_offsetValue);
                            } catch (Exception ignored) {
                            }
                        }
                    } else if (key.equalsIgnoreCase("direction")) {
                        builder.direction(Boolean.parseBoolean(_value));
                    } else if (key.equalsIgnoreCase("amount")) {
                        try {
                            builder.amount(Integer.parseInt(_value));
                        } catch (Exception ignored) {
                        }
                    } else if (key.equalsIgnoreCase("speed")) {
                        try {
                            builder.speed(Float.parseFloat(_value));
                        } catch (Exception ignored) {
                        }
                    } else {
                        boolean negative = _value.startsWith("-");
                        String substring = (negative ? "-" : "") + _value.substring(negative ? 2 : 1, _value.length() - 1);
                        if (key.equalsIgnoreCase("x"))
                            builder.x(substring);
                        else if (key.equalsIgnoreCase("y"))
                            builder.y(substring);
                        else if (key.equalsIgnoreCase("z"))
                            builder.z(substring);
                    }
                }
                if (particle != null && origin != null) {
                    ParticleSpawner spawner = builder.build();
                    spawner.setInterval(interval);
                    lines.add(spawner);
                }

            } else if (line.startsWith("sound ")) {

                String sound = null;
                PlaySound.PlaySoundBuilder builder = PlaySound.builder();

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
                if (sound != null) {
                    PlaySound playSound = builder.build();
                    playSound.setInterval(interval);
                    lines.add(playSound);
                }
            }
        }

    }

    public static ContextResolver<Effect, BukkitCommandExecutionContext> getContextResolver() {
        return (c) -> {
            String key = c.popFirstArg();
            Effect effect = TreasurePlugin.getInstance().getEffectManager().get(key);
            if (effect != null) {
                return effect;
            } else {
                throw new InvalidCommandArgument(String.format(Messages.EFFECT_UNKNOWN, key));
            }
        };
    }
}
package net.treasure.effect;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import net.treasure.color.data.ColorData;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.ParticleSpawner;
import net.treasure.effect.script.PlaySound;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.Variable;
import net.treasure.effect.script.basic.EmptyScript;
import net.treasure.effect.script.basic.ReturnScript;
import net.treasure.effect.script.conditional.ConditionalScript;
import net.treasure.effect.script.conditional.reader.ConditionReader;
import net.treasure.effect.script.message.ActionBar;
import net.treasure.effect.script.message.ChatMessage;
import net.treasure.effect.script.message.reader.TitleReader;
import net.treasure.effect.script.preset.Preset;
import net.treasure.locale.Messages;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class Effect {

    @Getter
    private final String key, displayName, armorColor, permission;

    @Getter
    private final int interval, times, postTimes;

    private final List<Script> lines;
    private final List<Script> postLines;

    @Getter
    private final Set<Pair<String, Double>> variables;

    @Getter
    private final boolean enableCaching;
    @Getter
    private double[][] cache, cachePost;

    public Effect(String key, String displayName, String armorColor, String permission, List<String> lines, List<String> postLines, List<String> variables, int interval, int times, int postTimes, boolean enableCaching) {
        this.key = key;
        this.displayName = displayName;
        this.armorColor = armorColor;
        this.permission = permission;
        this.interval = interval;
        this.times = times;
        this.postTimes = postTimes;
        this.enableCaching = enableCaching;

        this.variables = new HashSet<>();
        this.lines = new ArrayList<>();
        this.postLines = new ArrayList<>();

        for (String var : variables)
            if (checkVariable(var))
                addVariable(var);

        readFromFile(lines, this.lines, false);
        readFromFile(postLines, this.postLines, true);

        if (times > 1 || postTimes > 1)
            addVariable("i");

        if (enableCaching) {
            if (!lines.isEmpty())
                cache = new double[times][variables.size()];
            if (!postLines.isEmpty())
                cachePost = new double[postTimes][variables.size()];
            preTick();
        }
    }

    public boolean canUse(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public void initialize(Player player, EffectData data) {
        for (var pair : variables)
            data.getVariables().add(new Pair<>(pair.getKey(), pair.getValue()));
        data.setLines(new ArrayList<>());
        for (Script script : lines)
            data.getLines().add(script.cloneScript());
        data.setPostLines(new ArrayList<>());
        for (Script script : postLines)
            data.getPostLines().add(script.cloneScript());
    }

    public void preTick() {
        Set<Pair<String, Double>> variables = new HashSet<>(this.variables);
        var timesPairOptional = variables.stream().filter(pair -> pair.getKey().equalsIgnoreCase("i")).findFirst();
        Pair<String, Double> timesPair = null;
        if (timesPairOptional.isPresent())
            timesPair = timesPairOptional.get();
        try {
            if (!lines.isEmpty()) {
                for (int i = 0; i < times; i++) {
                    if (timesPair != null)
                        timesPair.setValue((double) i);
                    for (Script script : lines) {
                        if (script instanceof Variable variable) {
                            cache[i][variable.getIndex()] = variable.preTick(variables);
                        }
                    }
                }
            }
        } finally {
            if (!postLines.isEmpty()) {
                for (int i = 0; i < postTimes; i++) {
                    if (timesPair != null)
                        timesPair.setValue((double) i);
                    for (Script script : postLines) {
                        if (script instanceof Variable variable) {
                            variable.preTick(variables);
                            cachePost[i][variable.getIndex()] = variable.preTick(variables);
                        }
                    }
                }
            }
        }
    }

    public void doTick(Player player, EffectData data) {
        if (!TimeKeeper.isElapsed(interval))
            return;
        var timings = TreasurePlugin.timing("Effect: " + key + ", Player: " + player.getName());
        try {
            timings.startTiming();
            var timesPair = data.getVariable(player, "i");
            try {
                for (int i = 0; i < times; i++) {
                    if (timesPair != null)
                        timesPair.setValue((double) i);
                    for (Script script : data.getLines())
                        if (!script.doTick(player, data, i))
                            break;
                }
            } finally {
                for (int i = 0; i < postTimes; i++) {
                    if (timesPair != null)
                        timesPair.setValue((double) i);
                    for (Script script : data.getPostLines())
                        if (!script.doTick(player, data, i))
                            break;
                }
            }
        } finally {
            timings.stopTiming();
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

    public boolean hasVariable(String var) {
        for (var pair : variables)
            if (pair.getKey().equals(var))
                return true;
        return false;
    }

    public boolean checkVariable(String var) {
        return switch (var) {
            case "i", "PI", "TICK", "RANDOM", "playerYaw", "playerPitch", "playerX", "playerY", "playerZ" -> false;
            default -> true;
        };
    }

    public void readFromFile(List<String> _lines, List<Script> lines, boolean post) {
        int varIndex = 0;
        for (String line : _lines) {
            var pair = readLine(line, varIndex, false);
            var script = pair.getKey();
            varIndex = pair.getValue();
            if (script != null) {
                script.setPostLine(post);
                lines.add(script);
            } else
                TreasurePlugin.logger().log(Level.WARNING, "Couldn't read line: " + line);
        }
    }

    public Pair<Script, Integer> readLine(String line, int varIndex, boolean inLine) {
        if (line.equalsIgnoreCase("none"))
            return new Pair<>(new EmptyScript(), varIndex);
        else if (line.equalsIgnoreCase("return"))
            return new Pair<>(new ReturnScript(), varIndex);

        var inst = TreasurePlugin.getInstance();

        int interval = -1;
        int intervalIndex = line.lastIndexOf("~");
        if (intervalIndex != -1) {
            var args = Patterns.TILDE.split(line, 2);
            try {
                interval = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                inst.getLogger().warning("Invalid interval syntax: " + line);
            }
            line = args[0];
        }

        Script script = null;

        var args = Patterns.SPACE.split(line, 2);
        String type;
        try {
            type = args[0];
        } catch (Exception e) {
            return new Pair<>(null, varIndex);
        }

        if (args.length == 1)
            return new Pair<>(null, varIndex);

        switch (type) {
            case "conditional" -> {
                Matcher matcher = Patterns.CONDITIONAL.matcher(line);
                if (matcher.matches()) {
                    try {
                        var condition = matcher.group("condition");
                        var parent = new ConditionReader(inst).read(condition);
                        if (parent == null)
                            return new Pair<>(null, varIndex);

                        var firstExpr = matcher.group("first");
                        var secondExpr = matcher.group("second");

                        script = new ConditionalScript(
                                parent,
                                readLine(firstExpr, varIndex, true).getKey(),
                                readLine(secondExpr, varIndex, true).getKey()
                        );
                    } catch (Exception e) {
                        return new Pair<>(null, varIndex);
                    }
                }
            }
            case "variable" -> {
                String eval = args[1];
                Matcher evalMatcher = Patterns.EVAL.matcher(eval);

                if (evalMatcher.matches()) {
                    String variable = evalMatcher.group(1);
                    if (hasVariable(variable)) {
                        String operator = evalMatcher.group(2);
                        String s = evalMatcher.group(3);

                        Variable.VariableBuilder builder = Variable.builder();
                        builder.variable(variable);
                        builder.index(varIndex);
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
                        script = builder.build();
                        if (!inLine)
                            varIndex++;
                    }
                }
            }
            case "particle" -> {

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
                        } catch (Exception | ExceptionInInitializerError | NoClassDefFoundError ignored) {
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
                if (particle != null && origin != null)
                    script = builder.build();
            }
            case "sound" -> {

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
                if (sound != null)
                    script = builder.build();
            }
            case "preset" -> {
                var lines = inst.getEffectManager().getPresets().get(args[1]);
                if (lines == null || lines.isEmpty())
                    return new Pair<>(null, varIndex);
                if (lines.size() == 1)
                    script = readLine(lines.get(0), varIndex, true).getKey();
                else {
                    List<Script> scripts = new ArrayList<>();
                    int finalVarIndex = varIndex;
                    lines.forEach(s -> scripts.add(readLine(s, finalVarIndex, true).getKey()));
                    script = new Preset(scripts);
                }
            }
            case "actionbar" -> {
                String message = args[1];
                script = new ActionBar(message);
            }
            case "chat" -> {
                String message = args[1];
                script = new ChatMessage(message);
            }
            case "title" -> {
                String message = args[1];
                script = new TitleReader().read(message);
            }
        }

        if (script != null && interval > 0)
            script.setInterval(interval);
        return new Pair<>(script, varIndex);
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
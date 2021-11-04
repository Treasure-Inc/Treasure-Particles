import lombok.Getter;
import lombok.Setter;
import net.cladium.color.ColorManager;
import net.cladium.color.GradientColor;
import net.cladium.color.player.ColorData;
import net.cladium.effect.player.EffectData;
import net.cladium.util.MathUtil;
import net.cladium.util.Pair;
import net.cladium.util.TimeKeeper;
import org.bukkit.Particle;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestEffect {

    private static final Pattern EVAL = Pattern.compile("^([a-z])(\\X?)=(.+)$");
    private static final Pattern PARTICLE = Pattern.compile("(?:particle\\{(?<particle>.+)\\} \\[|(?<=\\,))(?<type>\\w+)(?:=)(?<value>[a-zA-Z0-9{}=.-]+)(?:(?=\\,)|\\])");
    private static final Pattern COLOR = Pattern.compile("(|(?<=\\-))(?<type>name|revertWhenDone|speed)(?:=)(?<value>[a-zA-Z0-9{}.]+)(?:(?=\\-)|)");

    @Getter
    private final String key;

    @Getter
    @Setter
    private int interval = 1, colorIndex = 0;

    @Getter
    private final List<String> lines;

    @Getter
    private final Set<Pair<String, Object>> variables;

    public TestEffect(String key, List<String> lines) {
        this.key = key;
        this.lines = lines;
        this.variables = new HashSet<>();

        manager.getColors().add(new GradientColor(
                "redtogreen",
                10,
                Color.BLUE, Color.RED
        ));
        manager.getColors().add(new GradientColor(
                "redtogreen2",
                10,
                Color.BLUE, Color.RED
        ));
    }

    EffectData data = new EffectData();
    ColorManager manager = new ColorManager();

    public void tick() {
        if (!TimeKeeper.isElapsed(interval))
            return;
        if (lines.isEmpty())
            return;
        System.out.println("----------------------------------------");
        for (String line : lines) {

            int interval = line.lastIndexOf("~");
            if (interval != -1) {
                String _value = line.substring(interval + 1);
                try {
                    int value = Integer.parseInt(_value);
                    if (!TimeKeeper.isElapsed(value))
                        continue;
                } catch (Exception ignored) {
                }
                line = line.substring(0, interval);
            }

            // VARIABLE UPDATER
            if (line.startsWith("variable ")) {
                System.out.println("> found variable");
                String eval = line.substring("variable ".length());
                Matcher evalMatcher = EVAL.matcher(eval);
                if (evalMatcher.matches()) {
                    String variable = evalMatcher.group(1);
                    Pair<String, Object> pair = getVariable(variable);
                    if (pair != null) {
                        String operator = evalMatcher.group(2);
                        Double oldValue = (Double) pair.getValue();
                        String s = evalMatcher.group(3)
                                .replaceAll("\\{TICK}", String.valueOf(TimeKeeper.getTimeElapsed()))
                                .replaceAll("\\{PI}", "180");
                        if (s.contains("{")) {
                            for (Pair<String, Object> p : variables) {
                                if (s.contains("{" + p.getKey() + "}")) {
                                    s = s.replaceAll("\\{" + p.getKey() + "}", String.valueOf((double) p.getValue()));
                                }
                            }
                        }
                        Double value = MathUtil.eval(s);
                        if (operator.isEmpty()) {
                            pair.setValue(value);
                        } else if (operator.equalsIgnoreCase("+")) {
                            value = (double) pair.getValue() + value;
                            pair.setValue(value);
                        } else if (operator.equalsIgnoreCase("-")) {
                            value = (double) pair.getValue() - value;
                            pair.setValue(value);
                        } else if (operator.equalsIgnoreCase("*")) {
                            value = (double) pair.getValue() * value;
                            pair.setValue(value);
                        } else if (operator.equalsIgnoreCase("/")) {
                            value = (double) pair.getValue() / value;
                            pair.setValue(value);
                        }
                        System.out.println("old value: " + oldValue);
                        System.out.println("new value: " + pair.getValue());
                    }
                }
                continue;
            }

            // SPAWN PARTICLE
            if (line.startsWith("particle")) {

                Particle particle = Particle.REDSTONE;
                Color color = null;
                String origin = null;
                double x = 0, y = 0, z = 0;
                int amount = 1;

                Matcher particleMatcher = PARTICLE.matcher(line);
                while (particleMatcher.find()) {
                    String _particle = particleMatcher.group("particle");
                    if (_particle != null) {
                        try {
                            System.out.println("particle type: " + _particle);
                        } catch (Exception ignored) {
                        }
                    }
                    String key = particleMatcher.group(2);
                    String _value = particleMatcher.group(3);
                    double value = Double.MIN_VALUE;
                    if (key == null || _value == null)
                        continue;
                    if (_value.startsWith("{") || _value.startsWith("-{")) {
                        _value = _value.substring(1 + (_value.startsWith("-") ? 1 : 0), _value.length() - 1);
                        System.out.println(_value);
                        try {
                            value = (double) getVariable(_value).getValue();
                            System.out.println("{" + _value + "}: " + value);
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            value = Double.parseDouble(_value);
                            System.out.println("value: " + value);
                        } catch (Exception ignored) {
                        }
                    }
                    if (value == Double.MIN_VALUE) {
                        if (key.equalsIgnoreCase("from")) {
                            if (_value.equalsIgnoreCase("head")) {
                                origin = "origin: head";
                            } else if (_value.equalsIgnoreCase("feet")) {
                                origin = "origin: feet";
                            }
                        } else if (key.equalsIgnoreCase("colorScheme")) {
                            System.out.println("color scheme found");
                            ColorData colorData = null;
                            color = colorData.next();
                            colorIndex++;
                        }
                    } else {
                        if (key.equalsIgnoreCase("x"))
                            x = value;
                        else if (key.equalsIgnoreCase("y"))
                            y = value;
                        else if (key.equalsIgnoreCase("z"))
                            z = value;
                        else if (key.equalsIgnoreCase("amount"))
                            amount = (int) value;
                        System.out.println("set " + key + " to " + value);
                    }
                }
                if (origin == null)
                    continue;
                System.out.println("particle spawned:");
                System.out.println("- type: " + particle);
                System.out.println("- color: " + color.getRGB());
                System.out.println("- amount: " + amount);
                System.out.println("- location: " + origin + "+(" + x + "," + y + "," + z + ")");
            }
        }
        colorIndex = 0;
    }

    private Pair<String, Object> getVariable(String variable) {
        return variables.stream().filter(pair -> pair.getKey().equals(variable)).findFirst().orElse(null);
    }
}
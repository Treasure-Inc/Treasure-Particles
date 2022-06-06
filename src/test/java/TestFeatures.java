import com.google.gson.GsonBuilder;
import net.treasure.common.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.conditional.Condition;
import net.treasure.effect.script.conditional.ConditionGroup;
import net.treasure.effect.script.conditional.reader.ConditionReader;
import net.treasure.util.MathUtil;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
import net.treasure.util.color.Rainbow;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;

public class TestFeatures {

    @Test
    public void testReader() {
        var reader = new ConditionReader(null);
        var parent = reader.read(null, "((((p==1 || p==0) && (q==0 || q==1)) && ((a>1 && a!=3) || (b==4 && b>=99))) && (c==5 || c!=5))");
//        var parent = reader.read("((p==1 && q==1) || (r==0 && s==0))");
//        var parent = reader.read("(p==1 && q==1 && r==1)");
        System.out.println("-----RESULTS " + parent.inner.size());
        var gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(parent));
    }

    @Test
    public void testReaderResult() {
        ConditionGroup parent = new ConditionGroup();
        parent.multiGroup = true;
        parent.operators.add(ConditionGroup.Operator.AND);
        parent.conditions.add(new Condition(true));
        parent.conditions.add(new Condition(false));
        parent.operators.add(ConditionGroup.Operator.AND);
        System.out.println("Result: " + parent.test(null, null));
    }

    @Test
    public void testConditionGroups() {
        ConditionGroup parent = new ConditionGroup();
        parent.multiGroup = true;
        parent.operators.add(ConditionGroup.Operator.OR);

        ConditionGroup g1 = new ConditionGroup();
        g1.multiGroup = true;
        g1.parent = parent;
        g1.conditions = List.of(new Condition(true), new Condition(true));
        g1.operators.add(ConditionGroup.Operator.AND);
        parent.inner.add(g1);

        ConditionGroup g2 = new ConditionGroup();
        g2.multiGroup = true;
        g2.parent = parent;
        g2.conditions = List.of(new Condition(false), new Condition(false));
        g2.operators.add(ConditionGroup.Operator.OR);
        parent.inner.add(g2);

        System.out.println("-----Result: " + parent.test(null, null));
    }

    @Test
    public void testParse() throws ParseException {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        System.out.println(df.parse("6.123233995736766E").doubleValue());
    }

    @Test
    public void testMath() {
        System.out.println(2 * Math.PI / 16);
        System.out.println("Math.atan2: " + (Math.atan2(3, 4)));
        System.out.println("MathUtil.eval: " + MathUtil.eval("atan(3,4)"));
        System.out.println("Math.cos: " + MathUtil.cos(0 + (Math.PI * 2 * ((double) 1 / 3))));
        System.out.println("MathUtil.eval: " + MathUtil.eval("cos(0 + (" + Math.PI + " * 2 * (1 / 3)))"));
    }

    @Test
    public void testReplace() {
        final Set<Pair<String, Double>> variables = new HashSet<>();
        variables.add(new Pair<>("phase", 150.0547789798));
        EffectData data = new EffectData(variables);
        String eval = replaceVariables(data, "actionbar {%,.2f:phase}");
        System.out.println("Result: " + eval);
    }

    public String replaceVariables(EffectData data, String line) {
        StringBuilder builder = new StringBuilder();

        var array = line.toCharArray();
        int startPos = -1;
        StringBuilder variable = new StringBuilder();
        StringBuilder format = new StringBuilder();
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];
            switch (c) {
                case '{' -> {
                    if (startPos != -1) {
                        return null;
                    }
                    startPos = pos;
                }
                case '}' -> {
                    if (startPos == -1) {
                        return null;
                    }
                    var result = variable.toString();
                    var p = data.getVariable(null, result);
                    double value;
                    if (p == null) {
                        Double preset = switch (result) {
                            case "TICK" -> (double) TimeKeeper.getTimeElapsed();
                            case "PI" -> Math.PI;
                            case "RANDOM" -> Math.random();
                            default -> null;
                        };
                        if (preset == null) break;
                        value = preset;
                    } else
                        value = p.getValue();

                    if (!format.isEmpty())
                        builder.append(String.format(format.toString(), value));
                    else
                        builder.append(value);

                    startPos = -1;
                    variable = new StringBuilder();
                    format = new StringBuilder();
                }
                case ':' -> {
                    if (startPos != -1) {
                        format = variable;
                        variable = new StringBuilder();
                    } else
                        builder.append(c);
                }
                default -> {
                    if (startPos != -1)
                        variable.append(c);
                    else
                        builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    @Test
    public void testPatterns() {
        Matcher matcher = Patterns.SCRIPT.matcher("particle [effect=end_rod,amount=0,offset={x={xAng};y=0;z={zAng}},direction=true,speed=0.375]");
        while (matcher.find()) {
            System.out.println(matcher.group("type") + " --> " + matcher.group("value"));
        }
        String offset = "{x={xAng};y=0;z={zAng}}";
        matcher = Patterns.INNER_SCRIPT.matcher(offset);
        while (matcher.find())
            System.out.println(matcher.group("type") + " --> " + matcher.group("value"));
    }

    @Test
    public void testSubstring() {
        String s = "variable i=0 ~20";
        System.out.println(s.lastIndexOf("~"));
        System.out.println(s.substring(0, s.lastIndexOf("~")));
    }

    @Test
    public void testRGB() {
        System.out.println(Color.RED.getRGB());
        System.out.println(org.bukkit.Color.RED.asRGB());
    }

    @Test
    public void testColor() {
        Rainbow rainbow = new Rainbow();
        Color[] colors = rainbow.colors(15);
        for (Color color : colors) {
            System.out.println(color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
        }
    }
}
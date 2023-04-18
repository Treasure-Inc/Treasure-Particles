import com.google.gson.GsonBuilder;
import net.treasure.common.Patterns;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.conditional.Condition;
import net.treasure.effect.script.conditional.ConditionGroup;
import net.treasure.effect.script.conditional.reader.ConditionReader;
import net.treasure.util.math.MathUtils;
import net.treasure.util.tuples.Pair;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestFeatures {

    @Test
    public void testLoop() {
        for (var th : List.of("th1", "th2", "th3")) {
            for (var times = 0; times < 3; times++) {
                for (int script = 0; script < 5; script++) {
                    System.out.println(th + "." + times + "." + script);
                    if (th.equals("th1") && script == 1)
                        break;
                }
                System.out.println(th + "." + times + "--");
            }
            System.out.println("-------");
        }
    }

    @Test
    public void testReader() {
        final List<Pair<String, Double>> variables = new ArrayList<>();
        variables.add(new Pair<>("p", 150d));

        var reader = new ConditionReader(null);
//        var parent = reader.read(null, "((((p==1 || p==0) && (q==0 || q==1)) && ((a>1 && a!=3) || (b==4 && b>=99))) && (c==5 || c!=5))");
//        var parent = reader.read(null,"((p==1 && q==1) || (r==0 && s==0))");
        var parent = reader.read(null, null, "({p}/10==15)");
        System.out.println("Test Result: " + parent.test(null, new EffectData(variables)));
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
        System.out.println("MathUtil.eval: " + MathUtils.eval("atan(3,4)"));
        System.out.println("Math.cos: " + MathUtils.cos(0 + (Math.PI * 2 * ((double) 1 / 3))));
        System.out.println("MathUtil.eval: " + MathUtils.eval("cos(0 + (" + Math.PI + " * 2 * (1 / 3)))"));
    }

    @Test
    public void testReplace() {
        final List<Pair<String, Double>> variables = new ArrayList<>();
        variables.add(new Pair<>("phase", 150.0547789798));
        EffectData data = new EffectData(variables);
        System.out.println("Result: " + data.replaceVariables("actionbar {#.##:phase}"));
    }

    @Test
    public void testPatterns() {
        var matcher = Patterns.SCRIPT.matcher("[effect=sweep_attack,amount=0,speed=1,offset={x={size};y=10},pos={y=0.5},origin=feet,color=#FFFFFF]");
        while (matcher.find()) {
            var value = matcher.group("value");
            System.out.println(matcher.group("type") + " --> " + value);
            var inner = Patterns.INNER_SCRIPT.matcher(value);
            System.out.println("{");
            while (inner.find()) {
                System.out.println(inner.group("type") + " --> " + inner.group("value"));
            }
            System.out.println("}");
        }
    }

    @Test
    public void testMatcher() {
        var matcher = Patterns.EVAL.matcher("asd*=123");
        if (matcher.matches()) {
            var variable = matcher.group(1);
            System.out.println(variable + " [" + matcher.start() + matcher.end() + "]");
            var operator = matcher.group(2);
            System.out.println(operator + " [" + matcher.start() + matcher.end() + "]");
            var value = matcher.group(1);
            System.out.println(value + " [" + matcher.start() + matcher.end() + "]");
        }
    }

    @Test
    public void testSubstring() {
        String s = "variable i=0 ~20";
        System.out.println(s.lastIndexOf("~"));
        System.out.println(s.substring(0, s.lastIndexOf("~")));
    }
}
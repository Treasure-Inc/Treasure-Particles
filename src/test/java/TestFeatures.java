import com.google.gson.GsonBuilder;
import net.treasure.common.Patterns;
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
import java.util.regex.Pattern;

public class TestFeatures {

    private boolean validate(Matcher matcher, String group) {
        try {
            return matcher.start(group) != -1;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void testReader() {
        var reader = new ConditionReader(null);
//        var groups = reader.read("((p==1) && (((a>1 && a!=3) && (a==4 || q>=99)) && (c>1 || d<2)))");
        var groups = reader.read("((p==1 && q==1) || (r==0 && s==0))");
        System.out.println("Size: " + groups.size());
        System.out.println("-----RESULTS");
        var gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(groups));
    }

    @Test
    public void testReaderResult() {
        ConditionGroup parent = new ConditionGroup();
        parent.multiGroup = true;
        parent.operators.add(ConditionGroup.Operator.AND);
        parent.conditions.add(new Condition(true));
        parent.conditions.add(new Condition(false));
        parent.operators.add(ConditionGroup.Operator.AND);
        System.out.println("Result: " + parent.test(null));
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

        System.out.println("-----Result: " + parent.test(null));
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

//        System.out.println("Math.cos: " + MathUtil.cos(Math.toRadians(90) + Math.toRadians(45)));
//        System.out.println("MathUtil.cos: " + MathUtil.eval("cos(" + Math.toRadians(90) + "+" + Math.toRadians(45) + " / 1 * 3)"));
//        System.out.println("MathUtil.eval: " + MathUtil.eval("(0 + " + Math.PI + " / 20 ) % 40"));
//        System.out.println("Normal: " + ((0 + Math.PI * 2 / 40) % 40));
    }

    @Test
    public void testEval() {
        final Set<Pair<String, Double>> variables = new HashSet<>();
        variables.add(new Pair<>("x", 3.78786));
        String eval = "{PI} * 2 + {x} / 5";
        String _eval = eval
                .replaceAll("\\{TICK}", String.valueOf(TimeKeeper.getTimeElapsed()))
                .replaceAll("\\{PI}", String.valueOf(MathUtil.PI));
        if (_eval.contains("{")) {
            for (Pair<String, Double> p : variables) {
                if (_eval.contains("{" + p.getKey() + "}")) {
                    _eval = _eval.replaceAll("\\{" + p.getKey() + "}", String.format("%.2f", p.getValue()));
                }
            }
        }
        System.out.println("Eval: " + _eval);
        System.out.println("Result: " + MathUtil.eval(_eval));
    }

    @Test
    public void testPatterns() {
        Matcher matcher = Patterns.SCRIPT.matcher("particle [effect=end_rod,amount=0,offset={x={xAng};y=0;z={zAng}},direction=true,speed=0.375]");
        while (matcher.find()) {
            System.out.println(matcher.group("type") + " --> " + matcher.group("value"));
        }
        String offset = "{x={xAng};y=0;z={zAng}}";
        matcher = Patterns.OFFSET.matcher(offset);
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
        /*
        ColorData data = new ColorData(new GradientColor(
                "redtogreen",
                10,
                Color.BLUE, Color.RED
        ), 1, true);
        for (int i = 0; i <= 10; i++) {
            Color color = data.next();
            System.out.println(color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
        }
         */
    }

    @Test
    public void testGroup() {
        Pattern PARTICLE = Pattern.compile("(?:particle\\{(?<particle>.+)\\} \\[|(?<=\\,))(?<type>\\w+)(?:=)(?<value>[a-zA-Z0-9{}:-]+)(?:(?=\\,)|\\])");
        Matcher evalMatcher = PARTICLE.matcher("particle{DUST} [x={x},y={y},z={z},from=feet] ~3");
        int counter = 1;
        while (evalMatcher.find()) {
            System.out.println(counter++);
            System.out.println(evalMatcher.group("particle"));
            System.out.println("1: " + evalMatcher.group(1));
            System.out.println("2: " + evalMatcher.group(2));
            System.out.println("3: " + evalMatcher.group(3));
            System.out.println("____");
        }
    }
}
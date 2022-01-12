import net.treasure.common.Patterns;
import net.treasure.util.MathUtil;
import net.treasure.util.color.Rainbow;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {

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

//    @Test
//    @SneakyThrows
//    public void testEffect() {
//        TestEffect effect = new TestEffect("osuruk", Arrays.asList(
//                "variable r+={PI}/60",
//                "variable x=cos({r})",
//                "variable z=sin({r})",
//                "particle{REDSTONE} [x=-{x},z={z},from=head,colorScheme={name=redtogreen2-revertWhenDone=true-speed=1.75}]",
//                "particle{REDSTONE} [x=-{x},z={z},from=head,colorScheme={name=redtogreen-revertWhenDone=true-speed=1.75}]"
//        ));
//        effect.getVariables().add(new Pair<>("x", 0D));
//        effect.getVariables().add(new Pair<>("z", 0D));
//        effect.getVariables().add(new Pair<>("r", 0D));
//        for (int i = 0; i < 40; i++) {
//            effect.tick();
//            TimeKeeper.increaseTime();
//            Thread.sleep(50);
//        }
//    }
}
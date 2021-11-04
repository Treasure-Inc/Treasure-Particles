import lombok.SneakyThrows;
import net.cladium.util.Pair;
import net.cladium.util.TimeKeeper;
import net.cladium.util.color.Rainbow;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CladiumTester {

    @Test
    public void testMath() {
        for (int i = 0; i < 28; i++) {
            if (i % 7 == 0)
                System.out.println("-------------------------------------");
            System.out.println("[" + i + "] Result: " + ((i % 7) + 1) + ", Slot: " + (i / 7 + 1) + ", Where: " + ((i / 7 + 1) * 9 + (i % 7) + 1));
        }
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

    @Test
    @SneakyThrows
    public void testEffect() {
        TestEffect effect = new TestEffect("osuruk", Arrays.asList(
                "variable r+={PI}/60",
                "variable x=cos({r})",
                "variable z=sin({r})",
                "particle{REDSTONE} [x=-{x},z={z},from=head,colorScheme={name=redtogreen2-revertWhenDone=true-speed=1.75}]",
                "particle{REDSTONE} [x=-{x},z={z},from=head,colorScheme={name=redtogreen-revertWhenDone=true-speed=1.75}]"
        ));
        effect.getVariables().add(new Pair<>("x", 0D));
        effect.getVariables().add(new Pair<>("z", 0D));
        effect.getVariables().add(new Pair<>("r", 0D));
        for (int i = 0; i < 40; i++) {
            effect.tick();
            TimeKeeper.increaseTime();
            Thread.sleep(50);
        }
    }
}
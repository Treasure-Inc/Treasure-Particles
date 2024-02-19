package net.treasure.particles.util.math;

import lombok.SneakyThrows;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class MathUtils {

    private static final DecimalFormat DF;

    static public final float PI = 3.1415927f;
    static public final double PI2 = PI * 2;

    private static final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float radFull = PI * 2;
    private static final float degFull = 360;
    private static final float radToIndex = SIN_COUNT / radFull;
    private static final float degToIndex = SIN_COUNT / degFull;

    public static final float degreesToRadians = 0.017453292519943295f;

    static {
        DF = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        DF.setMaximumFractionDigits(340);
    }

    private static class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) {
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            }
            for (int i = 0; i < 360; i += 90) {
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
            }
        }
    }

    public static float sin(double radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    public static float cos(double radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    private static final int ATAN2_BITS = 7; // Adjust for accuracy.
    private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
    private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    private static final int ATAN2_COUNT = ATAN2_MASK + 1;
    private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

    private static class Atan2 {

        static final float[] table = new float[ATAN2_COUNT];

        static {
            for (int i = 0; i < ATAN2_DIM; i++) {
                for (int j = 0; j < ATAN2_DIM; j++) {
                    float x0 = (float) i / ATAN2_DIM;
                    float y0 = (float) j / ATAN2_DIM;
                    table[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
                }
            }
        }
    }

    public static float atan2(double y, double x) {
        float add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else {
                mul = -1;
            }
            x = -x;
            add = -PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else {
                mul = 1;
            }
            add = 0;
        }
        double invDiv = 1 / ((Math.max(x, y)) * INV_ATAN2_DIM_MINUS_1);

        if (invDiv == Float.POSITIVE_INFINITY) return ((float) Math.atan2(y, x) + add) * mul;

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            double save = Double.MIN_VALUE;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat(',')) {
                        save = x;
                        x = parseFactor();
                    } else if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else if (eat('%'))
                        x %= parseFactor();
                    else
                        return x;
                }
            }

            @SneakyThrows
            double parseFactor() {
                if (eat('+'))
                    return parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (ch == ',') {
                    x = 0;
                } else if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'E') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'E') nextChar();
                    x = DF.parse(str.substring(startPos, this.pos)).doubleValue();
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "sin" -> sin(x);
                        case "cos" -> cos(x);
                        case "tan" -> Math.tan(x);
                        case "cot" -> 1 / Math.tan(x);
                        case "sec" -> 1 / cos(x);
                        case "cosec" -> 1 / sin(x);
                        case "asin" -> Math.asin(x);
                        case "acos" -> Math.acos(x);
                        case "abs" -> Math.abs(x);
                        case "atan" -> atan2(save, x);
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public static int generateRandomInteger(int minimum, int maximum) {
        return minimum + (int) (new Random().nextDouble() * ((maximum - minimum) + 1));
    }

    public static Vector getRandomVector() {
        var random = new Random();

        var u = random.nextDouble();
        var v = random.nextDouble();

        var theta = u * PI2;
        var phi = Math.acos(2 * v - 1);

        var sinTheta = sin(theta);
        var cosTheta = cos(theta);
        var sinPhi = sin(phi);
        var cosPhi = cos(phi);

        var x = sinPhi * cosTheta;
        var y = sinPhi * sinTheta;
        var z = cosPhi;

        // Going to take it on faith from the math gods that
        // this is always a normal vector
        return new Vector(x, y, z);
    }
}
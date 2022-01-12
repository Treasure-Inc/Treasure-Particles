package net.treasure.util;

public class MathUtil {

    private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
    private static final double radFull, radToIndex;
    private static final double degFull, degToIndex;
    private static final double[] sin, cos;

    static {
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;

        radFull = Math.PI * 2.0;
        degFull = 360.0;
        radToIndex = SIN_COUNT / radFull;
        degToIndex = SIN_COUNT / degFull;

        sin = new double[SIN_COUNT];
        cos = new double[SIN_COUNT];

        for (int i = 0; i < SIN_COUNT; i++) {
            sin[i] = Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            cos[i] = Math.cos((i + 0.5f) / SIN_COUNT * radFull);
        }

        for (int i = 0; i < 360; i += 90) {
            sin[(int) (i * degToIndex) & SIN_MASK] = Math.sin(i * Math.PI / 180.0);
            cos[(int) (i * degToIndex) & SIN_MASK] = Math.cos(i * Math.PI / 180.0);
        }
    }

    public static double sin(double rad) {
        return sin[(int) (rad * radToIndex) & SIN_MASK];
    }

    public static double cos(double rad) {
        return cos[(int) (rad * radToIndex) & SIN_MASK];
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            double save = Double.MIN_VALUE;

            void nextChar(String by) {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
//                System.out.println("> next char (" + by + "," + pos + ")");
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar("eat empty");
                if (ch == charToEat) {
                    nextChar("eat");
                    return true;
                }
                return false;
            }

            double parse() {
//                System.out.println("started parsing (" + str.length() + ")");
                nextChar("start");
                double x = parseExpression();
//                System.out.println("parse: " + x);
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
                    if (eat('+')) {
//                        System.out.println("parsing expression (+)");
                        x += parseTerm(); // addition
                    } else if (eat('-')) {
//                        System.out.println("parsing expression (-)");
                        x -= parseTerm(); // subtraction
                    } else {
//                        System.out.println("parsing expression (" + x + ")");
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat(',')) {
                        save = x;
//                        System.out.println("parsing term (save=" + save + ")");
                        x = parseFactor();
                    } else if (eat('*')) {
//                        System.out.println("parsing term (*)");
                        x *= parseFactor(); // multiplication
                    } else if (eat('/')) {
//                        System.out.println("parsing term (/)");
                        x /= parseFactor(); // division
                    } else if (eat('%')) {
//                        System.out.println("parsing term (%)");
                        x %= parseFactor();
                    } else {
//                        System.out.println("parsing term (" + x + ")");
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
//                    System.out.println("unary plus");
                    return parseFactor(); // unary plus
                }
                if (eat('-')) {
//                    System.out.println("unary minus");
                    return -parseFactor(); // unary minus
                }
//                System.out.println("char: " + Character.getName(ch));

                double x;
                int startPos = this.pos;
                if (ch == ',') {
                    x = 0;
//                    System.out.println("yes");
                } else if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar("factor number");
                    x = Double.parseDouble(str.substring(startPos, this.pos));
//                    System.out.println("found number: " + x);
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar("factor functions");
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "sin" -> MathUtil.sin(x);
                        case "cos" -> MathUtil.cos(x);
                        case "tan" -> Math.tan(x);
                        case "cot" -> 1 / Math.tan(x);
                        case "atan" -> {
//                            System.out.println("atan (" + x + "," + save + ")");
                            yield Math.atan2(save, x);
                        }
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                } else {
                    throw new RuntimeException("Unexpected buro: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
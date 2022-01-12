package net.treasure.common;

import java.util.regex.Pattern;

public class Patterns {

    public static final Pattern VARIABLE = Pattern.compile("(?<name>[a-zA-Z0-9]+)(?:=)(?<default>[0-9.]+)");
    public static final Pattern EVAL = Pattern.compile("^([a-zA-Z0-9]+)(\\X?)=(.+)$");
    public static final Pattern SCRIPT = Pattern.compile("(?:particle \\[|sound \\[|(?<=\\,))(?<type>\\w+)(?:=)(?<value>[a-zA-Z0-9{}=*.;_  -]+)(?:(?=\\,)|\\])");
    public static final Pattern OFFSET = Pattern.compile("(\\{|(?<=;))(?<type>x|y|z)(?:=)(?<value>[a-zA-Z0-9{}.]+)(?:(?=;)|})");
    public static final Pattern COLOR = Pattern.compile("(|(?<=;))(?<type>name|revertWhenDone|speed)(?:=)(?<value>[a-zA-Z0-9{}.]+)(?:(?=;)|)");

}

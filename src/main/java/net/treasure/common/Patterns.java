package net.treasure.common;

import java.util.regex.Pattern;

public final class Patterns {

    // GENERAL
    public static final Pattern SPACE = Pattern.compile(" ");
    public static final Pattern TILDE = Pattern.compile("~");

    // EFFECTS
    public static final Pattern VARIABLE = Pattern.compile("(?<name>[a-zA-Z\\d]+)(?:=)(?<default>[\\d.-]+)");
    public static final Pattern EVAL = Pattern.compile("^([a-zA-Z\\d]+)(\\X?)=(.+)$");
    public static final Pattern SCRIPT = Pattern.compile("(?:\\[|(?<=\\,))(?<type>\\w+)(?:=)(?<value>[a-zA-Z0-9{}=*.;_-]+)(?:(?=\\,)|\\])");
    public static final Pattern INNER_SCRIPT = Pattern.compile("(?:\\{|(?<=;))(?<type>\\w+)(?:=)(?<value>[a-zA-Z\\d{}_.-]+)(?:(?=;)|})");

    // CONDITIONS
    public static final Pattern CONDITIONAL = Pattern.compile("(?<condition>.+) (?:\\?) (?<first>.+) (?:\\:) (?<second>.+)");
}
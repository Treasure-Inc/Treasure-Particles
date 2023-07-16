package net.treasure.constants;

import java.util.regex.Pattern;

public final class Patterns {

    // GENERAL
    public static final Pattern SPACE = Pattern.compile(" ");
    public static final Pattern TILDE = Pattern.compile("~");
    public static final Pattern COLON = Pattern.compile(":");
    public static final Pattern DOUBLE = Pattern.compile("\\.\\.");
    public static final Pattern ASTERISK = Pattern.compile("\\*");

    // SCRIPTS
    public static final Pattern SCRIPT = Pattern.compile("(?!^\\[)(?<type>[^=,]+)=(?<value>\".+?\"|[^\0]+?)(?=,[^,]+=|]$)");
    public static final Pattern INNER_SCRIPT = Pattern.compile("(?!^\\{)(?<type>[^=;]+)=(?<value>[^\0]+?)(?=;[^;]+=|}$)");
    // VARIABLE SCRIPT
    public static final Pattern VARIABLE = Pattern.compile("(?<name>[a-zA-Z\\d]+)(=)(?<default>[\\d.-]+)");
    public static final Pattern EVAL = Pattern.compile("^([a-zA-Z\\d]+)(\\X?)=(.+)$");

    // CONDITIONS
    public static final Pattern CONDITIONAL = Pattern.compile("(?<condition>.+) (\\?) (?<first>.+) (:) (?<second>.+)");
}
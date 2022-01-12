package net.treasure.util;

import lombok.Getter;

public class TimeKeeper {

    @Getter
    private static long timeElapsed;

    public static boolean isElapsed(int time) {
        return timeElapsed % time == 0;
    }

    public static void increaseTime() {
        timeElapsed++;
    }

    public static void reset() {
        timeElapsed = 0;
    }
}

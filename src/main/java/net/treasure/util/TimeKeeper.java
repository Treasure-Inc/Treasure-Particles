package net.treasure.util;

import lombok.Getter;

public class TimeKeeper {

    @Getter
    private static long timeElapsed;

    public static boolean isElapsed(int seconds) {
        return timeElapsed % seconds == 0;
    }

    public static void increaseTime() {
        try {
            timeElapsed++;
        } catch (Exception e) {
            timeElapsed = 0;
        }
    }

    public static void reset() {
        timeElapsed = 0;
    }
}
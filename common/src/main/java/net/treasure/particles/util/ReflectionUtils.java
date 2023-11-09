package net.treasure.particles.util;

import org.bukkit.Bukkit;

public class ReflectionUtils {

    public static final double MINECRAFT_VERSION;

    static {
        String bukkitVersion = Bukkit.getBukkitVersion();
        int dashIndex = bukkitVersion.indexOf("-");
        MINECRAFT_VERSION = Double.parseDouble(bukkitVersion.substring(2, dashIndex > -1 ? dashIndex : bukkitVersion.length()));
    }
}
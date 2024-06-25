package net.treasure.particles.util;

import org.bukkit.Bukkit;

public class ReflectionUtils {

    public static final double LATEST_SUPPORTED_VERSION = 21;

    public static final double MINECRAFT_VERSION;
    public static final String MINECRAFT_VERSION_CONVERTED;

    static {
        var bukkitVersion = Bukkit.getBukkitVersion();
        var dashIndex = bukkitVersion.indexOf("-");
        MINECRAFT_VERSION_CONVERTED = bukkitVersion.substring(2, dashIndex > -1 ? dashIndex : bukkitVersion.length());
        MINECRAFT_VERSION = Double.parseDouble(MINECRAFT_VERSION_CONVERTED);
    }
}
package net.treasure.particles.util.nms;

import net.treasure.particles.util.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NMSMatcher {

    private static final Map<String, String> VERSION_MAP;

    static {
        VERSION_MAP = new HashMap<>();
        put("v1_16_R1", "16.1");
        put("v1_16_R2", "16.2", "16.3");
        put("v1_16_R3", "16.4", "16.5");
        put("v1_17_R1", "17", "17.1");
        put("v1_18_R1", "18", "18.1");
        put("v1_18_R2", "18.2");
        put("v1_19_R1", "19", "19.1", "19.2");
        put("v1_19_R2", "19.3");
        put("v1_19_R3", "19.4");
        put("v1_20_R1", "20", "20.1");
        put("v1_20_R2", "20.2", "20.3");
        put("v1_20_R3", "20.4");
        put("v1_20_R4", "20.5", "20.6");
        put("v1_21", "21");
    }

    private static void put(String plugin, String... versions) {
        for (var version : versions)
            VERSION_MAP.put(version, plugin);
    }

    public static AbstractNMSHandler match() {
        var serverVersion = VERSION_MAP.get(ReflectionUtils.MINECRAFT_VERSION_CONVERTED);
        try {
            var builderClass = Class.forName("net.treasure.particles.version." + serverVersion + ".NMSHandler");
            var constructor = builderClass.getConstructor();
            return (AbstractNMSHandler) constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException exception) {
            throw new IllegalStateException("Failed to instantiate NMS Handler for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            Bukkit.getLogger().log(Level.WARNING, "Bukkit version: " + Bukkit.getBukkitVersion() + " (Found: " + ReflectionUtils.MINECRAFT_VERSION_CONVERTED + ")");
            throw new IllegalStateException("TreasureParticles does not support server version " + serverVersion, exception);
        }
    }
}
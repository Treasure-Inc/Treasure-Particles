package net.treasure.particles.util.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NMSMatcher {

    private static final Map<String, String> VERSION_MAP;

    static {
        VERSION_MAP = new HashMap<>();
        registerVersion("v1_16_R1", "16.1");
        registerVersion("v1_16_R2", "16.2", "16.3");
        registerVersion("v1_16_R3", "16.4", "16.5");
        registerVersion("v1_17_R1", "17", "17.1");
        registerVersion("v1_18_R1", "18", "18.1");
        registerVersion("v1_18_R2", "18.2");
        registerVersion("v1_19_R1", "19", "19.1", "19.2");
        registerVersion("v1_19_R2", "19.3");
        registerVersion("v1_19_R3", "19.4");
        registerVersion("v1_20_R1", "20", "20.1");
        registerVersion("v1_20_R2", "20.2", "20.3");
        registerVersion("v1_20_R3", "20.4");
        registerVersion("v1_20_R4", "20.5", "20.6");
        registerVersion("v1_21_R1", "21", "21.1");
        registerVersion("v1_21_R2", "21.2", "21.3");
        registerVersion("v1_21_R3", "21.4");
        registerVersion("v1_21_R4", "21.5");
        registerVersion("v1_21_R5", "21.6", "21.7");
    }

    private static void registerVersion(String plugin, String... versions) {
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
            Bukkit.getLogger().log(Level.WARNING, "Bukkit version: " + Bukkit.getBukkitVersion() + " (Found: " + ReflectionUtils.MINECRAFT_VERSION_CONVERTED + ")");
            Bukkit.getLogger().warning("Failed to instantiate NMS Handler for version " + ReflectionUtils.MINECRAFT_VERSION_CONVERTED);
        } catch (ClassNotFoundException exception) {
            Bukkit.getLogger().log(Level.WARNING, "Bukkit version: " + Bukkit.getBukkitVersion() + " (Found: " + ReflectionUtils.MINECRAFT_VERSION_CONVERTED + ")");
            Bukkit.getLogger().warning("TreasureParticles does not support server version " + ReflectionUtils.MINECRAFT_VERSION_CONVERTED);
        }
        return null;
    }
}
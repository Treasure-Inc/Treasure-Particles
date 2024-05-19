package net.treasure.particles.util.nms;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.util.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class NMSMatcher {

    public AbstractNMSHandler match() {
        var serverVersion = TreasureParticles.isPaper() && ReflectionUtils.MINECRAFT_VERSION >= 20.5 ? "1_20_R4" : Bukkit.getServer()
                .getClass()
                .getPackage()
                .getName()
                .split("\\.")[3]
                .substring(1);

        try {
            var builderClass = Class.forName("net.treasure.particles.version.v" + serverVersion + ".NMSHandler");
            var constructor = builderClass.getConstructor();
            return (AbstractNMSHandler) constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException exception) {
            throw new IllegalStateException("Failed to instantiate NMS Handler for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("TreasureParticles does not support server version " + serverVersion, exception);
        }
    }
}
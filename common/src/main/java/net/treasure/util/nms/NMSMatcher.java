package net.treasure.util.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class NMSMatcher {

    public AbstractNMSHandler match() {
        final String serverVersion = Bukkit.getServer()
                .getClass()
                .getPackage()
                .getName()
                .split("\\.")[3]
                .substring(1);
        try {
            var builderClass = Class.forName("net.treasure.version.v" + serverVersion + ".NMSHandler");
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
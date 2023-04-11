package net.treasure.util.particles;

import net.treasure.common.NMSHandler;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class NMSMatcher {

    public NMSHandler match() {
        final String serverVersion = Bukkit.getServer()
                .getClass()
                .getPackage()
                .getName()
                .split("\\.")[3]
                .substring(1);
        try {
            var builderClass = Class.forName("net.treasure.v" + serverVersion + ".NMSHandler");
            var constructor = builderClass.getConstructor();
            return (NMSHandler) constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException exception) {
            throw new IllegalStateException("Failed to instantiate NMS Handler for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("TreasureElytra+ does not support server version " + serverVersion, exception);
        }
    }
}
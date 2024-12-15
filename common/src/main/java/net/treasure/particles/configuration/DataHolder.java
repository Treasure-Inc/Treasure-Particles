package net.treasure.particles.configuration;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.util.logging.ComponentLogger;
import org.bukkit.configuration.file.YamlConfiguration;

public interface DataHolder {

    String getVersion();

    ConfigurationGenerator getGenerator();

    boolean initialize();

    void reload();

    /**
     * Checks the version of data holder
     *
     * @return true if the data holder is up-to-date
     */
    default boolean checkVersion() {
        return getVersion().equals(getGenerator().getConfiguration().getString("version"));
    }

    default YamlConfiguration getConfiguration() {
        var generator = getGenerator();
        var config = generator.generate();
        if (config == null) return null;

        if (!checkVersion()) {
            if (!TreasureParticles.isAutoUpdateEnabled()) {
                logNewVersionInfo();
            } else {
                generator.reset();
                config = generator.generate();
                logGeneratedNewFile();
            }
        }

        return config;
    }

    default void logNewVersionInfo() {
        ComponentLogger.error(getGenerator(), "New version available (v" + getVersion() + ")");
    }

    default void logGeneratedNewFile() {
        ComponentLogger.error(getGenerator(), "Generated new file (v" + getVersion() + ")");
    }
}
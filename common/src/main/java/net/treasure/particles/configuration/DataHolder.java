package net.treasure.particles.configuration;

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
}
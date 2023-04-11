package net.treasure.core.configuration;

public interface DataHolder {

    boolean initialize();

    void reload();

    /**
     * Checks the version of data holder
     *
     * @return true if the data holder is up-to-date
     */
    boolean checkVersion();
}
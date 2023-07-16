package net.treasure;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractTreasurePlugin extends JavaPlugin {

    @Override
    public void onDisable() {
        TreasureParticles.getDatabaseManager().close();
    }

    public void disable() {
        getLogger().warning("Couldn't initialize TreasureParticles");
        getPluginLoader().disablePlugin(this);
    }
}
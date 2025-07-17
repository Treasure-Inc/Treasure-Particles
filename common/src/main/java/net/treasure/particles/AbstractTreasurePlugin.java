package net.treasure.particles;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractTreasurePlugin extends JavaPlugin {

    @Override
    public void onDisable() {
        if (TreasureParticles.getDatabaseManager() != null)
            TreasureParticles.getDatabaseManager().close();
    }

    public void disable(String message) {
        getLogger().warning(message);
        getPluginLoader().disablePlugin(this);
    }
}
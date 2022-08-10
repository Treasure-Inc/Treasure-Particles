package net.treasure.util;

import lombok.AllArgsConstructor;
import net.treasure.core.TreasurePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

@AllArgsConstructor
public class UpdateChecker implements Listener {

    private static final String RESOURCE_ID = "99860";

    private final TreasurePlugin plugin;

    public void check() {
        getVersion(version -> {
            if (!plugin.getVersion().equals(version)) {
                plugin.getLogger().warning("New version of TreasureElytra available!");
                plugin.getLogger().warning(" • Your version: " + plugin.getVersion());
                plugin.getLogger().warning(" • Latest version: " + version);
                plugin.getLogger().warning("");
                plugin.getLogger().warning("Please update to the newest version.");
                plugin.getLogger().warning("");
                plugin.getLogger().warning("Spigot Page: https://www.spigotmc.org/resources/" + RESOURCE_ID);
            }
        });
    }

    private void getVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (var inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
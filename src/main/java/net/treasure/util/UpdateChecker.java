package net.treasure.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.treasure.core.TreasurePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class UpdateChecker implements Listener {

    private static final String RESOURCE_ID = "99860";

    private final TreasurePlugin plugin;
    @Getter
    private boolean updateAvailable;

    public void check() {
        getVersion(version -> {
            if (!plugin.getVersion().equals(version)) {
                updateAvailable = true;

                plugin.getLogger().warning("New version of TreasureElytra available!");
                plugin.getLogger().warning(" • Your version: " + plugin.getVersion());
                plugin.getLogger().severe(" • Latest version: " + version);
                plugin.getLogger().warning("");
                plugin.getLogger().warning("Please update to the newest version.");
                plugin.getLogger().warning("");
                plugin.getLogger().warning("Spigot Page: https://www.spigotmc.org/resources/" + RESOURCE_ID);
            }
        });
    }

    private void getVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                var connection = new URL("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=" + RESOURCE_ID).openConnection();
                try (final var input = new InputStreamReader(connection.getInputStream()); final var reader = new BufferedReader(input)) {
                    var version = new Gson().fromJson(reader, JsonObject.class).get("current_version").getAsString();
                    consumer.accept(version);
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
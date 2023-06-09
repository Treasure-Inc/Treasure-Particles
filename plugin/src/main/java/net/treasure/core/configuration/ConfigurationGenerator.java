package net.treasure.core.configuration;

import com.google.common.io.Files;
import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class ConfigurationGenerator {

    final String fileName;
    final String directory;
    @Getter
    YamlConfiguration configuration;
    Plugin plugin;

    public ConfigurationGenerator(String fileName) {
        this.fileName = fileName;
        this.directory = fileName;
    }

    public ConfigurationGenerator(String fileName, String directory) {
        this.fileName = fileName;
        this.directory = directory + "/" + fileName;
    }

    public YamlConfiguration generate() {
        return generate(TreasurePlugin.getInstance());
    }

    public YamlConfiguration generate(Plugin plugin) {
        this.plugin = plugin;
        var file = new File(plugin.getDataFolder(), directory);
        try {
            boolean exists = file.exists();
            if (!exists)
                saveResource(false);
            if (!file.exists()) {
                this.configuration = null;
                return null;
            }
            return configuration = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reset() {
        var dataFolder = plugin.getDataFolder();
        var file = new File(dataFolder, directory);
        try {
            Files.copy(file, new File(dataFolder, "old_" + fileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".yml"));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't copy old file: " + fileName + ".yml", e);
        }

        saveResource(true);
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    private void saveResource(boolean replace) {
        var in = plugin.getResource(fileName);
        if (in == null) {
            plugin.getLogger().warning("The embedded resource '" + fileName + "' cannot be found");
            return;
        }

        var outFile = new File(plugin.getDataFolder(), directory);
        var lastIndex = directory.lastIndexOf('/');
        var outDir = new File(plugin.getDataFolder(), directory.substring(0, Math.max(lastIndex, 0)));

        boolean exists = outDir.exists();
        if (!exists)
            exists = outDir.mkdirs();
        if (!exists) {
            plugin.getLogger().warning("Couldn't create a directory: " + directory);
            return;
        }

        try {
            if (outFile.exists() && !replace)
                return;

            var out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save resource " + fileName, e);
        }
    }
}
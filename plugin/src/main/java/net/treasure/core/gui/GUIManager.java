package net.treasure.core.gui;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.ConfigurationGenerator;
import net.treasure.core.configuration.DataHolder;
import net.treasure.core.gui.config.GUIElements;
import net.treasure.core.gui.config.GUISounds;
import net.treasure.core.gui.config.GUIStyle;
import net.treasure.core.gui.task.GUITask;
import net.treasure.core.gui.type.GUI;
import net.treasure.core.gui.type.admin.AdminGUI;
import net.treasure.core.gui.type.color.ColorsGUI;
import net.treasure.core.gui.type.effects.EffectsGUI;
import net.treasure.locale.Translations;
import net.treasure.util.tuples.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class GUIManager implements DataHolder {

    public static final String VERSION = "1.0.0";

    final ConfigurationGenerator generator;
    YamlConfiguration config;

    final GUIElements elements = new GUIElements();
    final GUISounds sounds = new GUISounds();
    GUIStyle style;

    int taskId = -5, interval = 2;
    float colorCycleSpeed = 0.85f;

    public GUIManager() {
        this.generator = new ConfigurationGenerator("gui.yml");

        EffectsGUI.configure(this);
        ColorsGUI.configure(this);
        AdminGUI.configure(this);
    }

    @Override
    public boolean checkVersion() {
        return VERSION.equals(config.getString("version"));
    }

    @Override
    public boolean initialize() {
        var inst = TreasurePlugin.getInstance();

        this.config = generator.generate();
        if (!checkVersion()) {
            if (!inst.isAutoUpdateEnabled()) {
                inst.getLogger().warning("New version of gui.yml available (v" + VERSION + ")");
            } else {
                generator.reset();
                config = generator.getConfiguration();
            }
        }

        interval = config.getInt("animation.interval", interval);
        colorCycleSpeed = (float) config.getDouble("animation.color-cycle-speed", getColorCycleSpeed());
        if (taskId != -5 && !config.getBoolean("animation.enabled", true)) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -5;
        } else if (taskId == -5 && config.getBoolean("animation.enabled", true)) {
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(inst, new GUITask(), 0, interval).getTaskId();
        }

        style = getCurrentStyle();
        if (style == null) {
            inst.getLogger().warning("Couldn't set gui style");
            return false;
        }

        elements.initialize(this);
        sounds.initialize(this);

        EffectsGUI.setItems();
        ColorsGUI.setItems();
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    public GUIStyle getCurrentStyle() {
        var id = config.getString("current-style");
        if (id == null) return null;
        var title = config.getString("styles." + id + ".title", Translations.GUI_TITLE);
        return new GUIStyle(
                id,
                title,
                config.getInt("styles." + id + ".size", 54),
                Stream.of(GUI.values()).map(gui -> new Pair<>(gui, config.getStringList("styles." + id + "." + gui.id() + ".layout").toArray(String[]::new))).collect(Collectors.toMap(Pair::getKey, Pair::getValue))
        );
    }
}

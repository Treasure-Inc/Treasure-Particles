package net.treasure.particles;

import lombok.Getter;
import net.treasure.particles.color.ColorManager;
import net.treasure.particles.command.MainCommand;
import net.treasure.particles.database.DatabaseManager;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.EffectManager;
import net.treasure.particles.integration.Expansions;
import net.treasure.particles.locale.Translations;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.Map;

@Getter
public class TreasurePlugin extends AbstractTreasurePlugin {

    @Override
    public void onEnable() {
        TreasureParticles.setPlugin(this);

        // Commands & Listeners
        initializeCommands();

        // bStats
        initializeMetrics();

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new Expansions().register();
    }

    private void initializeCommands() {
        // Main command with completions
        var commandManager = TreasureParticles.getCommandManager();
        commandManager.registerCommand(new MainCommand(this));
        var completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("effects", context -> TreasureParticles.getEffectManager().getEffects().stream().map(Effect::getKey).toList());
        completions.registerAsyncCompletion("groupColors", context -> {
            var key = context.getContextValue(String.class, 1);
            var effect = TreasureParticles.getEffectManager().get(key);
            return effect == null || effect.getColorGroup() == null ? Collections.emptyList() : effect.getColorGroup().getAvailableOptions().stream().map(option -> option.colorScheme().getKey()).toList();
        });
        commandManager.usePerIssuerLocale(false, false);
    }

    private void initializeMetrics() {
        var metrics = new Metrics(this, 18854);
        metrics.addCustomChart(new SimplePie("locale", () -> Translations.LOCALE));
        metrics.addCustomChart(new SimplePie("database_type", () -> DatabaseManager.TYPE));

        metrics.addCustomChart(new DrilldownPie("effects_size", () -> Map.of(String.valueOf(TreasureParticles.getEffectManager().getEffects().size()), Map.of(EffectManager.VERSION, 1))));
        metrics.addCustomChart(new DrilldownPie("color_schemes_size", () -> Map.of(String.valueOf(TreasureParticles.getColorManager().getColors().size()), Map.of(ColorManager.VERSION, 1))));
        metrics.addCustomChart(new DrilldownPie("color_groups_size", () -> Map.of(String.valueOf(TreasureParticles.getColorManager().getGroups().size()), Map.of(ColorManager.VERSION, 1))));

        metrics.addCustomChart(new SimplePie("notifications_enabled", () -> String.valueOf(TreasureParticles.isNotificationsEnabled())));
        metrics.addCustomChart(new SimplePie("auto_update_enabled", () -> String.valueOf(TreasureParticles.isAutoUpdateEnabled())));

        metrics.addCustomChart(new SimplePie("gui_current_style", () -> TreasureParticles.getGUIManager().getCurrentStyle().getId()));
        metrics.addCustomChart(new SimplePie("gui_animation_enabled", () -> String.valueOf(TreasureParticles.getGUIManager().getTaskId() != -5)));
        metrics.addCustomChart(new SimplePie("gui_animation_interval", () -> String.valueOf(TreasureParticles.getGUIManager().getInterval())));
        metrics.addCustomChart(new SimplePie("gui_animation_speed", () -> String.valueOf(TreasureParticles.getGUIManager().getColorCycleSpeed())));
    }
}
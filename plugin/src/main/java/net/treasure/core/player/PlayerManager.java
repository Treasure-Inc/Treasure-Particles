package net.treasure.core.player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.treasure.color.scheme.ColorScheme;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.database.DatabaseManager;
import net.treasure.effect.EffectManager;
import net.treasure.effect.data.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerManager {

    @Getter
    private final ConcurrentHashMap<UUID, EffectData> data;
    private final Gson gson;

    public PlayerManager() {
        data = new ConcurrentHashMap<>();
        gson = new Gson();
    }

    public void initializePlayer(Player player) {
        this.initializePlayer(player, null);
    }

    public void initializePlayer(Player player, Consumer<EffectData> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(TreasurePlugin.getInstance(), () -> {
            var inst = TreasurePlugin.getInstance();
            var effectManager = inst.getEffectManager();
            var colorManager = inst.getColorManager();
            var database = inst.getDatabase();

            var data = new EffectData(player);
            this.data.put(player.getUniqueId(), data);

            PlayerData playerData = inst.getDatabase().get("SELECT `data` FROM `" + DatabaseManager.TABLE + "` WHERE `uuid`=?", rs -> {
                try {
                    if (rs.next())
                        return gson.fromJson(rs.getString("data"), PlayerData.class);
                } catch (JsonSyntaxException e) {
                    database.update("DELETE FROM `" + DatabaseManager.TABLE + "` WHERE `uuid`=?", player.getUniqueId().toString());
                }
                return new PlayerData();
            }, player.getUniqueId().toString());

            if (playerData == null) return;
            var effect = effectManager.get(playerData.effectName);
            if (effect != null && (!EffectManager.ALWAYS_CHECK_PERMISSION || effect.canUse(player)))
                data.setCurrentEffect(effect);

            for (var entry : playerData.colorPreferences.entrySet()) {
                var effectName = entry.getKey();
                if (!effectManager.has(effectName)) continue;
                var colorScheme = colorManager.getColorScheme(entry.getValue());
                if (colorScheme == null) continue;
                data.getColorPreferences().put(effectName, colorScheme);
            }

            data.setNotificationsEnabled(playerData.notificationsEnabled);
            data.setEffectsEnabled(playerData.effectsEnabled);

            if (callback != null)
                callback.accept(data);
        });
    }

    public EffectData getEffectData(Player player) {
        return getEffectData(player.getUniqueId());
    }

    public EffectData getEffectData(UUID uuid) {
        return data.get(uuid);
    }

    public void remove(Player player) {
        var data = this.data.remove(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(TreasurePlugin.getInstance(), () -> save(player, data));
    }

    public void save(Player player, EffectData data) {
        if (data == null) return;
        Map<String, String> colorPreferences = data.getColorPreferences().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getKey()
                ));
        var playerData = new PlayerData(data.getCurrentEffect() != null ? data.getCurrentEffect().getKey() : null, colorPreferences, data.isEffectsEnabled(), data.isNotificationsEnabled());
        TreasurePlugin.getInstance().getDatabase().update("REPLACE INTO `" + DatabaseManager.TABLE + "` (`uuid`, `data`) VALUES (?, ?)", player.getUniqueId().toString(), gson.toJson(playerData));
    }

    public void reload() {
        var inst = TreasurePlugin.getInstance();
        var effectManager = inst.getEffectManager();
        var colorManager = inst.getColorManager();

        for (var iterator = data.entrySet().iterator(); iterator.hasNext(); ) {
            var set = iterator.next();
            var data = set.getValue();
            var player = Bukkit.getPlayer(set.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }

            Map<String, ColorScheme> colorPreferences = new HashMap<>();
            for (var entry : data.getColorPreferences().entrySet()) {
                var effectName = entry.getKey();
                if (!effectManager.has(effectName)) continue;
                if (entry.getValue() == null) continue;
                var colorScheme = colorManager.getColorScheme(entry.getValue().getKey());
                if (colorScheme == null) continue;
                colorPreferences.put(effectName, colorScheme);
            }
            data.setColorPreferences(colorPreferences);

            if (data.getCurrentEffect() == null) continue;

            var effect = effectManager.get(data.getCurrentEffect().getKey());
            data.setCurrentEffect(null);
            if (effect != null && (!EffectManager.ALWAYS_CHECK_PERMISSION || effect.canUse(player)))
                data.setCurrentEffect(effect);
        }
    }
}
package net.treasure.player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.treasure.TreasureParticles;
import net.treasure.color.scheme.ColorScheme;
import net.treasure.database.DatabaseManager;
import net.treasure.effect.data.EffectData;
import net.treasure.permission.Permissions;
import net.treasure.player.listener.JoinQuitListener;
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
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), TreasureParticles.getPlugin());
    }

    public void initializePlayer(Player player) {
        this.initializePlayer(player, null);
    }

    public void initializePlayer(Player player, Consumer<EffectData> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(TreasureParticles.getPlugin(), () -> {
            var effectManager = TreasureParticles.getEffectManager();
            var colorManager = TreasureParticles.getColorManager();
            var database = TreasureParticles.getDatabase();

            var data = new EffectData(player);
            this.data.put(player.getUniqueId(), data);

            PlayerData playerData = database.get("SELECT `data` FROM `" + DatabaseManager.TABLE + "` WHERE `uuid`=?", rs -> {
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
            if (effect != null && (!Permissions.ALWAYS_CHECK_PERMISSION || effect.canUse(player)))
                data.setCurrentEffect(effect);

            for (var entry : playerData.colorPreferences.entrySet()) {
                var effectName = entry.getKey();
                if (!effectManager.has(effectName)) continue;
                var colorScheme = colorManager.getColorScheme(entry.getValue());
                if (colorScheme == null) continue;
                data.getColorPreferences().put(effectName, colorScheme);
            }

            data.setMixData(playerData.mixData);
            if (Permissions.MIX_LIMIT_ENABLED) {
                var mixLimit = data.getMixLimit();
                if (data.getMixData().size() > mixLimit) {
                    data.setMixData(data.getMixData().subList(0, mixLimit));
                    data.setCurrentEffect(null);
                }
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
        Bukkit.getScheduler().runTaskAsynchronously(TreasureParticles.getPlugin(), () -> save(player, data));
    }

    public void save(Player player, EffectData data) {
        if (data == null) return;
        Map<String, String> colorPreferences = data.getColorPreferences().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getKey()
                ));
        var playerData = new PlayerData(data.getCurrentEffect() != null ? data.getCurrentEffect().getKey() : null, colorPreferences, data.getMixData(), data.isEffectsEnabled(), data.isNotificationsEnabled());
        TreasureParticles.getDatabase().update("REPLACE INTO `" + DatabaseManager.TABLE + "` (`uuid`, `data`) VALUES (?, ?)", player.getUniqueId().toString(), gson.toJson(playerData));
    }

    public void reload() {
        var effectManager = TreasureParticles.getEffectManager();
        var colorManager = TreasureParticles.getColorManager();

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
            if (effect != null && (!Permissions.ALWAYS_CHECK_PERMISSION || effect.canUse(player)))
                data.setCurrentEffect(effect);

            if (Permissions.MIX_LIMIT_ENABLED) {
                var mixLimit = data.getMixLimit();
                if (data.getMixData().size() > mixLimit) {
                    data.setMixData(data.getMixData().subList(0, mixLimit));
                    data.setCurrentEffect(null);
                }
            }

            data.resetMixDataCache();
        }
    }
}
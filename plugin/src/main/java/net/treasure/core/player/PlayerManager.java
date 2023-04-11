package net.treasure.core.player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.treasure.color.ColorScheme;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.EffectManager;
import net.treasure.effect.data.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            PlayerData playerData = null;

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                var connection = database.getConnection();
                ps = connection.prepareStatement("SELECT data FROM data WHERE uuid=?");
                ps.setString(1, player.getUniqueId().toString());
                rs = ps.executeQuery();
                if (rs.next())
                    playerData = gson.fromJson(rs.getString("data"), PlayerData.class);
                else
                    playerData = new PlayerData();
            } catch (SQLException exception) {
                exception.printStackTrace();
            } catch (JsonSyntaxException exception) {
                database.update("DELETE FROM data WHERE uuid=?", player.getUniqueId().toString());
            } finally {
                database.close(ps, rs);
            }

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
        TreasurePlugin.getInstance().getDatabase().update("REPLACE INTO data (uuid, data) VALUES (?, ?)", player.getUniqueId().toString(), gson.toJson(playerData));
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

            if (data.getCurrentEffect() == null) {
                data.setEnabled(false);
                continue;
            }
            var effect = effectManager.get(data.getCurrentEffect().getKey());
            data.setCurrentEffect(null);
            if (effect != null && (!EffectManager.ALWAYS_CHECK_PERMISSION || effect.canUse(player)))
                data.setCurrentEffect(effect);
            else
                data.setEnabled(false);
            if (!inst.isDebugModeEnabled())
                data.setDebugModeEnabled(false);
        }
    }
}
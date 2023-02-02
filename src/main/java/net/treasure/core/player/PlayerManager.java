package net.treasure.core.player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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
            var database = inst.getDatabase();

            EffectData data = new EffectData();
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
            var effect = inst.getEffectManager().get(playerData.effectName);
            if (effect != null && effect.canUse(player))
                data.setCurrentEffect(player, effect);
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
        var playerData = new PlayerData(data.getCurrentEffect() != null ? data.getCurrentEffect().getKey() : null, data.isEffectsEnabled(), data.isNotificationsEnabled());
        TreasurePlugin.getInstance().getDatabase().update("REPLACE INTO data (uuid, data) VALUES (?, ?)", player.getUniqueId().toString(), gson.toJson(playerData));
    }

    public void reload() {
        var inst = TreasurePlugin.getInstance();
        for (var iterator = data.entrySet().iterator(); iterator.hasNext(); ) {
            var set = iterator.next();
            var data = set.getValue();
            var player = Bukkit.getPlayer(set.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (data.getCurrentEffect() == null) {
                data.setEnabled(false);
                continue;
            }
            var effect = inst.getEffectManager().get(data.getCurrentEffect().getKey());
            data.setCurrentEffect(player, null);
            if (effect != null && effect.canUse(player))
                data.setCurrentEffect(player, effect);
            else
                data.setEnabled(false);
            if (!inst.isDebugModeEnabled())
                data.setDebugModeEnabled(false);
        }
    }
}
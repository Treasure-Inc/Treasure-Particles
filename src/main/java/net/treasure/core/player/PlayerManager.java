package net.treasure.core.player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    @Getter
    private final HashMap<UUID, EffectData> playersData;
    private final Gson gson;

    public PlayerManager() {
        playersData = new HashMap<>();
        gson = new Gson();
    }

    public void initializePlayer(Player player) {
        var inst = TreasurePlugin.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(inst, () -> {
            var database = inst.getDatabase();

            EffectData data = new EffectData();
            playersData.put(player.getUniqueId(), data);

            PlayerData playerData = null;

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                Connection connection = database.getConnection();
                ps = connection.prepareStatement("SELECT data FROM data WHERE uuid=?");
                ps.setString(1, player.getUniqueId().toString());
                rs = ps.executeQuery();
                if (rs.next())
                    playerData = gson.fromJson(rs.getString("data"), PlayerData.class);
            } catch (SQLException exception) {
                exception.printStackTrace();
            } catch (JsonSyntaxException exception) {
                database.update("DELETE FROM data WHERE uuid=?", player.getUniqueId().toString());
            } finally {
                database.close(ps, rs);
            }

            if (playerData == null) return;
            Effect effect = inst.getEffectManager().get(playerData.effectName);
            if (effect != null)
                data.setCurrentEffect(player, effect);
            data.setEffectsEnabled(playerData.effectsEnabled);
        });
    }

    public EffectData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public EffectData getPlayerData(UUID uuid) {
        return playersData.get(uuid);
    }

    public void remove(Player player) {
        var inst = TreasurePlugin.getInstance();
        var data = playersData.remove(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(inst, () -> {
            PlayerData playerData = new PlayerData(data.getCurrentEffect() != null ? data.getCurrentEffect().getKey() : null, data.isEffectsEnabled());
            if (data.getCurrentEffect() != null)
                inst.getDatabase().update("REPLACE INTO data (uuid, data) VALUES (?, ?)", player.getUniqueId().toString(), gson.toJson(playerData));
            else
                inst.getDatabase().update("DELETE FROM data WHERE uuid=?", player.getUniqueId().toString());
        });
    }

    public void reload() {
        var inst = TreasurePlugin.getInstance();
        for (Iterator<Map.Entry<UUID, EffectData>> iterator = playersData.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<UUID, EffectData> set = iterator.next();
            EffectData data = set.getValue();
            Player player = Bukkit.getPlayer(set.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (data.getCurrentEffect() == null) {
                data.setEnabled(false);
                continue;
            }
            Effect effect = TreasurePlugin.getInstance().getEffectManager().get(data.getCurrentEffect().getKey());
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
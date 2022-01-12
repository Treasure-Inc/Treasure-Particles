package net.treasure.core.player;

import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.player.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    @Getter
    private final HashMap<UUID, EffectData> playersData;

    public PlayerManager() {
        playersData = new HashMap<>();
    }

    public void initializePlayer(Player player) {
        EffectData data = new EffectData();
        playersData.put(player.getUniqueId(), data);

        String effectName = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection connection = TreasurePlugin.getInstance().getDatabase().getConnection();
            ps = connection.prepareStatement("SELECT data FROM data WHERE uuid=?");
            ps.setString(1, player.getUniqueId().toString());
            rs = ps.executeQuery();
            if (rs.next())
                effectName = rs.getString("data");
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            TreasurePlugin.getInstance().getDatabase().close(ps, rs);
        }

        if (effectName == null) return;
        Effect effect = TreasurePlugin.getInstance().getEffectManager().get(effectName);
        if (effect != null)
            data.setCurrentEffect(player, effect);
    }

    public EffectData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public EffectData getPlayerData(UUID uuid) {
        return playersData.get(uuid);
    }

    public void remove(Player player) {
        EffectData data = getPlayerData(player);
        if (data.getCurrentEffect() != null)
            TreasurePlugin.getInstance().getDatabase().update("REPLACE INTO data (uuid, data) VALUES (?, ?)", player.getUniqueId().toString(), data.getCurrentEffect().getKey());
        else
            TreasurePlugin.getInstance().getDatabase().update("DELETE FROM data WHERE uuid=?", player.getUniqueId().toString());
        playersData.remove(player.getUniqueId());
    }

    public void reload() {
        for (Map.Entry<UUID, EffectData> set : playersData.entrySet()) {
            EffectData data = set.getValue();
            Player player = Bukkit.getPlayer(set.getKey());
            if (data.getCurrentEffect() == null) {
                data.setEnabled(false);
                continue;
            }
            Effect effect = TreasurePlugin.getInstance().getEffectManager().get(data.getCurrentEffect().getKey());
            data.setCurrentEffect(player, null);
            if (effect != null)
                data.setCurrentEffect(player, effect);
            else
                data.setEnabled(false);
        }
    }
}
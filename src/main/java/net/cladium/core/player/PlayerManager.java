package net.cladium.core.player;

import lombok.Getter;
import net.cladium.core.CladiumPlugin;
import net.cladium.effect.Effect;
import net.cladium.effect.player.EffectData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    }

    public EffectData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public EffectData getPlayerData(UUID uuid) {
        return playersData.get(uuid);
    }

    public void remove(Player player) {
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
            Effect effect = CladiumPlugin.getInstance().getEffectManager().get(data.getCurrentEffect().getKey());
            data.setCurrentEffect(player, null);
            if (effect != null)
                data.setCurrentEffect(player, effect);
            else
                data.setEnabled(false);
        }
    }
}
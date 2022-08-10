package net.treasure.effect.task;

import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.util.TimeKeeper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ParticleTask implements Runnable {

    @Override
    public void run() {
        TimeKeeper.increaseTime();
        Iterator<Map.Entry<UUID, EffectData>> iterator = TreasurePlugin.getInstance().getPlayerManager().getPlayersData().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, EffectData> set = iterator.next();

            UUID uuid = set.getKey();
            EffectData data = set.getValue();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }

            if (!data.isEnabled())
                continue;

            if (!player.isGliding())
                continue;

            var current = data.getCurrentEffect();
            if (current != null)
                current.doTick(player, data);
        }
    }
}
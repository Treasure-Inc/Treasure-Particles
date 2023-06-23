package net.treasure.core.gui.task;

import lombok.Getter;
import net.treasure.core.gui.GUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashSet;
import java.util.Set;

public class GUITask implements Runnable {

    @Getter
    private static final Set<Player> players = new HashSet<>();

    @Override
    public void run() {
        var iterator = players.iterator();
        while (iterator.hasNext()) {
            var player = iterator.next();
            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof GUIHolder holder) || holder.getAnimatedSlots() == null) {
                iterator.remove();
                continue;
            }
            for (var set : holder.getAnimatedSlots().entrySet()) {
                int slot = set.getKey();
                var data = set.getValue();
                if (data == null || slot < 0 || slot > 53) continue;
                var item = holder.getInventory().getItem(slot);
                if (item == null) continue;
                if (item.getItemMeta() != null && item.getItemMeta() instanceof LeatherArmorMeta meta) {
                    meta.setColor(data.next(null));
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
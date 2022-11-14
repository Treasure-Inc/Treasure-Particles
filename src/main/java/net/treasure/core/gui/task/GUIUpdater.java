package net.treasure.core.gui.task;

import lombok.Getter;
import net.treasure.core.gui.GUIHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIUpdater implements Runnable {

    @Getter
    private static final List<UUID> players = new ArrayList<>();

    @Override
    public void run() {
        var iterator = players.iterator();
        while (iterator.hasNext()) {
            var uuid = iterator.next();
            var player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof GUIHolder holder)) {
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
                    meta.setColor(data.nextBukkit());
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
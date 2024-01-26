package net.treasure.particles.gui.task;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import net.treasure.particles.gui.GUIHolder;
import net.treasure.particles.util.message.MessageUtils;
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
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof GUIHolder holder) || holder.getSlotData() == null) {
                iterator.remove();
                continue;
            }
            for (var set : holder.getSlotData().entrySet()) {
                int slot = set.getKey();
                var data = set.getValue();
                if (data == null) continue;
                var colorData = data.colorData();
                if (colorData == null) continue;
                var item = holder.getInventory().getItem(slot);
                if (item == null) continue;
                if (item.getItemMeta() != null && item.getItemMeta() instanceof LeatherArmorMeta meta) {
                    var color = colorData.next(null);
                    meta.setColor(color);
                    if (data.name() != null)
                        meta.setDisplayName(MessageUtils.gui(data.name(), TextColor.color(color.getRed(), color.getGreen(), color.getBlue())));
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
package net.treasure.core.command.gui.task;

import lombok.Getter;
import net.treasure.color.player.ColorData;
import net.treasure.core.command.gui.GUIHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class GUIUpdater implements Runnable {

    @Getter
    private static final List<UUID> players = new ArrayList<>();

    @Override
    public void run() {
        Iterator<UUID> iterator = players.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof GUIHolder)) {
                iterator.remove();
                continue;
            }
            GUIHolder holder = (GUIHolder) player.getOpenInventory().getTopInventory().getHolder();
            for (Map.Entry<Integer, ColorData> set : holder.getUpdateSlots().entrySet()) {
                int slot = set.getKey();
                ColorData data = set.getValue();
                if (data == null || slot < 0 || slot > 53) continue;
                ItemStack item = holder.getInventory().getItem(slot);
                if (item == null) continue;
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                if (meta == null) continue;
                meta.setColor(data.nextBukkit());
                item.setItemMeta(meta);
            }
        }
    }
}
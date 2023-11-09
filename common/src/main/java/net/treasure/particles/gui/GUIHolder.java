package net.treasure.particles.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.data.RGBColorData;
import net.treasure.particles.util.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
@Setter
public class GUIHolder implements InventoryHolder {
    private Inventory inventory;
    private int page;
    private HashMap<Integer, SlotData> slotData;
    @Accessors(fluent = true)
    private Consumer<Player> closeListener;
    @Accessors(fluent = true)
    private boolean hasAnimation = false;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        this.slotData = new HashMap<>();
        this.closeListener = null;
        this.hasAnimation = false;
    }

    public void setItem(int where, CustomItem item, ClickListener listener, RGBColorData colorData) {
        inventory.setItem(where, item.build());
        slotData.put(where, new SlotData(listener, colorData));
        if (colorData != null)
            hasAnimation = true;
    }

    public void setItem(int where, CustomItem item, ClickListener listener) {
        inventory.setItem(where, item.build());
        slotData.put(where, new SlotData(listener, null));
    }

    public void setItem(int where, CustomItem item) {
        inventory.setItem(where, item.build());
    }

    public void performClick(InventoryClickEvent event) {
        var where = event.getSlot();
        var data = slotData.get(where);
        if (data == null || data.listener() == null) return;
        data.listener().onClick(event);
    }

    public void nextPage() {
        page++;
    }

    public void previousPage() {
        page--;
    }
}
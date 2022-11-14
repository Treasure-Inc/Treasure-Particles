package net.treasure.core.gui;

import lombok.Getter;
import lombok.Setter;
import net.treasure.color.data.RGBColorData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

@Getter
@Setter
public class GUIHolder implements InventoryHolder {
    private Inventory inventory;
    private int page;
    private HashMap<Integer, RGBColorData> animatedSlots;
}
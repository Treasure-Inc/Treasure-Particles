package net.treasure.core.command.gui;

import lombok.Getter;
import lombok.Setter;
import net.treasure.color.data.ColorData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

@Getter
@Setter
public class GUIHolder implements InventoryHolder {
    private Inventory inventory;
    private int page;
    private HashMap<Integer, ColorData> updateSlots;
}
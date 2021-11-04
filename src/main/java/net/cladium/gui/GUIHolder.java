package net.cladium.gui;

import lombok.Getter;
import lombok.Setter;
import net.cladium.color.player.ColorData;
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
package net.treasure.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ClickListener {
    void onClick(InventoryClickEvent event);
}
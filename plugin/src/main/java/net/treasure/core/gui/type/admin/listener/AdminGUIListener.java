package net.treasure.core.gui.type.admin.listener;

import net.treasure.common.Keys;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.config.GUISounds;
import net.treasure.core.gui.type.admin.AdminGUI;
import net.treasure.core.gui.type.admin.AdminGUIHolder;
import net.treasure.core.gui.type.admin.FilterCategory;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.util.tuples.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class AdminGUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof AdminGUIHolder holder)) return;

        event.setCancelled(true);
        var item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (item.getItemMeta() == null) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();

        if (pdc.has(Keys.BUTTON_TYPE, PersistentDataType.STRING)) {
            var buttonType = pdc.get(Keys.BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) return;
            Pair<String, float[]> sound = null;
            switch (ElementType.valueOf(buttonType)) {
                case NEXT_PAGE -> {
                    sound = GUISounds.NEXT_PAGE;
                    AdminGUI.open(player, holder.getCategory(), holder.getEvent(), holder.getPage() + 1);
                }
                case PREVIOUS_PAGE -> {
                    sound = GUISounds.PREVIOUS_PAGE;
                    AdminGUI.open(player, holder.getCategory(), holder.getEvent(), holder.getPage() - 1);
                }
                case CLOSE -> player.closeInventory();
                case FILTER -> {
                    sound = GUISounds.FILTER;
                    if (event.getClick() == ClickType.MIDDLE) {
                        var filter = holder.getCategory();
                        var values = FilterCategory.values();

                        var ordinal = filter == null ? (event.isRightClick() ? values.length - 1 : 0) : filter.ordinal() + (event.isRightClick() ? -1 : 1);
                        var newFilter = ordinal >= values.length || ordinal < 0 ? null : values[ordinal];
                        AdminGUI.open(player, newFilter, holder.getEvent(), 0);
                    } else if (holder.getCategory() == FilterCategory.SUPPORTED_EVENTS) {
                        var filter = holder.getEvent();
                        var values = HandlerEvent.values();

                        var ordinal = filter == null ? (event.isRightClick() ? values.length - 1 : 0) : filter.ordinal() + (event.isRightClick() ? -1 : 1);
                        var newFilter = ordinal >= values.length || ordinal < 0 ? null : values[ordinal];
                        AdminGUI.open(player, holder.getCategory(), newFilter, 0);
                    }
                }
            }
            if (sound != null)
                player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
        }
    }

}
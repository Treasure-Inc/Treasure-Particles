package net.treasure.core.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.core.gui.type.GUI;
import net.treasure.core.gui.type.color.ColorsGUI;
import net.treasure.core.gui.type.color.ColorsGUIHolder;
import net.treasure.core.gui.type.effects.EffectsGUI;
import net.treasure.core.gui.type.effects.EffectsGUIHolder;
import net.treasure.util.item.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public class GUIStyle {
    Type type;
    String id;
    String title;
    int size;
    Map<GUI, String[]> layouts;

    @Getter
    @AllArgsConstructor
    public enum Type {
        DEFAULT((style, inventory) -> {
            int[] slots;
            ItemStack item;

            if (inventory.getHolder() instanceof EffectsGUIHolder) {
                if (!EffectsGUI.BORDERS.isEnabled()) return;
                slots = EffectsGUI.BORDERS.slots;
                item = EffectsGUI.BORDERS.item;
            } else if (inventory.getHolder() instanceof ColorsGUIHolder) {
                if (!ColorsGUI.BORDERS.isEnabled()) return;
                slots = ColorsGUI.BORDERS.slots;
                item = ColorsGUI.BORDERS.item;
            } else return;

            for (int slot : slots)
                inventory.setItem(slot, new CustomItem(item).emptyName().build());
        }),
        CUSTOM((style, inventory) -> {

        });
        final BiConsumer<GUIStyle, Inventory> decoration;
    }
}
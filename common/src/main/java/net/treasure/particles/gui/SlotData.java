package net.treasure.particles.gui;

import net.treasure.particles.color.data.RGBColorData;
import net.treasure.particles.util.item.CustomItem;

public record SlotData(ClickListener listener, CustomItem item, RGBColorData colorData, String name) {
}
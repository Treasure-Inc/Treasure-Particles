package net.treasure.particles.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.ColorManager;
import net.treasure.particles.color.data.RGBColorData;
import net.treasure.particles.color.generator.Gradient;
import net.treasure.particles.color.group.ColorGroup;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.util.item.CustomItem;
import net.treasure.particles.util.logging.ComponentLogger;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.Color;
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

    public void setItem(int where, CustomItem item, ClickListener listener, RGBColorData colorData, String name) {
        inventory.setItem(where, item.build());
        slotData.put(where, new SlotData(listener, item, colorData, name));
        if (colorData != null)
            hasAnimation = true;
    }

    public void setItem(int where, CustomItem item, ClickListener listener) {
        inventory.setItem(where, item.build());
        slotData.put(where, new SlotData(listener, item, null, null));
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

    public Pair<RGBColorData, Color> colorData(EffectData data, Effect effect, ColorGroup colorGroup, boolean canUseAny, ColorManager colorManager, float colorCycleSpeed) {
        Color color = null;
        RGBColorData colorData = null;

        if (effect.getColorAnimation() != null) {
            var scheme = colorManager.getColorScheme(effect.getColorAnimation());
            if (scheme != null) {
                colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                color = colorData.next(null);
            } else {
                try {
                    color = Gradient.hex2Rgb(effect.getColorAnimation());
                } catch (Exception ignored) {
                    ComponentLogger.error(effect, "Unknown color animation value: " + effect.getColorAnimation());
                }
            }
        } else if (canUseAny) {
            var preference = data.getColorPreference(effect);
            var scheme = preference == null ? colorGroup.getAvailableOptions().get(0).colorScheme() : preference;
            colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
            color = colorData.next(null);
        }

        return new Pair<>(colorData, color);
    }
}
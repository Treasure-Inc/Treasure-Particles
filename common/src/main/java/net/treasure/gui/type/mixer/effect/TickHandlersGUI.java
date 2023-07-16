package net.treasure.gui.type.mixer.effect;

import net.treasure.TreasureParticles;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.type.mixer.MixerGUI;
import net.treasure.gui.type.mixer.MixerHolder;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;

import static net.treasure.gui.type.GUI.HANDLERS;

public class TickHandlersGUI {

    private static Translations translations;

    public static EnumMap<HandlerEvent, ItemStack> ELEMENTS = new EnumMap<>(HandlerEvent.class);

    public static void configure() {
        translations = TreasureParticles.getTranslations();
    }

    public static void setItems() {
        for (var event : HandlerEvent.values()) {
            ELEMENTS.put(event, GUIElements.getItemStack(HANDLERS, event.translationKey(), new ItemStack(Material.PAPER)));
        }
    }

    public static void open(EffectData data, MixerHolder mixerHolder, Effect effect) {
        // Variables
        var player = data.player;
        var handlers = effect.mixerCompatibleTickHandlers();

        // Create inventory
        var holder = new TickHandlersHolder();
        var inventory = Bukkit.createInventory(holder, (handlers.size() / 9 * 9) + 9, effect.getParsedDisplayName());
        holder.setInventory(inventory);
        holder.closeListener(p -> {
            if (!holder.lockCloseListener)
                MixerGUI.open(player, mixerHolder);
        });

        for (int i = 0, handlersSize = handlers.size(); i < handlersSize; i++) {
            var handler = handlers.get(i);
            var selected = mixerHolder.isSelected(handler);
            var eventTranslation = translations.get("events." + handler.event.translationKey());
            holder.setItem(i, new CustomItem(ELEMENTS.get(handler.event))
                    .setDisplayName(MessageUtils.gui(handler.displayName))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .glow(selected)
                    .addLore(
                            MessageUtils.gui(Translations.HANDLER_EVENT_TYPE, eventTranslation),
                            "",
                            selected ? MessageUtils.gui(Translations.HANDLERS_GUI_SELECTED) : MessageUtils.gui(Translations.HANDLERS_GUI_SELECT),
                            selected ? MessageUtils.gui(Translations.HANDLERS_GUI_UNSELECT) : null,
                            selected && handler.mixerOptions.lockEvent ? MessageUtils.gui(Translations.HANDLER_EVENT_LOCKED, eventTranslation) : null
                    ), event -> {
                if (selected) {
                    mixerHolder.remove(handler);
                    holder.lockCloseListener = true;
                    open(data, mixerHolder, effect);
                    GUISounds.play(player, GUISounds.UNSELECT_HANDLER);
                    return;
                }
                mixerHolder.add(effect, handler);
                holder.lockCloseListener = true;
                open(data, mixerHolder, effect);
                GUISounds.play(player, GUISounds.SELECT_HANDLER);
            });
        }

        player.openInventory(inventory);
    }
}
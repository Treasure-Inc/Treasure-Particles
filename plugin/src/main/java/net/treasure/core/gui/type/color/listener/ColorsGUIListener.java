package net.treasure.core.gui.type.color.listener;

import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.config.GUISounds;
import net.treasure.core.gui.task.GUITask;
import net.treasure.core.gui.type.color.ColorsGUI;
import net.treasure.core.gui.type.color.ColorsGUIHolder;
import net.treasure.core.gui.type.effects.EffectsGUI;
import net.treasure.locale.Translations;
import net.treasure.util.Pair;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class ColorsGUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ColorsGUIHolder holder)) return;
        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        var data = TreasurePlugin.getInstance().getPlayerManager().getEffectData(player);

        if (item.getItemMeta() == null) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();

        var effect = holder.getEffect();

        if (pdc.has(Keys.BUTTON_TYPE, PersistentDataType.STRING)) {
            var buttonType = pdc.get(Keys.BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) return;
            Pair<String, float[]> sound = null;
            switch (ElementType.valueOf(buttonType)) {
                case NEXT_PAGE -> {
                    sound = GUISounds.NEXT_PAGE;
                    ColorsGUI.open(player, effect, holder.getPage() + 1);
                }
                case PREVIOUS_PAGE -> {
                    sound = GUISounds.PREVIOUS_PAGE;
                    ColorsGUI.open(player, effect, holder.getPage() - 1);
                }
                case RANDOM_COLOR -> {
                    var colors = effect.getColorGroup().getAvailableOptions().stream().filter(option -> option.canUse(player)).toList();
                    if (colors.isEmpty()) return;
                    sound = event.isRightClick() ? GUISounds.RANDOM_COLOR : GUISounds.RANDOM_EFFECT;

                    var random = colors.get(new Random().nextInt(colors.size()));
                    data.setColorPreference(effect, random.colorScheme());
                    if (event.isRightClick()) {
                        MessageUtils.sendParsed(player, Translations.COLOR_SCHEME_SELECTED, random.colorScheme().getDisplayName(), effect.getDisplayName());
                    } else {
                        data.setCurrentEffect(effect);
                        MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                        player.closeInventory();
                    }
                }
                case BACK -> EffectsGUI.open(player, 0);
            }
            if (sound != null)
                player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
            return;
        }

        if (pdc.has(Keys.COLOR, PersistentDataType.STRING)) {
            var colorSchemeKey = pdc.get(Keys.COLOR, PersistentDataType.STRING);
            var colorScheme = TreasurePlugin.getInstance().getColorManager().getColorScheme(colorSchemeKey);
            var option = effect.getColorGroup().getOption(colorScheme);
            if (!option.canUse(player)) {
                player.closeInventory();
                return;
            }
            Pair<String, float[]> sound;

            if (event.isRightClick()) {
                sound = GUISounds.SELECT_COLOR;

                MessageUtils.sendParsed(player, Translations.COLOR_SCHEME_SELECTED, colorScheme.getDisplayName(), effect.getDisplayName());
                EffectsGUI.open(player, 0);
            } else {
                sound = GUISounds.SELECT_EFFECT;

                data.setCurrentEffect(effect);
                data.setColorPreference(effect, colorScheme);
                MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                player.closeInventory();
            }

            player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ColorsGUIHolder)
            GUITask.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}
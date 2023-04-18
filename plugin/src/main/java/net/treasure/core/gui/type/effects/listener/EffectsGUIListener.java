package net.treasure.core.gui.type.effects.listener;

import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.type.color.ColorsGUI;
import net.treasure.core.gui.type.effects.EffectsGUI;
import net.treasure.core.gui.type.effects.EffectsGUIHolder;
import net.treasure.core.gui.config.ElementType;
import net.treasure.core.gui.config.GUISounds;
import net.treasure.core.gui.task.GUITask;
import net.treasure.locale.Translations;
import net.treasure.util.tuples.Pair;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class EffectsGUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof EffectsGUIHolder holder)) return;

        event.setCancelled(true);
        var item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        var data = TreasurePlugin.getInstance().getPlayerManager().getEffectData(player);

        if (item.getItemMeta() == null) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();

        var effectManager = TreasurePlugin.getInstance().getEffectManager();

        if (pdc.has(Keys.BUTTON_TYPE, PersistentDataType.STRING)) {
            var buttonType = pdc.get(Keys.BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) return;
            Pair<String, float[]> sound = null;
            switch (ElementType.valueOf(buttonType)) {
                case NEXT_PAGE -> {
                    sound = GUISounds.NEXT_PAGE;
                    EffectsGUI.open(player, holder.getPage() + 1);
                }
                case PREVIOUS_PAGE -> {
                    sound = GUISounds.PREVIOUS_PAGE;
                    EffectsGUI.open(player, holder.getPage() - 1);
                }
                case RANDOM_EFFECT -> {
                    var effects = effectManager.getEffects().stream().filter(effect -> effect.canUse(player)).toList();
                    if (effects.isEmpty()) return;
                    sound = GUISounds.RANDOM_EFFECT;

                    var effect = effects.get(new Random().nextInt(effects.size()));
                    data.setCurrentEffect(effect);
                    MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                    player.closeInventory();
                }
                case CLOSE -> player.closeInventory();
                case RESET -> {
                    sound = GUISounds.RESET;
                    data.setCurrentEffect(null);
                    EffectsGUI.open(player, holder.getPage());
                }
            }
            if (sound != null)
                player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
            return;
        }

        if (pdc.has(Keys.EFFECT, PersistentDataType.STRING)) {
            var effectKey = pdc.get(Keys.EFFECT, PersistentDataType.STRING);
            var effect = effectManager.get(effectKey);
            if (!effect.canUse(player)) {
                player.closeInventory();
                return;
            }
            if (event.isRightClick() && effect.getColorGroup() != null) {
                ColorsGUI.open(player, effect, 0);
                return;
            }
            data.setCurrentEffect(effect);
            MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
            player.closeInventory();

            var sound = GUISounds.SELECT_EFFECT;
            player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof EffectsGUIHolder)
            GUITask.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}
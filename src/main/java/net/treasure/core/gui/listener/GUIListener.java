package net.treasure.core.gui.listener;

import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.gui.EffectsGUI;
import net.treasure.core.gui.GUIHolder;
import net.treasure.core.gui.config.GUISounds;
import net.treasure.core.gui.task.GUIUpdater;
import net.treasure.locale.Translations;
import net.treasure.util.Pair;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof GUIHolder holder)) return;

        event.setCancelled(true);
        var item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        var data = TreasurePlugin.getInstance().getPlayerManager().getEffectData(player);

        if (item.getItemMeta() == null) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();

        if (pdc.has(Keys.BUTTON_TYPE, PersistentDataType.STRING)) {
            var buttonType = pdc.get(Keys.BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) return;
            Pair<String, float[]> sound = null;
            switch (buttonType) {
                case "NEXT_PAGE" -> {
                    sound = GUISounds.NEXT_PAGE;
                    EffectsGUI.open(player, holder.getPage() + 1);
                }
                case "PREVIOUS_PAGE" -> {
                    sound = GUISounds.PREVIOUS_PAGE;
                    EffectsGUI.open(player, holder.getPage() - 1);
                }
                case "CLOSE" -> player.closeInventory();
                case "RESET" -> {
                    sound = GUISounds.RESET;
                    data.setCurrentEffect(player, null);
                    EffectsGUI.open(player, holder.getPage());
                }
            }
            if (sound != null)
                player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
            return;
        }

        if (pdc.has(Keys.EFFECT, PersistentDataType.STRING)) {
            var effectKey = pdc.get(Keys.EFFECT, PersistentDataType.STRING);
            var effect = TreasurePlugin.getInstance().getEffectManager().get(effectKey);
            if (!effect.canUse(player)) {
                player.closeInventory();
                return;
            }
            data.setCurrentEffect(player, effect);
            MessageUtils.sendParsed(player, String.format(Translations.EFFECT_SELECTED, effect.getDisplayName()));
            player.closeInventory();

            var sound = GUISounds.SELECT;
            player.playSound(player.getLocation(), sound.getKey(), sound.getValue()[0], sound.getValue()[1]);
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GUIHolder)
            GUIUpdater.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}
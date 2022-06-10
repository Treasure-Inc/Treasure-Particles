package net.treasure.core.command.gui.listener;

import net.treasure.common.Keys;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.command.gui.EffectsGUI;
import net.treasure.core.command.gui.GUIHolder;
import net.treasure.core.command.gui.task.GUIUpdater;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.locale.Messages;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GUIHolder holder)
            event.setCancelled(true);
        else return;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        EffectData data = TreasurePlugin.getInstance().getPlayerManager().getPlayerData(player);

        if (item.getItemMeta() == null) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();

        if (pdc.has(Keys.BUTTON_TYPE, PersistentDataType.STRING)) {
            var buttonType = pdc.get(Keys.BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) return;
            switch (buttonType) {
                case "PREVIOUS_PAGE" -> new EffectsGUI().open(player, holder.getPage() - 1);
                case "NEXT_PAGE" -> new EffectsGUI().open(player, holder.getPage() + 1);
                case "CLOSE" -> player.closeInventory();
                case "RESET" -> {
                    data.setCurrentEffect(player, null);
                    new EffectsGUI().open(player, holder.getPage());
                }
            }
            return;
        }

        if (pdc.has(Keys.EFFECT, PersistentDataType.STRING)) {
            String effectKey = pdc.get(Keys.EFFECT, PersistentDataType.STRING);
            Effect effect = TreasurePlugin.getInstance().getEffectManager().get(effectKey);
            if (!effect.canUse(player)) {
                player.closeInventory();
                return;
            }
            data.setCurrentEffect(player, effect);
            MessageUtils.sendParsed(player, String.format(Messages.EFFECT_SELECTED, effect.getDisplayName()));
            player.closeInventory();
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof GUIHolder)
            GUIUpdater.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}
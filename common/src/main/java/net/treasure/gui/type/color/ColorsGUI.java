package net.treasure.gui.type.color;

import net.treasure.TreasureParticles;
import net.treasure.color.data.RGBColorData;
import net.treasure.effect.Effect;
import net.treasure.gui.GUIManager;
import net.treasure.gui.config.ElementType;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.task.GUITask;
import net.treasure.gui.type.GUI;
import net.treasure.locale.Translations;
import net.treasure.player.PlayerManager;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import net.treasure.util.tuples.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static net.treasure.gui.type.GUIType.COLORS;

public class ColorsGUI extends GUI {

    protected final PlayerManager playerManager;

    private GUIElements.ElementInfo COLOR_ICON;
    private GUIElements.ElementInfo RANDOM_COLOR;
    private GUIElements.ElementInfo BACK;

    public ColorsGUI(GUIManager manager) {
        super(manager, COLORS);
        this.playerManager = TreasureParticles.getPlayerManager();
    }

    @Override
    public void reload() {
        super.reload();
        COLOR_ICON = GUIElements.element(type, ElementType.COLOR_ICON, 'C', new ItemStack(Material.LEATHER_HORSE_ARMOR));
        RANDOM_COLOR = GUIElements.element(type, ElementType.RANDOM_COLOR, 'R', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
        BACK = GUIElements.element(type, ElementType.BACK, 'b', new ItemStack(Material.STRUCTURE_VOID));
    }

    @Override
    public void open(Player player) {
        throw new RuntimeException("Unsupported");
    }

    public void open(Player player, Effect effect, int page) {
        // Variables
        var data = playerManager.getEffectData(player);
        var preference = data.getColorPreferences().get(effect.getKey());
        var colorSlots = COLOR_ICON.slots();
        var maxColors = colorSlots.length;

        // Colors
        var colors = effect.getColorGroup().getAvailableOptions().stream().filter(option -> option.canUse(player)).toList();
        if (colors.isEmpty()) {
            manager.effectsGUI().open(player);
            return;
        }

        // Create inventory
        var holder = new ColorsHolder(effect);
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.COLORS_GUI_TITLE));
        holder.setInventory(inventory);
        holder.setPage(page);

        this.commonItems(player, holder)
                .pageItems(player, holder, (page + 1) * maxColors < colors.size(), () -> open(player, effect, holder.getPage()));

        // Back button
        if (BACK.isEnabled()) for (int slot : BACK.slots())
            holder.setItem(slot,
                    new CustomItem(BACK.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_BACK)),
                    event -> {
                        manager.effectsGUI().open(player);
                        GUISounds.play(player, GUISounds.BACK);
                    });

        // Random color button
        if (RANDOM_COLOR.isEnabled())
            for (int slot : RANDOM_COLOR.slots())
                holder.setItem(slot,
                        new CustomItem(RANDOM_COLOR.item()).setDisplayName(MessageUtils.gui(Translations.COLORS_GUI_RANDOM_COLOR)),
                        event -> {
                            var random = colors.get(new Random().nextInt(colors.size()));
                            data.setColorPreference(effect, random.colorScheme());
                            if (event.isRightClick()) {
                                MessageUtils.sendParsed(player, Translations.COLOR_SCHEME_SELECTED, random.colorScheme().getDisplayName(), effect.getDisplayName());
                            } else {
                                data.setCurrentEffect(effect);
                                MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                                player.closeInventory();
                            }
                            GUISounds.play(player, event.isRightClick() ? GUISounds.RANDOM_COLOR : GUISounds.RANDOM_EFFECT);
                        });

        int index = 0;
        for (int i = page * maxColors; i < (page + 1) * maxColors; i++) {
            if (colors.size() <= i) break;
            int where = colorSlots[index];

            var option = colors.get(i);
            var scheme = option.colorScheme();

            var colorData = new RGBColorData(scheme, manager.getColorCycleSpeed(), true, false);
            var color = colorData.next(null);

            holder.setItem(where, new CustomItem(COLOR_ICON.item())
                    .setDisplayName(ChatColor.WHITE + MessageUtils.gui(scheme.getDisplayName()))
                    .addLore(MessageUtils.gui(scheme.equals(preference) ? Translations.COLORS_GUI_SCHEME_SELECTED : Translations.COLORS_GUI_SELECT_SCHEME))
                    .addLore(scheme.equals(preference) ? null : MessageUtils.gui(Translations.COLORS_GUI_SAVE_SCHEME))
                    .changeArmorColor(color)
                    .addItemFlags(ItemFlag.values()), event -> {
                if (!option.canUse(player)) {
                    player.closeInventory();
                    return;
                }
                Pair<String, float[]> sound;

                if (event.isRightClick()) {
                    sound = GUISounds.SELECT_COLOR;

                    data.setColorPreference(effect, scheme);
                    MessageUtils.sendParsed(player, Translations.COLOR_SCHEME_SELECTED, scheme.getDisplayName(), effect.getDisplayName());
                    manager.effectsGUI().open(player);
                } else {
                    sound = GUISounds.SELECT_EFFECT;

                    data.setCurrentEffect(effect);
                    data.setColorPreference(effect, scheme);
                    MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                    player.closeInventory();
                }

                GUISounds.play(player, sound);
            });
            index += 1;
        }

        player.openInventory(inventory);
        GUITask.getPlayers().add(player);
    }
}

package net.treasure.gui.type.color;

import net.treasure.TreasureParticles;
import net.treasure.color.data.RGBColorData;
import net.treasure.effect.Effect;
import net.treasure.gui.GUIManager;
import net.treasure.gui.config.ElementType;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.task.GUITask;
import net.treasure.gui.type.effects.EffectsGUI;
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

import static net.treasure.gui.type.GUI.COLORS;

public class ColorsGUI {

    private static GUIManager manager;
    private static PlayerManager playerManager;

    public static GUIElements.ElementInfo BORDERS;
    public static GUIElements.ElementInfo COLOR_ICON;
    public static GUIElements.ElementInfo NEXT_PAGE;
    public static GUIElements.ElementInfo PREVIOUS_PAGE;
    public static GUIElements.ElementInfo RANDOM_COLOR;
    public static GUIElements.ElementInfo BACK;

    public static void configure(GUIManager manager) {
        ColorsGUI.manager = manager;
        playerManager = TreasureParticles.getPlayerManager();
    }

    public static void setItems() {
        BORDERS = GUIElements.element(COLORS, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        COLOR_ICON = GUIElements.element(COLORS, ElementType.COLOR_ICON, 'C', new ItemStack(Material.LEATHER_HORSE_ARMOR));

        NEXT_PAGE = GUIElements.element(COLORS, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.element(COLORS, ElementType.PREVIOUS_PAGE, 'P', new ItemStack(Material.ENDER_EYE));
        RANDOM_COLOR = GUIElements.element(COLORS, ElementType.RANDOM_COLOR, 'R', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
        BACK = GUIElements.element(COLORS, ElementType.BACK, 'b', new ItemStack(Material.STRUCTURE_VOID));
    }

    public static void open(Player player, Effect effect, int page) {
        // Variables
        var data = playerManager.getEffectData(player);
        var preference = data.getColorPreferences().get(effect.getKey());
        var layout = manager.getStyle().getLayouts().get(COLORS);

        // Create inventory
        var holder = new ColorsHolder(effect);
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.COLORS_GUI_TITLE));
        holder.setInventory(inventory);
        holder.setPage(page);

        // Borders
        if (BORDERS.isEnabled()) for (int slot : BORDERS.slots())
            holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Colors
        var colors = effect.getColorGroup().getAvailableOptions().stream().filter(option -> option.canUse(player)).toList();
        if (colors.isEmpty()) {
            EffectsGUI.open(player, null, 0);
            return;
        }

        // Back button
        if (BACK.isEnabled()) for (int slot : BACK.slots())
            holder.setItem(slot,
                    new CustomItem(BACK.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_BACK)),
                    event -> {
                        EffectsGUI.open(player, null, 0);
                        GUISounds.play(player, GUISounds.BACK);
                    });

        // Previous page button
        if (page > 0 && PREVIOUS_PAGE.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(PREVIOUS_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_PREVIOUS_PAGE)),
                        event -> {
                            ColorsGUI.open(player, effect, holder.getPage() - 1);
                            GUISounds.play(player, GUISounds.PREVIOUS_PAGE);
                        });
        else if (PREVIOUS_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                inventory.setItem(slot, new CustomItem(BORDERS.item())
                        .emptyName()
                        .build());

        var colorSlots = COLOR_ICON.slots();
        var maxColors = colorSlots.length;

        // Next page button
        if ((page + 1) * maxColors < colors.size() && NEXT_PAGE.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(NEXT_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_NEXT_PAGE)),
                        event -> {
                            ColorsGUI.open(player, effect, holder.getPage() + 1);
                            GUISounds.play(player, GUISounds.NEXT_PAGE);
                        });
        else if (BORDERS.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

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
                    EffectsGUI.open(player, null, 0);
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

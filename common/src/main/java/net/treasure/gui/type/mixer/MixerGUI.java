package net.treasure.gui.type.mixer;

import net.treasure.TreasureParticles;
import net.treasure.color.ColorManager;
import net.treasure.color.data.RGBColorData;
import net.treasure.color.generator.Gradient;
import net.treasure.effect.EffectManager;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.mix.MixData;
import net.treasure.gui.GUIManager;
import net.treasure.gui.config.ElementType;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUIElements.ElementInfo;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.task.GUITask;
import net.treasure.gui.type.mixer.effect.TickHandlersGUI;
import net.treasure.locale.Translations;
import net.treasure.player.PlayerManager;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import net.treasure.util.tuples.Pair;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import net.wesjd.anvilgui.AnvilGUI.Slot;
import net.wesjd.anvilgui.AnvilGUI.StateSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.treasure.gui.type.GUI.MIXER;

public class MixerGUI {

    // Managers
    private static GUIManager manager;
    private static EffectManager effectManager;
    private static PlayerManager playerManager;
    private static ColorManager colorManager;
    private static Translations translations;

    // GUI Elements
    public static ElementInfo BORDERS;
    public static ElementInfo NEXT_PAGE;
    public static ElementInfo PREVIOUS_PAGE;
    public static ElementInfo DEFAULT_ICON;
    public static ElementInfo RESET;
    public static ElementInfo CLOSE;
    public static ElementInfo FILTER;
    public static ElementInfo CONFIRM;

    public static void configure(GUIManager manager) {
        MixerGUI.manager = manager;
        effectManager = TreasureParticles.getEffectManager();
        playerManager = TreasureParticles.getPlayerManager();
        colorManager = TreasureParticles.getColorManager();
        translations = TreasureParticles.getTranslations();
    }

    public static void setItems() {
        BORDERS = GUIElements.element(MIXER, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        NEXT_PAGE = GUIElements.element(MIXER, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.element(MIXER, ElementType.PREVIOUS_PAGE, 'N', new ItemStack(Material.ENDER_EYE));

        DEFAULT_ICON = GUIElements.element(MIXER, ElementType.DEFAULT_ICON, 'E', new ItemStack(Material.LEATHER_BOOTS));

        RESET = GUIElements.element(MIXER, ElementType.RESET, 'R', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        CLOSE = GUIElements.element(MIXER, ElementType.CLOSE, 'c', new ItemStack(Material.BARRIER));
        FILTER = GUIElements.element(MIXER, ElementType.FILTER, 'F', new ItemStack(Material.HOPPER));

        CONFIRM = GUIElements.element(MIXER, ElementType.CONFIRM, 'C', new ItemStack(Material.LIME_STAINED_GLASS_PANE));
    }

    public static void open(Player player) {
        var holder = new MixerHolder();
        holder.setPage(0);
        open(player, holder);
    }

    public static void open(Player player, MixerHolder holder) {
        // Variables
        var data = playerManager.getEffectData(player);
        var layout = manager.getStyle().getLayouts().get(MIXER);
        var colorCycleSpeed = manager.getColorCycleSpeed();
        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        var currentSelections = holder.getSelected().stream().map(pair -> MessageUtils.gui(pair.getKey().getDisplayName() + "<gray>:<reset> " + pair.getValue().displayName)).toList();

        // Create inventory
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.MIXER_GUI_TITLE));
        var filter = holder.getFilter();
        var page = holder.getPage();
        holder.setInventory(inventory);

        // Borders
        if (BORDERS.isEnabled()) for (int slot : BORDERS.slots())
            holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Close button
        if (CLOSE.isEnabled()) for (int slot : CLOSE.slots())
            holder.setItem(slot, new CustomItem(CLOSE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_CLOSE)), event -> player.closeInventory());

        // Effects
        var effects = effectManager.getEffects()
                .stream()
                .filter(effect ->
                        (filter == null || effect.getEvents().contains(filter)) &&
                        effect.canUse(player) &&
                        (holder.isSelected(effect) || effect.hasMixerCompatibleTickHandlers(holder.getLocked()))
                )
                .sorted((o1, o2) -> Boolean.compare(holder.isSelected(o2), holder.isSelected(o1)))
                .toList();

        // Filter button
        if (FILTER.isEnabled()) for (int slot : FILTER.slots())
            holder.setItem(slot,
                    new CustomItem(FILTER.item())
                            .setDisplayName(MessageUtils.gui(Translations.BUTTON_FILTER))
                            .setLore(Arrays.stream(HandlerEvent.values()).map(event -> MessageUtils.gui("<dark_gray> • <" + (event.equals(filter) ? "green" : "gray") + ">" + translations.get("events." + event.translationKey()))).toList()),
                    event -> {
                        if (event.getClick() == ClickType.MIDDLE) {
                            holder.setPage(0);
                            holder.setFilter(null);
                            MixerGUI.open(player, holder);
                            GUISounds.play(player, GUISounds.FILTER);
                            return;
                        }
                        var holderFilter = holder.getFilter();
                        var values = HandlerEvent.values();

                        var ordinal = holderFilter == null ? (event.isRightClick() ? values.length - 1 : 0) : holderFilter.ordinal() + (event.isRightClick() ? -1 : 1);
                        var newFilter = ordinal >= values.length || ordinal < 0 ? null : values[ordinal];
                        holder.setPage(0);
                        holder.setFilter(newFilter);

                        MixerGUI.open(player, holder);
                        GUISounds.play(player, GUISounds.FILTER);
                    });

        // Previous page button
        if (page > 0 && PREVIOUS_PAGE.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(PREVIOUS_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_PREVIOUS_PAGE)),
                        event -> {
                            holder.setPage(page - 1);
                            MixerGUI.open(player, holder);
                            GUISounds.play(player, GUISounds.PREVIOUS_PAGE);
                        });
        else if (PREVIOUS_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Reset effect button
        if (!holder.getSelected().isEmpty() && RESET.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot,
                        new CustomItem(RESET.item())
                                .setDisplayName(MessageUtils.gui(Translations.MIXER_GUI_RESET_SELECTIONS))
                                .setLore(currentSelections.isEmpty() ? null : MessageUtils.gui(Translations.MIXER_GUI_CURRENT_SELECTIONS))
                                .addLore(currentSelections.isEmpty() ? null : currentSelections),
                        event -> {
                            holder.reset();
                            MixerGUI.open(player, holder);
                            GUISounds.play(player, GUISounds.RESET);
                        });
        else if (RESET.isEnabled() && BORDERS.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Next page button
        if ((page + 1) * maxEffects < effects.size() && NEXT_PAGE.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(NEXT_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_NEXT_PAGE)),
                        event -> {
                            holder.setPage(page + 1);
                            MixerGUI.open(player, holder);
                            GUISounds.play(player, GUISounds.NEXT_PAGE);
                        });
        else if (NEXT_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Confirm button
        if (!holder.getSelected().isEmpty() && CONFIRM.isEnabled())
            for (int slot : CONFIRM.slots())
                holder.setItem(slot,
                        new CustomItem(CONFIRM.item())
                                .setDisplayName(MessageUtils.gui(Translations.MIXER_GUI_CONFIRM))
                                .setLore(currentSelections.isEmpty() ? null : MessageUtils.gui(Translations.MIXER_GUI_CURRENT_SELECTIONS))
                                .addLore(currentSelections.isEmpty() ? null : currentSelections)
                                .addLore(!holder.needsColorGroup() ? null : MessageUtils.gui(Translations.MIXER_GUI_PREFERRED_COLOR_GROUP_NEEDED)),
                        event -> {
                            if (holder.needsColorGroup()) return;
                            new AnvilGUI.Builder()
                                    .jsonTitle(MessageUtils.json(Translations.MIXER_GUI_ENTER_NAME))
                                    .itemLeft(new ItemStack(Material.PAPER))
                                    .text("...")
                                    .plugin(TreasureParticles.getPlugin())
                                    .onClick((clickedSlot, stateSnapshot) -> {
                                        if (clickedSlot != Slot.OUTPUT)
                                            return Collections.emptyList();
                                        return confirmName(data, stateSnapshot, holder);
                                    })
                                    .open(player);
                        });
        else if (CONFIRM.isEnabled() && BORDERS.isEnabled())
            for (int slot : CONFIRM.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Effects
        int index = 0;
        for (int i = page * maxEffects; i < (page + 1) * maxEffects; i++) {
            if (effects.size() <= i) break;

            int where = effectSlots[index];

            var effect = effects.get(i);
            Color color = null;
            RGBColorData colorData = null;

            var prefColorGroup = false;
            var colorGroup = effect.getColorGroup();

            if (effect.getArmorColor() != null) {
                var scheme = colorManager.getColorScheme(effect.getArmorColor());
                if (scheme != null) {
                    colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                    color = colorData.next(null);
                } else {
                    try {
                        color = Gradient.hex2Rgb(effect.getArmorColor());
                    } catch (Exception ignored) {
                        TreasureParticles.logger().warning(effect.getPrefix() + "Unknown armor color value: " + effect.getArmorColor());
                    }
                }
            } else if (colorGroup != null) {
                prefColorGroup = colorGroup.getKey().equals(holder.getPrefColorGroup());
                var preference = data.getColorPreference(effect);
                var scheme = preference == null ? colorGroup.getAvailableOptions().get(0).colorScheme() : preference;
                colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                color = colorData.next(null);
            }

            var compatibleTickHandlers = effect.mixerCompatibleTickHandlers();

            boolean anySelected = holder.isSelected(effect);
            boolean allSelected = true;
            boolean hasCompatibleSelection = false;
            for (var handler : compatibleTickHandlers) {
                if (holder.isSelected(handler)) continue;
                allSelected = false;
                if (!holder.isLocked(handler.event))
                    hasCompatibleSelection = true;
            }

            boolean finalAllSelected = allSelected;
            holder.setItem(where, new CustomItem(effect.getIcon())
                    .setDisplayName(effect.getParsedDisplayName())
                    .addLore(effect.getDescription())
                    .addLore(effect.getDescription() != null ? ChatColor.AQUA.toString() : null)
                    .addLore(
                            colorGroup != null ? MessageUtils.gui(Translations.MIXER_GUI_HAS_DYNAMIC_COLOR) : null,
                            colorGroup != null ? MessageUtils.gui(prefColorGroup ? Translations.MIXER_GUI_PREFERRED_COLOR_GROUP : Translations.MIXER_GUI_PREFER_COLOR_GROUP) : null
                    )
                    .addLore(allSelected ? MessageUtils.gui(Translations.MIXER_GUI_ALL_SELECTED) : null)
                    .addLore(hasCompatibleSelection ? MessageUtils.gui(Translations.MIXER_GUI_SELECT_ALL) : null)
                    .addLore(MessageUtils.gui(Translations.MIXER_GUI_SELECT_HANDLERS))
                    .addLore(anySelected ? MessageUtils.gui(Translations.MIXER_GUI_UNSELECT_ALL) : null)
                    .changeArmorColor(color)
                    .glow(anySelected)
                    .addItemFlags(ItemFlag.values()), event -> {
                if (event.getClick() == ClickType.MIDDLE) {
                    holder.remove(effect);
                    MessageUtils.sendParsed(player, Translations.MIXER_GUI_UNSELECTED_ALL, effect.getDisplayName());
                    open(player, holder);
                    return;
                }
                if (event.getClick() == ClickType.SHIFT_RIGHT && colorGroup != null) {
                    holder.setPrefColorGroup(colorGroup.getKey());
                    open(player, holder);
                    return;
                }
                if (event.isRightClick()) {
                    TickHandlersGUI.open(data, holder, effect);
                    GUISounds.play(player, GUISounds.MIXER_SELECT_EFFECT);
                    return;
                }
                if (finalAllSelected) return;
                for (var handler : compatibleTickHandlers) {
                    if (holder.isSelected(handler)) continue;
                    if (holder.isLocked(handler.event)) continue;
                    holder.add(effect, handler);
                }
                MessageUtils.sendParsed(player, Translations.MIXER_GUI_SELECTED_ALL, effect.getDisplayName());
                open(player, holder);
                GUISounds.play(player, GUISounds.SELECT_HANDLER);
            }, colorData);
            index += 1;
        }

        player.openInventory(inventory);
        if (holder.hasAnimation()) GUITask.getPlayers().add(player);
    }

    private static List<ResponseAction> confirmName(EffectData data, StateSnapshot stateSnapshot, MixerHolder holder) {
        if (holder.needsColorGroup()) {
            MessageUtils.sendParsed(data.player, Translations.MIX_FAILED);
            return Collections.singletonList(ResponseAction.close());
        }
        var text = stateSnapshot.getText();
        if (data.hasMixData(text)) {
            MessageUtils.sendParsed(data.player, Translations.MIXER_GUI_NAME_ALREADY_USED);
            return Collections.emptyList();
        }

        var mixData = new MixData();
        mixData.name(text);
        mixData.needsColorGroup(holder.needsColorGroup());
        mixData.prefColorGroup(holder.getPrefColorGroup());
        mixData.handlers(new ArrayList<>(holder.getSelected().stream().map(pair -> new Pair<>(pair.getKey().getKey(), pair.getValue().key)).toList()));

        for (var pair : holder.getSelected()) {
            var effect = pair.getKey().getKey();
            var depends = pair.getValue().mixerOptions.depends;
            if (depends == null || depends.isEmpty()) continue;
            for (var depend : depends)
                if (mixData.handlers().stream().noneMatch(p -> p.getKey().equals(effect) && p.getValue().equals(depend)))
                    mixData.handlers().add(new Pair<>(effect, depend));
        }

        data.getMixData().add(mixData);
        data.setCurrentEffect(mixData.get());
        MessageUtils.sendParsed(data.player, Translations.MIX_CREATED);
        GUISounds.play(data.player, GUISounds.CONFIRM);
        return Collections.singletonList(ResponseAction.close());
    }
}
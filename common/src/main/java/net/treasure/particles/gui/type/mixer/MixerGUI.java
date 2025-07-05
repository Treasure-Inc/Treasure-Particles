package net.treasure.particles.gui.type.mixer;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.ColorManager;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.mix.MixData;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.gui.config.ElementType;
import net.treasure.particles.gui.config.GUIElements;
import net.treasure.particles.gui.config.GUISounds;
import net.treasure.particles.gui.task.GUITask;
import net.treasure.particles.gui.type.GUI;
import net.treasure.particles.gui.type.GUIType;
import net.treasure.particles.gui.type.mixer.effect.TickHandlersGUI;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.util.item.CustomItem;
import net.treasure.particles.util.message.MessageUtils;
import net.treasure.particles.util.tuples.Pair;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import net.wesjd.anvilgui.AnvilGUI.Slot;
import net.wesjd.anvilgui.AnvilGUI.StateSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MixerGUI extends GUI {

    // Managers
    private final PlayerManager playerManager;
    private final ColorManager colorManager;

    // GUI Elements
    private GUIElements.ElementInfo DEFAULT_ICON;
    private GUIElements.ElementInfo RESET;
    private GUIElements.ElementInfo CONFIRM;

    public MixerGUI(GUIManager manager) {
        super(manager, GUIType.MIXER);
        this.effectManager = TreasureParticles.getEffectManager();
        this.playerManager = TreasureParticles.getPlayerManager();
        this.colorManager = TreasureParticles.getColorManager();
    }

    @Override
    public void reload() {
        super.reload();
        DEFAULT_ICON = GUIElements.element(type, ElementType.DEFAULT_ICON, 'E', new ItemStack(Material.LEATHER_BOOTS));
        RESET = GUIElements.element(type, ElementType.RESET, 'R', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        CONFIRM = GUIElements.element(type, ElementType.CONFIRM, 'C', new ItemStack(Material.LIME_STAINED_GLASS_PANE));
    }

    @Override
    public void open(Player player) {
        var holder = new MixerHolder();
        open(player, holder);
    }

    public void open(Player player, MixerHolder holder) {
        // Variables
        var data = playerManager.getEffectData(player);
        var colorCycleSpeed = manager.getColorCycleSpeed();
        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        var filter = holder.getFilter();
        var page = holder.getPage();

        var currentSelections = holder.getSelected()
                .stream()
                .map(pair -> MessageUtils.gui(pair.getKey().getDisplayName() + (pair.getValue().event != null ? "<gray><!b>:<white><!b> " + translations.get("events." + pair.getValue().event.translationKey()) : "")))
                .toList();
        var limit = data.getMixEffectLimit();
        var selectedEffectsSize = holder.selectedEffectsSize();
        var canSelectAnotherEffect = limit == -1 || limit > selectedEffectsSize;

        // Create inventory
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.MIXER_GUI_TITLE) + (limit != -1 ? " (" + (limit - selectedEffectsSize) + ")" : ""));
        holder.setInventory(inventory);
        holder.canSelectAnotherEffect(canSelectAnotherEffect);

        // Effects
        var effects = effectManager.getEffects()
                .stream()
                .filter(effect ->
                        (filter == null || effect.getEvents().contains(filter)) &&
                                effect.canUse(player) &&
                                !effect.mixerCompatibleTickHandlersGUI(holder).isEmpty()
                )
                .sorted((o1, o2) -> Boolean.compare(holder.isSelected(o2), holder.isSelected(o1)))
                .toList();

        holder.setAvailableFilters(effectManager.getEffects()
                .stream()
                .filter(effect -> effect.canUse(player) && !effect.mixerCompatibleTickHandlers(holder).isEmpty())
                .flatMap(effect -> effect.getEvents().stream().filter(event -> event != HandlerEvent.STATIC))
                .distinct()
                .toList()
        );

        super.commonItems(player, holder)
                .pageItems(player, holder, (page + 1) * maxEffects < effects.size(), () -> open(player, holder))
                .filterItem(player, holder, () -> open(player, holder));

        // Reset effect button
        if (!holder.getSelected().isEmpty() && RESET.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot,
                        new CustomItem(RESET.item())
                                .setDisplayName(MessageUtils.gui(Translations.MIXER_GUI_RESET_SELECTIONS))
                                .setLore(currentSelections.isEmpty() ? null : List.of("", MessageUtils.gui(Translations.MIXER_GUI_CURRENT_SELECTIONS)))
                                .addLore(currentSelections.isEmpty() ? null : currentSelections),
                        event -> {
                            holder.reset();
                            open(player, holder);
                            GUISounds.play(player, GUISounds.RESET);
                        });
        else if (RESET.isEnabled() && BORDERS.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Confirm button
        if (!holder.getSelected().isEmpty() && CONFIRM.isEnabled())
            for (int slot : CONFIRM.slots())
                holder.setItem(slot,
                        new CustomItem(CONFIRM.item())
                                .setDisplayName(MessageUtils.gui(Translations.MIXER_GUI_CONFIRM))
                                .setLore(currentSelections.isEmpty() ? null : List.of("", MessageUtils.gui(Translations.MIXER_GUI_CURRENT_SELECTIONS)))
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

            var colorGroup = effect.getColorGroup();
            var canUseAny = colorGroup != null && colorGroup.canUseAny(player);

            var pair = holder.colorData(data, effect, colorGroup, canUseAny, colorManager, colorCycleSpeed);
            var colorData = pair.getKey();
            var color = pair.getValue();

            var prefColorGroup = canUseAny && colorGroup.getKey().equals(holder.getPrefColorGroup());

            var compatibleTickHandlers = effect.mixerCompatibleTickHandlers(holder);

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
                    .addLore(colorGroup == null ? null : List.of(
                            MessageUtils.gui(Translations.MIXER_GUI_HAS_DYNAMIC_COLOR),
                            MessageUtils.gui(prefColorGroup ? Translations.MIXER_GUI_PREFERRED_COLOR_GROUP : Translations.MIXER_GUI_PREFER_COLOR_GROUP),
                            ChatColor.AQUA.toString())
                    )
                    .addLore(allSelected ? MessageUtils.gui(Translations.MIXER_GUI_ALL_SELECTED) : null)
                    .addLore(hasCompatibleSelection && canSelectAnotherEffect ? MessageUtils.gui(Translations.MIXER_GUI_SELECT_ALL) : null)
                    .addLore(anySelected || canSelectAnotherEffect ? MessageUtils.gui(Translations.MIXER_GUI_SELECT_HANDLERS) : null)
                    .addLore(anySelected ? MessageUtils.gui(Translations.MIXER_GUI_UNSELECT_ALL) : null)
                    .changeColor(color)
                    .glow(anySelected)
                    .addItemFlags(ItemFlag.values()), event -> {
                if (event.getClick() == ClickType.MIDDLE) {
                    holder.remove(effect);
                    MessageUtils.sendParsed(player, Translations.MIXER_GUI_UNSELECTED_ALL, effect.getDisplayName());
                    GUISounds.play(player, GUISounds.MIXER_UNSELECT_EFFECT);
                    open(player, holder);
                    return;
                }
                if (event.getClick() == ClickType.SHIFT_RIGHT && colorGroup != null) {
                    holder.setPrefColorGroup(colorGroup.getKey());
                    open(player, holder);
                    return;
                }
                if (!canSelectAnotherEffect) return;
                if (event.isRightClick()) {
                    TickHandlersGUI.open(data, holder, effect);
                    GUISounds.play(player, GUISounds.MIXER_SELECT_EFFECT);
                    return;
                }
                if (finalAllSelected) return;
                for (var handler : compatibleTickHandlers) {
                    if (holder.isSelected(handler)) continue;
                    holder.add(effect, handler);
                }
                MessageUtils.sendParsed(player, Translations.MIXER_GUI_SELECTED_ALL, effect.getDisplayName());
                open(player, holder);
                GUISounds.play(player, GUISounds.SELECT_HANDLER);
            }, colorData, effect.isNameColorAnimationEnabled() ? effect.getDisplayName() : null);
            index += 1;
        }

        player.openInventory(inventory);
        if (holder.hasAnimation()) GUITask.getPlayers().add(player);
    }

    private static List<ResponseAction> confirmName(PlayerEffectData data, StateSnapshot stateSnapshot, MixerHolder holder) {
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
        data.setCurrentEffect(mixData.get(data.player));
        MessageUtils.sendParsed(data.player, Translations.MIX_CREATED);
        GUISounds.play(data.player, GUISounds.CONFIRM);
        return Collections.singletonList(ResponseAction.close());
    }
}
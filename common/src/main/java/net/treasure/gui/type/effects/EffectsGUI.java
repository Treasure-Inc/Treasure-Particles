package net.treasure.gui.type.effects;

import net.treasure.TreasureParticles;
import net.treasure.color.ColorManager;
import net.treasure.color.data.RGBColorData;
import net.treasure.color.generator.Gradient;
import net.treasure.effect.Effect;
import net.treasure.effect.EffectManager;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.gui.GUIManager;
import net.treasure.gui.config.ElementType;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUIElements.ElementInfo;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.task.GUITask;
import net.treasure.gui.type.color.ColorsGUI;
import net.treasure.gui.type.mixer.MixerGUI;
import net.treasure.locale.Translations;
import net.treasure.permission.Permissions;
import net.treasure.player.PlayerManager;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
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
import java.util.List;
import java.util.Random;

import static net.treasure.gui.type.GUI.EFFECTS;

public class EffectsGUI {

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
    public static ElementInfo RANDOM_EFFECT;
    public static ElementInfo RESET;
    public static ElementInfo CLOSE;
    public static ElementInfo FILTER;
    public static ElementInfo MIXER;

    public static void configure(GUIManager manager) {
        EffectsGUI.manager = manager;
        effectManager = TreasureParticles.getEffectManager();
        playerManager = TreasureParticles.getPlayerManager();
        colorManager = TreasureParticles.getColorManager();
        translations = TreasureParticles.getTranslations();
    }

    public static void setItems() {
        BORDERS = GUIElements.element(EFFECTS, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        NEXT_PAGE = GUIElements.element(EFFECTS, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.element(EFFECTS, ElementType.PREVIOUS_PAGE, 'N', new ItemStack(Material.ENDER_EYE));

        DEFAULT_ICON = GUIElements.element(EFFECTS, ElementType.DEFAULT_ICON, 'E', new ItemStack(Material.LEATHER_BOOTS));
        RANDOM_EFFECT = GUIElements.element(EFFECTS, ElementType.RANDOM_EFFECT, 'r', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));

        RESET = GUIElements.element(EFFECTS, ElementType.RESET, 'R', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        CLOSE = GUIElements.element(EFFECTS, ElementType.CLOSE, 'C', new ItemStack(Material.BARRIER));
        FILTER = GUIElements.element(EFFECTS, ElementType.FILTER, 'F', new ItemStack(Material.HOPPER));

        MIXER = GUIElements.element(EFFECTS, ElementType.MIXER, 'M', new ItemStack(Material.END_CRYSTAL));
    }

    public static void open(Player player, HandlerEvent filter, int page) {
        var effects = effectManager.getEffects().stream().filter(effect -> (filter == null || effect.getEvents().contains(filter)) && effect.canUse(player)).toList();
        var holder = new EffectsHolder();
        holder.setFilter(filter);
        holder.setPage(page);
        open(player, holder, effects);
    }

    public static void open(Player player, EffectsHolder holder, List<Effect> effects) {
        // Variables
        var data = playerManager.getEffectData(player);
        var layout = manager.getStyle().getLayouts().get(EFFECTS);
        var colorCycleSpeed = manager.getColorCycleSpeed();
        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        var page = holder.getPage();
        var filter = holder.getFilter();

        // Create inventory
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.EFFECTS_GUI_TITLE));
        holder.setInventory(inventory);

        // Borders
        if (BORDERS.isEnabled()) for (int slot : BORDERS.slots())
            holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Close button
        if (CLOSE.isEnabled()) for (int slot : CLOSE.slots())
            holder.setItem(slot, new CustomItem(CLOSE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_CLOSE)), event -> player.closeInventory());

        // Filter button
        if (FILTER.isEnabled()) for (int slot : FILTER.slots())
            holder.setItem(slot,
                    new CustomItem(FILTER.item())
                            .setDisplayName(MessageUtils.gui(Translations.BUTTON_FILTER))
                            .setLore(Arrays.stream(HandlerEvent.values()).map(event -> MessageUtils.gui("<dark_gray> • <" + (event.equals(filter) ? "green" : "gray") + ">" + translations.get("events." + event.translationKey()))).toList())
                            .addLore(
                                    MessageUtils.gui(Translations.FILTER_UP),
                                    MessageUtils.gui(Translations.FILTER_DOWN),
                                    MessageUtils.gui(Translations.FILTER_RESET)
                            ),
                    event -> {
                        if (event.getClick() == ClickType.MIDDLE) {
                            open(player, null, 0);
                            GUISounds.play(player, GUISounds.FILTER);
                            return;
                        }
                        var holderFilter = holder.getFilter();
                        var values = HandlerEvent.values();

                        var ordinal = holderFilter == null ? (event.isRightClick() ? values.length - 1 : 0) : holderFilter.ordinal() + (event.isRightClick() ? -1 : 1);
                        var newFilter = ordinal >= values.length || ordinal < 0 ? null : values[ordinal];

                        open(player, newFilter, 0);
                        GUISounds.play(player, GUISounds.FILTER);
                    });

        // Previous page button
        if (page > 0 && PREVIOUS_PAGE.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(PREVIOUS_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_PREVIOUS_PAGE)),
                        event -> {
                            holder.setPage(holder.getPage() - 1);
                            open(player, holder, effects);
                            GUISounds.play(player, GUISounds.PREVIOUS_PAGE);
                        });
        else if (PREVIOUS_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Reset effect button
        if (data.getCurrentEffect() != null && RESET.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot,
                        new CustomItem(RESET.item())
                                .setDisplayName(MessageUtils.gui(Translations.EFFECTS_GUI_RESET))
                                .addLore(MessageUtils.gui(Translations.EFFECTS_GUI_CURRENT, data.getCurrentEffect().getDisplayName())),
                        event -> {
                            data.setCurrentEffect(null);
                            open(player, holder, effects);
                            GUISounds.play(player, GUISounds.RESET);
                        });
        else if (RESET.isEnabled() && BORDERS.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Random effect button
        if (!effects.isEmpty() && RANDOM_EFFECT.isEnabled())
            for (int slot : RANDOM_EFFECT.slots())
                holder.setItem(slot,
                        new CustomItem(RANDOM_EFFECT.item()).setDisplayName(MessageUtils.gui(Translations.EFFECTS_GUI_RANDOM)),
                        event -> {
                            var effect = effects.get(new Random().nextInt(effects.size()));
                            data.setCurrentEffect(effect);
                            MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                            player.closeInventory();
                            GUISounds.play(player, GUISounds.RANDOM_EFFECT);
                        });
        else if (RANDOM_EFFECT.isEnabled() && BORDERS.isEnabled())
            for (int slot : RANDOM_EFFECT.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Mixer button
        var hasMix = !data.getMixData().isEmpty();
        var canCreateAnotherMix = data.canCreateAnotherMix();
        if (!holder.isPlayerMixGUI() && MIXER.isEnabled() && player.hasPermission(Permissions.MIXER))
            for (int slot : MIXER.slots())
                holder.setItem(slot,
                        new CustomItem(MIXER.item())
                                .setDisplayName(MessageUtils.gui(Translations.EFFECTS_GUI_MIXER))
                                .addLore(
                                        hasMix ? MessageUtils.gui(Translations.EFFECTS_GUI_OPEN_PLAYER_MIX_LIST) : null,
                                        canCreateAnotherMix ? MessageUtils.gui(Translations.EFFECTS_GUI_OPEN_MIXER) : null
                                ),
                        event -> {
                            if (event.isRightClick() && canCreateAnotherMix) {
                                MixerGUI.open(player);
                                return;
                            }
                            if (!hasMix) return;
                            List<Effect> mixEffects = new ArrayList<>();
                            var iterator = data.getMixData().iterator();
                            while (iterator.hasNext()) {
                                var mixData = iterator.next();
                                var mixEffect = mixData.get(player);
                                if (mixEffect == null) {
                                    iterator.remove();
                                    continue;
                                }
                                mixEffects.add(mixEffect);
                            }
                            if (mixEffects.isEmpty()) {
                                open(player, holder, effects);
                                return;
                            }
                            var newHolder = new EffectsHolder();
                            newHolder.setPlayerMixGUI(true);
                            open(player, newHolder, mixEffects);
                        });
        else if ((holder.isPlayerMixGUI() || MIXER.isEnabled()) && BORDERS.isEnabled())
            for (int slot : MIXER.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Next page button
        if ((page + 1) * maxEffects < effects.size() && NEXT_PAGE.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(NEXT_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_NEXT_PAGE)),
                        event -> {
                            holder.nextPage();
                            open(player, holder, effects);
                            GUISounds.play(player, GUISounds.NEXT_PAGE);
                        });
        else if (NEXT_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        // Effects
        int index = 0;
        for (int i = page * maxEffects; i < (page + 1) * maxEffects; i++) {
            if (effects.size() <= i) break;

            int where = effectSlots[index];

            var effect = effects.get(i);
            Color color = null;
            RGBColorData colorData = null;

            var currentEffect = data.getCurrentEffect() != null && (holder.isPlayerMixGUI() ? effect.getKey().equals(data.getCurrentEffect().getKey()) : effect.equals(data.getCurrentEffect()));

            var colorGroup = effect.getColorGroup();
            var canUseAny = colorGroup != null && colorGroup.canUseAny(player);

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
            } else if (canUseAny) {
                var preference = data.getColorPreference(effect);
                var scheme = preference == null ? colorGroup.getAvailableOptions().get(0).colorScheme() : preference;
                colorData = new RGBColorData(scheme, colorCycleSpeed, true, false);
                color = colorData.next(null);
            }

            holder.setItem(where, new CustomItem(effect.getIcon())
                    .setDisplayName(effect.getParsedDisplayName())
                    .addLore(effect.getDescription())
                    .addLore(effect.getDescription() != null ? ChatColor.AQUA.toString() : null)
                    .addLore(MessageUtils.gui(currentEffect ? Translations.EFFECTS_GUI_SELECTED : Translations.EFFECTS_GUI_SELECT))
                    .addLore(canUseAny ? MessageUtils.gui(Translations.COLOR_SELECTION_AVAILABLE) : null)
                    .addLore(holder.isPlayerMixGUI() ? MessageUtils.gui(Translations.EFFECTS_GUI_REMOVE_MIX) : null)
                    .changeArmorColor(color)
                    .glow(currentEffect)
                    .addItemFlags(ItemFlag.values()), event -> {
                if (event.getClick() == ClickType.MIDDLE && holder.isPlayerMixGUI()) {
                    data.getMixData().removeIf(mixData -> effect.getKey().equals(player.getName() + "/" + mixData.name()));
                    if (currentEffect)
                        data.setCurrentEffect(null);
                    player.closeInventory();
                    MessageUtils.sendParsed(player, Translations.EFFECTS_GUI_MIX_REMOVED);
                    return;
                }
                if (event.isRightClick() && canUseAny) {
                    ColorsGUI.open(player, effect, 0);
                    return;
                }
                data.setCurrentEffect(effect);
                MessageUtils.sendParsed(player, Translations.EFFECT_SELECTED, effect.getDisplayName());
                player.closeInventory();
                GUISounds.play(player, GUISounds.SELECT_EFFECT);
            }, colorData);
            index += 1;
        }

        player.openInventory(inventory);
        if (holder.hasAnimation()) GUITask.getPlayers().add(player);
    }
}
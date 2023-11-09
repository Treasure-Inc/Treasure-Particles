package net.treasure.particles.gui.type.effects;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.color.ColorManager;
import net.treasure.particles.color.data.RGBColorData;
import net.treasure.particles.color.generator.Gradient;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.gui.GUIManager;
import net.treasure.particles.gui.config.ElementType;
import net.treasure.particles.gui.config.GUIElements;
import net.treasure.particles.gui.config.GUIElements.ElementInfo;
import net.treasure.particles.gui.config.GUISounds;
import net.treasure.particles.gui.task.GUITask;
import net.treasure.particles.gui.type.GUI;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.permission.Permissions;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.util.item.CustomItem;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.treasure.particles.gui.type.GUIType.EFFECTS;

public class EffectsGUI extends GUI {

    // Managers
    private final PlayerManager playerManager;
    private final ColorManager colorManager;

    // GUI Elements
    public static ElementInfo DEFAULT_ICON;
    private ElementInfo RANDOM_EFFECT;
    private ElementInfo RESET;
    private ElementInfo MIXER;

    public EffectsGUI(GUIManager manager) {
        super(manager, EFFECTS);
        this.effectManager = TreasureParticles.getEffectManager();
        this.playerManager = TreasureParticles.getPlayerManager();
        this.colorManager = TreasureParticles.getColorManager();
    }

    @Override
    public void reload() {
        super.reload();
        DEFAULT_ICON = GUIElements.element(type, ElementType.DEFAULT_ICON, 'E', new ItemStack(Material.LEATHER_BOOTS));
        RANDOM_EFFECT = GUIElements.element(type, ElementType.RANDOM_EFFECT, 'r', new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
        RESET = GUIElements.element(type, ElementType.RESET, 'R', new ItemStack(Material.RED_STAINED_GLASS_PANE));
        MIXER = GUIElements.element(type, ElementType.MIXER, 'M', new ItemStack(Material.END_CRYSTAL));
    }

    @Override
    public void open(Player player) {
        var holder = new EffectsHolder();
        open(player, holder);
    }

    public void open(Player player, int page) {
        var holder = new EffectsHolder();
        holder.setPage(page);
        open(player, holder);
    }

    public void open(Player player, EffectsHolder holder) {
        var filter = holder.getFilter();
        var effects = effectManager.getEffects().stream().filter(effect -> (filter == null || effect.getEvents().contains(filter)) && effect.canUse(player)).toList();
        open(player, holder, effects);
    }

    public void open(Player player, EffectsHolder holder, List<Effect> effects) {
        // Variables
        var data = playerManager.getEffectData(player);
        var colorCycleSpeed = manager.getColorCycleSpeed();
        var effectSlots = DEFAULT_ICON.slots();
        var maxEffects = effectSlots.length;

        var page = holder.getPage();

        // Create inventory
        var inventory = Bukkit.createInventory(holder, layout.getSize(), MessageUtils.parseLegacy(Translations.EFFECTS_GUI_TITLE));
        holder.setInventory(inventory);

        holder.setAvailableFilters(effectManager.getEffects().stream().filter(effect -> effect.canUse(player)).flatMap(effect -> effect.getEvents().stream()).distinct().toList());

        super.commonItems(player, holder)
                .pageItems(player, holder, (page + 1) * maxEffects < effects.size(), () -> open(player, holder, effects))
                .filterItem(player, holder, () -> open(player, holder));

        //region Reset effect button
        if (data.getCurrentEffect() != null && RESET.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot,
                        new CustomItem(RESET.item())
                                .setDisplayName(MessageUtils.gui(Translations.EFFECTS_GUI_RESET))
                                .addLore(MessageUtils.gui(Translations.EFFECTS_GUI_CURRENT, data.getCurrentEffect().getDisplayName())),
                        event -> {
                            data.setCurrentEffect(null);
                            open(player);
                            GUISounds.play(player, GUISounds.RESET);
                        });
        else if (RESET.isEnabled() && BORDERS.isEnabled())
            for (int slot : RESET.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());
        //endregion

        //region Random effect button
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
        //endregion

        //region Mixer button
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
                                manager.mixerGUI().open(player);
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
        //endregion

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
                    manager.colorsGUI().open(player, effect, 0);
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
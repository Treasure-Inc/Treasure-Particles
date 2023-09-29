package net.treasure.gui.type;

import net.treasure.TreasureParticles;
import net.treasure.effect.EffectManager;
import net.treasure.gui.GUIHolder;
import net.treasure.gui.GUIManager;
import net.treasure.gui.config.ElementType;
import net.treasure.gui.config.GUIElements;
import net.treasure.gui.config.GUILayout;
import net.treasure.gui.config.GUISounds;
import net.treasure.gui.type.effects.EffectsHolder;
import net.treasure.locale.Translations;
import net.treasure.util.item.CustomItem;
import net.treasure.util.message.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.treasure.gui.config.GUIElements.ElementInfo;
import static net.treasure.gui.type.GUIType.EFFECTS;

public abstract class GUI {

    // GUI Info
    protected final GUIType type;
    protected GUILayout layout;

    // Managers
    protected GUIManager manager;
    protected EffectManager effectManager;
    protected Translations translations;

    // Elements
    protected ElementInfo BORDERS;
    protected ElementInfo NEXT_PAGE;
    protected ElementInfo PREVIOUS_PAGE;
    protected ElementInfo CLOSE;
    protected ElementInfo FILTER;

    protected GUI(GUIManager manager, GUIType type) {
        this.manager = manager;
        this.type = type;
        this.effectManager = TreasureParticles.getEffectManager();
        this.translations = TreasureParticles.getTranslations();
    }

    public void reload() {
        layout = manager.getStyle().getLayouts().get(type);

        BORDERS = GUIElements.element(type, ElementType.BORDERS, 'B', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        NEXT_PAGE = GUIElements.element(type, ElementType.NEXT_PAGE, 'N', new ItemStack(Material.ENDER_PEARL));
        PREVIOUS_PAGE = GUIElements.element(type, ElementType.PREVIOUS_PAGE, 'N', new ItemStack(Material.ENDER_EYE));
        CLOSE = GUIElements.element(EFFECTS, ElementType.CLOSE, 'C', new ItemStack(Material.BARRIER));
        FILTER = GUIElements.element(EFFECTS, ElementType.FILTER, 'F', new ItemStack(Material.HOPPER));
    }

    public abstract void open(Player player);

    public GUI commonItems(Player player, GUIHolder holder) {
        if (BORDERS.isEnabled()) for (int slot : BORDERS.slots())
            holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        if (CLOSE.isEnabled()) for (int slot : CLOSE.slots())
            holder.setItem(slot, new CustomItem(CLOSE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_CLOSE)), event -> player.closeInventory());
        return this;
    }

    public GUI pageItems(Player player, GUIHolder holder, boolean nextPageAvailable, Runnable guiOpener) {
        var page = holder.getPage();
        if (page > 0 && PREVIOUS_PAGE.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(PREVIOUS_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_PREVIOUS_PAGE)),
                        event -> {
                            holder.previousPage();
                            guiOpener.run();
                            GUISounds.play(player, GUISounds.PREVIOUS_PAGE);
                        });
        else if (PREVIOUS_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : PREVIOUS_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        if (nextPageAvailable && NEXT_PAGE.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot,
                        new CustomItem(NEXT_PAGE.item()).setDisplayName(MessageUtils.gui(Translations.BUTTON_NEXT_PAGE)),
                        event -> {
                            holder.nextPage();
                            guiOpener.run();
                            GUISounds.play(player, GUISounds.NEXT_PAGE);
                        });
        else if (NEXT_PAGE.isEnabled() && BORDERS.isEnabled())
            for (int slot : NEXT_PAGE.slots())
                holder.setItem(slot, new CustomItem(BORDERS.item()).emptyName());

        return this;
    }

    public GUI filterItem(Player player, EffectsHolder holder, Runnable guiOpener) {
        var filter = holder.getFilter();

        var values = holder.getAvailableFilters();
        var empty = values.isEmpty();

        if (FILTER.isEnabled()) for (int slot : FILTER.slots())
            holder.setItem(slot,
                    new CustomItem(FILTER.item())
                            .setDisplayName(MessageUtils.gui(Translations.BUTTON_FILTER))
                            .setLore(values.stream()
                                    .map(event -> MessageUtils.gui("<dark_gray> • <" + (event.equals(filter) ? "green" : "gray") + ">" + translations.get("events." + event.translationKey())))
                                    .toList()
                            )
                            .addLore(empty ? null :
                                    List.of(MessageUtils.gui(Translations.FILTER_UP),
                                            MessageUtils.gui(Translations.FILTER_DOWN),
                                            MessageUtils.gui(Translations.FILTER_RESET))
                            ),
                    event -> {
                        if (event.getClick() == ClickType.MIDDLE) {
                            holder.setPage(0);
                            holder.setFilter(null);
                            guiOpener.run();
                            GUISounds.play(player, GUISounds.FILTER);
                            return;
                        }

                        var numKey = event.getHotbarButton();
                        if (event.getClick() == ClickType.NUMBER_KEY) {
                            if (numKey >= values.size()) return;
                            holder.setPage(0);
                            holder.setFilter(values.get(numKey));
                            guiOpener.run();
                            GUISounds.play(player, GUISounds.FILTER);
                            return;
                        }

                        var holderFilter = holder.getFilter();
                        var ordinal = holderFilter == null ? 0 : values.indexOf(holderFilter);

                        var newOrdinal = holderFilter == null ? (event.isRightClick() ? values.size() - 1 : 0) : ordinal + (event.isRightClick() ? -1 : 1);
                        var newFilter = newOrdinal >= values.size() || newOrdinal < 0 ? null : values.get(newOrdinal);
                        holder.setPage(0);
                        holder.setFilter(newFilter);

                        guiOpener.run();
                        GUISounds.play(player, GUISounds.FILTER);
                    });
        return this;
    }
}
package net.treasure.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomItem {

    private final ItemStack i;

    public CustomItem(Material material) {
        this.i = new ItemStack(material);
    }

    public CustomItem(Material material, int amount) {
        if (amount == 0) {
            this.i = new ItemStack(material, 1);
        } else {
            this.i = new ItemStack(material, amount);
        }
    }

    public CustomItem(ItemStack itemStack) {
        this.i = itemStack.clone();
    }

    public CustomItem addItemFlags(ItemFlag... flag) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.addItemFlags(flag);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addLore(String... args) {
        if (args == null || (args.length == 1 && args[0] == null))
            return this;
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        if (meta.getLore() != null) {
            List<String> old = meta.getLore();
            old.addAll(Arrays.stream(args).filter(Objects::nonNull).collect(Collectors.toList()));
            meta.setLore(old);
        } else
            meta.setLore(Arrays.stream(args).filter(Objects::nonNull).collect(Collectors.toList()));
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addLore(List<String> list) {
        if (list == null)
            return this;
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        if (meta.getLore() != null) {
            List<String> old = meta.getLore();
            old.addAll(list);
            meta.setLore(old);
        } else
            meta.setLore(list);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setLore(String... args) {
        if (args == null || (args.length == 1 && args[0] == null))
            return this;
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.setLore(Arrays.stream(args).filter(Objects::nonNull).collect(Collectors.toList()));
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setLore(List<String> list) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.setLore(list.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addData(NamespacedKey key, String value) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem glow() {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem glow(boolean glow) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        if (!glow) {
            meta.removeEnchant(Enchantment.LUCK);
        } else {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem changeArmorColor(Color color) {
        if (color == null) return this;
        if (this.i.getItemMeta() != null && this.i.getItemMeta() instanceof LeatherArmorMeta lam) {
            lam.setColor(color);
            this.i.setItemMeta(lam);
        }
        return this;
    }

    public CustomItem emptyName() {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.setDisplayName(ChatColor.WHITE.toString());
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setDisplayName(String string) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.setDisplayName(string);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setAmount(int amount) {
        this.i.setAmount(amount);
        return this;
    }

    public CustomItem setCustomModelData(int customModelData) {
        var meta = this.i.getItemMeta();
        if (meta == null) return this;
        meta.setCustomModelData(customModelData);
        this.i.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.i;
    }
}
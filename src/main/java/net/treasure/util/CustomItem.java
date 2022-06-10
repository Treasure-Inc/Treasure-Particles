package net.treasure.util;

import net.treasure.core.TreasurePlugin;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomItem {
    private ItemStack i;

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

    public CustomItem addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = this.i.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addItemFlags(ItemFlag... flag) {
        ItemMeta meta = this.i.getItemMeta();
        meta.addItemFlags(flag);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addLore(String... args) {
        if (args == null || (args.length == 1 && args[0] == null))
            return this;
        ItemMeta meta = this.i.getItemMeta();
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
        ItemMeta meta = this.i.getItemMeta();
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
        ItemMeta meta = this.i.getItemMeta();
        meta.setLore(Arrays.stream(args).filter(Objects::nonNull).collect(Collectors.toList()));
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setLore(List<String> list) {
        ItemMeta meta = this.i.getItemMeta();
        meta.setLore(list.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem addData(String key, String value) {
        ItemMeta meta = this.i.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(TreasurePlugin.getInstance(), key), PersistentDataType.STRING, value);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem glow() {
        ItemMeta meta = this.i.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem glow(boolean glow) {
        ItemMeta meta = this.i.getItemMeta();
        if (!glow) {
            meta.removeEnchant(Enchantment.LUCK);
        } else {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        this.i.setItemMeta(meta);
        return this;
    }

    public boolean isGlowing() {
        return this.i.getItemMeta().hasEnchants();
    }

    public CustomItem changeArmorColor(Color color) {
        if (color == null)
            return this;
        if (this.i.getItemMeta() != null && this.i.getItemMeta() instanceof LeatherArmorMeta lam) {
            lam.setColor(color);
            this.i.setItemMeta(lam);
        }
        return this;
    }

    public CustomItem setDisplayName(String string) {
        ItemMeta meta = this.i.getItemMeta();
        meta.setDisplayName(string);
        this.i.setItemMeta(meta);
        return this;
    }

    public CustomItem setAmount(int amount) {
        this.i.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        return this.i;
    }

    public CustomItem clone() {
        return new CustomItem(this.i);
    }

    public CustomItem setItemStack(ItemStack itemStack) {
        this.i = itemStack;
        return this;
    }
}
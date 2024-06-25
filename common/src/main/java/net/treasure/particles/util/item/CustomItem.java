package net.treasure.particles.util.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

    public CustomItem(ItemStack itemStack, boolean clone) {
        if (clone)
            this.i = itemStack.clone();
        else
            this.i = itemStack;
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
            old.addAll(Arrays.stream(args).filter(Objects::nonNull).toList());
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

    public CustomItem changeColor(Color color) {
        if (color == null) return this;
        if (this.i.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(color);
            this.i.setItemMeta(meta);
        } else if (this.i.getItemMeta() instanceof PotionMeta meta) {
            meta.setColor(color);
            this.i.setItemMeta(meta);
        } else if (this.i.getItemMeta() instanceof MapMeta meta) {
            meta.setColor(color);
            this.i.setItemMeta(meta);
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

    public CustomItem setPlayerHeadName(String name) {
        if (name == null) return this;
        var meta = this.i.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) return this;
        try {
            skullMeta.setOwner(name);
        } catch (Exception ignored) {
        }
        this.i.setItemMeta(skullMeta);
        return this;
    }

    public CustomItem setPlayerHeadTexture(String texture) {
        if (texture == null) return this;
        var meta = this.i.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) return this;
        GameProfile profile = new GameProfile(UUID.randomUUID(), "PLAYER");
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            var setProfile = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);
            setProfile.invoke(skullMeta, profile);
        } catch (RuntimeException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException ignored) {
        }
        this.i.setItemMeta(skullMeta);
        return this;
    }


    public ItemStack build() {
        return this.i;
    }
}
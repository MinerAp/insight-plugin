package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WandUtil {

    private static final ItemStack WAND;

    static {
        WAND = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = WAND.getItemMeta();
        meta.setDisplayName("Insight Wand");
        meta.setLore(ImmutableList.of("Authorized users only!"));
        WAND.setItemMeta(meta);
    }

    public static ItemStack getWand() {
        return new ItemStack(WAND);
    }

    public static boolean isWand(ItemStack other) {
        return WAND.isSimilar(other);
    }
}

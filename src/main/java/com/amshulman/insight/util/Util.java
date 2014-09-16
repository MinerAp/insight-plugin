package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static final BlockFace[] CARDINAL_DIRECTIONS = new BlockFace[] { BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH };
    public static final BlockFace[] ALL_FLOW_DIRECTIONS = ObjectArrays.concat(CARDINAL_DIRECTIONS, BlockFace.DOWN);

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

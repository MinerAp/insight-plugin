package com.amshulman.insight.event.block;

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.serialization.MetadataEntry;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;

public class BlockBreakListener extends InternalEventHandler<BlockBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockBreakEvent event) {
        BlockState block = event.getBlock().getState();
        String name = event.getPlayer().getName();
        long time = System.currentTimeMillis();

        MetadataEntry meta = null;
        switch (block.getType()) {
            case SKULL:
                meta = new SkullMeta((Skull) block);
                break;
            case CHEST:
            case TRAPPED_CHEST:
                Chest chest = (Chest) block;
                Location location = chest.getLocation();
                for (ItemStack item : chest.getBlockInventory()) {
                    if (item != null) {
                        add(new ItemRowEntry(time, name, EventCompat.ITEM_REMOVE, location, item));
                    }
                }
                ++time;
                break;
            default:
                break;
        }

        add(new BlockRowEntry(time, name, EventCompat.BLOCK_BREAK, block, meta));

        if (isDoubleBlock(block.getType())) {
            Block other = getSiblingBlock(event.getBlock());
            add(new BlockRowEntry(time, name, EventCompat.BLOCK_BREAK, other));
        }
    }

    private static boolean isDoubleBlock(Material mat) {
        return EnumSet.of(Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.DOUBLE_PLANT, Material.BED_BLOCK).contains(mat);
    }

    private static Block getSiblingBlock(Block block) {
        switch (block.getType()) {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case DOUBLE_PLANT:
                if (block.getData() < 8) {
                    return block.getRelative(BlockFace.UP);
                } else {
                    return block.getRelative(BlockFace.DOWN);
                }

            case BED_BLOCK:
                Bed b = (Bed) block.getState().getData();
                if (b.isHeadOfBed()) {
                    return block.getRelative(b.getFacing().getOppositeFace());
                } else {
                    return block.getRelative(b.getFacing());
                }

            default:
                return null;
        }
    }
}

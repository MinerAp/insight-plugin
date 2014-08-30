package com.amshulman.insight.event.block;

import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Bed;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.serialization.MetadataEntry;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InsightConfigurationContext;

public class BlockPlaceListener extends InternalEventHandler<BlockPlaceEvent> {

    private final boolean loggingFarmland;

    public BlockPlaceListener(InsightConfigurationContext configurationContext) {
        loggingFarmland = configurationContext.isLoggingFarmland();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockPlaceEvent event) {
        if (Material.FIRE.equals(event.getBlock().getType())) {
            return;
        }

        if (Material.SOIL.equals(event.getBlock().getType())) {
            if (loggingFarmland) {
                add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.SOIL_TILL, event.getBlockReplacedState()));
            }
            return;
        }

        MetadataEntry meta = null;
        if (Material.SKULL.equals(event.getBlock().getType())) {
            meta = new SkullMeta((Skull) event.getBlock().getState(), event.getPlayer().getItemInHand().getItemMeta());
        }

        add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, event.getBlock(), meta));

        if (isDoubleBlock(event.getBlock().getType())) {
            Block other = getOtherBlock(event.getBlock());
            add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, other));
        }
    }

    private static boolean isDoubleBlock(Material mat) {
        return EnumSet.of(Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.DOUBLE_PLANT, Material.BED_BLOCK).contains(mat);
    }

    private static Block getOtherBlock(Block block) {
        switch (block.getType()) {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case DOUBLE_PLANT:
                return block.getRelative(BlockFace.UP);

            case BED_BLOCK:
                Bed b = (Bed) block.getState().getData();
                return block.getRelative(b.getFacing());

            default:
                return null;
        }
    }
}

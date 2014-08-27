package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;
import com.google.common.collect.ObjectArrays;

public class BlockFromToListener extends InternalEventHandler<BlockFromToEvent> {

    private static final BlockFace[] CARDINAL_DIRECTIONS = new BlockFace[] { BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH };
    private static final BlockFace[] ALL_FLOW_DIRECTIONS = ObjectArrays.concat(CARDINAL_DIRECTIONS, BlockFace.DOWN);

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(final BlockFromToEvent event) {
        long when = System.currentTimeMillis();
        switch (event.getBlock().getType()) {
            case WATER:
            case STATIONARY_WATER:
                add(new BlockRowEntry(when, NonPlayerLookup.WATER, EventCompat.BLOCK_FLOW, event.getToBlock()));

                for (BlockFace face : ALL_FLOW_DIRECTIONS) {
                    Block b = event.getToBlock().getRelative(face);
                    if (isLava(b.getType())) {
                        if (isSourceBlock(b)) {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.OBSIDIAN)));
                        } else {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.COBBLESTONE)));
                        }
                    }
                }
                break;
            case LAVA:
            case STATIONARY_LAVA:
                if (event.getFace() == BlockFace.DOWN && isWater(event.getToBlock().getType())) {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.STONE)));
                } else if (event.getFace() != BlockFace.DOWN && hasAdjacentWater(event.getToBlock())) {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.COBBLESTONE)));
                } else {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FLOW, event.getToBlock()));
                }
                break;
            case DRAGON_EGG:
                add(new BlockRowEntry(when, NonPlayerLookup.NATURE, EventCompat.BLOCK_TELEPORT, getState(event.getToBlock(), Material.DRAGON_EGG)));
                break;
            default:
                System.out.println("BlockFromToListener - ??? " + event.getBlock().getType() + " " + event.getToBlock().getType());
                break;
        }
    }

    private static boolean hasAdjacentWater(Block block) {
        for (BlockFace face : CARDINAL_DIRECTIONS) {
            if (isWater(block.getRelative(face).getType())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLava(Material type) {
        return type == Material.LAVA || type == Material.STATIONARY_LAVA;
    }

    private static boolean isWater(Material type) {
        return type == Material.WATER || type == Material.STATIONARY_WATER;
    }

    private static boolean isSourceBlock(Block block) {
        return block.getData() == 0;
    }

    private final BlockState getState(Block block, Material newMat) {
        BlockState state = block.getState();
        state.setType(newMat);
        state.setRawData((byte) 0);
        return state;
    }
}

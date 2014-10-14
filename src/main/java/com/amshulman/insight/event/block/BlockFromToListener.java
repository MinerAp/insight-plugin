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
import com.amshulman.insight.util.Util;

public class BlockFromToListener extends InternalEventHandler<BlockFromToEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(final BlockFromToEvent event) {
        long when = System.currentTimeMillis();
        BlockState previousState;

        switch (event.getBlock().getType()) {
            case WATER:
            case STATIONARY_WATER:
                previousState = getState(event.getToBlock(), Material.WATER, (byte) (event.getBlock().getData() + 1));
                add(new BlockRowEntry(when, NonPlayerLookup.WATER, EventCompat.BLOCK_FLOW, event.getToBlock(), previousState));

                for (BlockFace face : Util.ALL_FLOW_DIRECTIONS) {
                    Block b = event.getToBlock().getRelative(face);
                    if (isLava(b.getType())) {
                        previousState = Util.getBlockStateOrNullIfAir(b.getState());
                        if (isSourceBlock(b)) {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.OBSIDIAN), previousState));
                        } else {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.COBBLESTONE), previousState));
                        }
                    }
                }
                break;
            case LAVA:
            case STATIONARY_LAVA:
                if (event.getFace() == BlockFace.DOWN) {
                    if (isWater(event.getToBlock().getType())) {
                        previousState = Util.getBlockStateOrNullIfAir(event.getToBlock().getState());
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.STONE), previousState));
                    } else if (event.getToBlock().getType() == Material.REDSTONE_WIRE && hasAdjacentWater(event.getToBlock())) {
                        previousState = event.getToBlock().getState();
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.OBSIDIAN), previousState));
                    } else if (event.getToBlock().getType() == Material.TRIPWIRE && event.getToBlock().getData() == 0 && hasAdjacentWater(event.getToBlock()) &&
                            ((event.getToBlock().getRelative(BlockFace.SOUTH).getType() == Material.TRIPWIRE_HOOK && event.getToBlock().getRelative(BlockFace.SOUTH).getData() == 2)
                             || (event.getToBlock().getRelative(BlockFace.WEST).getType() == Material.TRIPWIRE_HOOK && event.getToBlock().getRelative(BlockFace.WEST).getData() == 3))) {
                        System.out.println(BlockFace.SELF + ": " + event.getToBlock().getData());
                        for (BlockFace face : Util.CARDINAL_DIRECTIONS) {
                            Block b = event.getToBlock().getRelative(face);
                            if (b.getType() == Material.TRIPWIRE_HOOK) {
                                System.out.println(face + ": " + b.getData());
                            }
                        }
                        previousState = event.getToBlock().getState();
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.OBSIDIAN), previousState));
                    } else {
                        previousState = getState(event.getToBlock(), Material.LAVA, (byte) 8);
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FLOW, event.getToBlock(), previousState));
                    }
                } else {
                    if (hasAdjacentWater(event.getToBlock())) {
                        previousState = Util.getBlockStateOrNullIfAir(event.getToBlock().getState());
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.COBBLESTONE), previousState));
                    } else {
                        previousState = getState(event.getToBlock(), Material.LAVA, (byte) (event.getBlock().getData() + 2));
                        add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FLOW, event.getToBlock(), previousState));
                    }
                }
                break;
            case DRAGON_EGG:
                add(new BlockRowEntry(when, NonPlayerLookup.NATURE, EventCompat.BLOCK_TELEPORT, getState(event.getToBlock(), Material.DRAGON_EGG)));
                break;
            case AIR:
                break;
            default:
                System.out.println("BlockFromToListener - ??? " + event.getBlock().getType() + " " + event.getToBlock().getType());
                break;
        }
    }

    private static boolean hasAdjacentWater(Block block) {
        for (BlockFace face : Util.CARDINAL_DIRECTIONS) {
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
        return getState(block, newMat, (byte) 0);
    }

    private final BlockState getState(Block block, Material newMat, byte newData) {
        BlockState state = block.getState();
        state.setType(newMat);
        state.setRawData(newData);
        return state;
    }
}

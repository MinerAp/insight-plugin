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
//        System.out.println("--------------------------");
//        System.out.println("from: " + event.getBlock().getType());
//        System.out.println("to: " + event.getToBlock().getType());
//        System.out.println("face: " + event.getFace());
        long when = System.currentTimeMillis();
        switch (event.getBlock().getType()) {
            case WATER:
            case STATIONARY_WATER:
                add(new BlockRowEntry(when, NonPlayerLookup.WATER, EventCompat.BLOCK_FLOW, event.getToBlock()));
                System.out.println("BlockFromToListener - water: flow");

                for (BlockFace face : ALL_FLOW_DIRECTIONS) {
                    Block b = event.getToBlock().getRelative(face);
                    if (isLava(b.getType())) {
                        if (isSourceBlock(b)) {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.OBSIDIAN)));
                            System.out.println("BlockFromToListener - water: lava -> obsidian");
                        } else {
                            add(new BlockRowEntry(++when, NonPlayerLookup.WATER, EventCompat.BLOCK_FORM, getState(b, Material.COBBLESTONE)));
                            System.out.println("BlockFromToListener - water: lava -> cobblestone");
                        }
                    }
                }
                break;
            case LAVA:
            case STATIONARY_LAVA:
                if (event.getFace() == BlockFace.DOWN && isWater(event.getToBlock().getType())) {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.STONE)));
                    System.out.println("BlockFromToListener - lava: water -> stone");
                } else if (event.getFace() != BlockFace.DOWN && hasAdjacentWater(event.getToBlock())) {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FORM, getState(event.getToBlock(), Material.COBBLESTONE)));
                    System.out.println("BlockFromToListener - lava: lava -> cobblestone");
                } else {
                    add(new BlockRowEntry(when, NonPlayerLookup.LAVA, EventCompat.BLOCK_FLOW, event.getToBlock()));
                    System.out.println("BlockFromToListener - lava flow");
                }
                break;
            case DRAGON_EGG:
                add(new BlockRowEntry(when, NonPlayerLookup.NATURE, EventCompat.BLOCK_TELEPORT, getState(event.getToBlock(), Material.DRAGON_EGG)));
                System.out.println("BlockFromToListener - teleport");
                break;
            default:
                System.out.println("BlockFromToListener - ???");
                break;
        }
    }

/*    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
            final Block to = event.getToBlock();
            final Material typeFrom = event.getBlock().getType();
            final Material typeTo = to.getType();
            final boolean canFlow = typeTo == Material.AIR || nonFluidProofBlocks.contains(typeTo);
            if (typeFrom == Material.LAVA || typeFrom == Material.STATIONARY_LAVA) {
                if (canFlow) {
                    if (isSurroundedByWater(to) && event.getBlock().getData() <= 2) {
                        // queueBlockReplace("LavaFlow", to.getState(), 4, (byte) 0);
                    } else if (typeTo == Material.AIR) {
                        // queueBlockPlace("LavaFlow", to.getLocation(), 10, (byte) (event.getBlock().getData() + 1));
                    } else {
                        // queueBlockReplace("LavaFlow", to.getState(), 10, (byte) (event.getBlock().getData() + 1));
                    }
                } else if (typeTo == 8 || typeTo == 9) {
                    if (event.getFace() == BlockFace.DOWN) {
                        // queueBlockReplace("LavaFlow", to.getState(), 1, (byte) 0);
                    } else {
                        // queueBlockReplace("LavaFlow", to.getState(), 4, (byte) 0);
                    }
                }
            } else if ((typeFrom == 8 || typeFrom == 9)) {
                if (typeTo == Material.AIR) {
                    // queueBlockPlace("WaterFlow", to.getLocation(), 8, (byte) (event.getBlock().getData() + 1));
                } else if (nonFluidProofBlocks.contains(typeTo)) {
                    // queueBlockReplace("WaterFlow", to.getState(), 8, (byte) (event.getBlock().getData() + 1));
                }
                else if (typeTo == Material.LAVA || typeTo == Material.STATIONARY_LAVA) {
                    if (to.getData() == 0) {
                        // queueBlockReplace("WaterFlow", to.getState(), 49, (byte) 0);
                    } else if (event.getFace() == BlockFace.DOWN) {
                        // queueBlockReplace("LavaFlow", to.getState(), 1, (byte) 0);
                    }
                }
                if (typeTo == Material.AIR || nonFluidProofBlocks.contains(typeTo)) {
                    for (final BlockFace face : new BlockFace[] { BlockFace.DOWN, BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH }) {
                        final Block lower = to.getRelative(face);
                        if (lower.getTypeId() == 10 || lower.getTypeId() == 11) {
                            // queueBlockReplace("WaterFlow", lower.getState(), lower.getData() == 0 ? 49 : 4, (byte) 0);
                        }
                    }
                }
            }
    }*/

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

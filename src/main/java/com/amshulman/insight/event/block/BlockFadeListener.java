package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFadeEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.NonPlayerLookup;
import com.amshulman.insight.util.Util;

public class BlockFadeListener extends InternalEventHandler<BlockFadeEvent> {

    private final boolean loggingFarmland;

    public BlockFadeListener(InsightConfigurationContext configurationContext) {
        loggingFarmland = configurationContext.isLoggingFarmland();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockFadeEvent event) {
        BlockState newState;

        switch (event.getBlock().getType()) {
            case FIRE:
                return; // Fire extinguishing is not covered
            case ICE:
            case SNOW:
                newState = Util.getBlockStateOrNullIfAir(event.getNewState());
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_MELT, event.getBlock(), newState));
                break;
            case GRASS:
            case MYCEL:
                newState = Util.getBlockStateOrNullIfAir(event.getNewState());
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_DIE, event.getBlock(), newState));
                break;
            case GLOWING_REDSTONE_ORE:
                break; // Not logged
            case SOIL:
                if (loggingFarmland) {
                    newState = Util.getBlockStateOrNullIfAir(event.getNewState());
                    add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.SOIL_REVERT, event.getBlock(), newState));
                }
                break;
            default:
                System.out.println("BlockFadeListener - ??? " + event.getBlock().getType());
                return;
        }
    }
}

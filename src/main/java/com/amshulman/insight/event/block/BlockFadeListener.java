package com.amshulman.insight.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFadeEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.NonPlayerLookup;

public class BlockFadeListener extends InternalEventHandler<BlockFadeEvent> {

    private final boolean loggingFarmland;

    public BlockFadeListener(InsightConfigurationContext configurationContext) {
        loggingFarmland = configurationContext.isLoggingFarmland();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockFadeEvent event) {

        switch (event.getBlock().getType()) {
            case FIRE:
                return; // Fire extinguishing is not covered
            case ICE:
            case SNOW:
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_MELT, event.getBlock()));
                break;
            case GRASS:
            case MYCEL:
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_DIE, event.getBlock()));
                break;
            case GLOWING_REDSTONE_ORE:
                break; // Not logged
            case SOIL:
                if (loggingFarmland) {
                    add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.SOIL_REVERT, event.getBlock()));
                }
                break;
            default:
                System.out.println("BlockFadeListener - ??? " + event.getBlock().getType());
                return;
        }
    }
}

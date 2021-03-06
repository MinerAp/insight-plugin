package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;
import com.amshulman.insight.util.Util;

public class BlockSpreadListener extends InternalEventHandler<BlockSpreadEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockSpreadEvent event) {
        switch (event.getSource().getType()) {
            case CHORUS_FLOWER:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case GRASS:
            case MYCEL:
            case VINE:
                BlockState previousState = Util.getBlockStateOrNullIfAir(event.getBlock().getState());
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_GROW, event.getNewState(), previousState));
                break;
            case FIRE:
                break; // handled by BlockIgniteListener
            default:
                System.out.println("BlockSpreadListener -- ??? " + event.getSource().getType());
                break;
        }
    }
}

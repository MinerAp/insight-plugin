package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;

public class BlockSpreadListener extends InternalEventHandler<BlockSpreadEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockSpreadEvent event) {
        switch (event.getSource().getType()) {
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case GRASS:
            case MYCEL:
            case VINE:
                BlockState state = event.getBlock().getState();
                state.setType(event.getSource().getType());
                state.setRawData(event.getSource().getData());

                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_GROW, state));
                System.out.println("BlockSpreadListener -- organics");
                break;
            case FIRE:
                break; // handled by BlockIgniteListener
            default:
                System.out.println("BlockSpreadListener -- ???");
                System.out.println(event.getSource().getType());
                break;
        }
    }
}

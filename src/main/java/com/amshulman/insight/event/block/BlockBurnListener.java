package com.amshulman.insight.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;

public class BlockBurnListener extends InternalEventHandler<BlockBurnEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockBurnEvent event) {
        add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.FIRE, EventCompat.BLOCK_BURN, event.getBlock()));
    }
}

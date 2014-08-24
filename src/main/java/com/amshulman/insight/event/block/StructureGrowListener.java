package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.StructureGrowEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;

public class StructureGrowListener extends InternalEventHandler<StructureGrowEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(StructureGrowEvent event) {

        String name = event.getPlayer() == null ? NonPlayerLookup.NATURE : event.getPlayer().getName();
        long time = System.currentTimeMillis();

        for (BlockState b : event.getBlocks()) {
            add(new BlockRowEntry(time, name, EventCompat.BLOCK_GROW, b));
        }
    }
}

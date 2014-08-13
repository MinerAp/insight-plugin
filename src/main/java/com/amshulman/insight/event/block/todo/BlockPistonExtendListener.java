package com.amshulman.insight.event.block.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class BlockPistonExtendListener extends InternalEventHandler<BlockPistonExtendEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockPistonExtendEvent event) {
        // add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.PISTON, BlockActionImpl.PISTON_EXTEND, event.getBlock()));
        System.out.println("BlockPistonExtendListener");
    }
}

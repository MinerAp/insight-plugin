package com.amshulman.insight.event.block.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class BlockPistonRetractListener extends InternalEventHandler<BlockPistonRetractEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockPistonRetractEvent event) {
        // add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.PISTON, BlockActionImpl.PISTON_RETRACT, event.getBlock()));
        System.out.println("BlockPistonRetractListener");
    }
}

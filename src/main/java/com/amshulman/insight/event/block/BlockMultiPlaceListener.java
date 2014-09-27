package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockMultiPlaceEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.Util;

public class BlockMultiPlaceListener extends InternalEventHandler<BlockMultiPlaceEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockMultiPlaceEvent event) {
        for (BlockState state : event.getReplacedBlockStates()) {
            BlockState previousBlock = Util.getBlockStateOrNullIfAir(state);
            BlockState newBlock = state.getBlock().getState();

            add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, newBlock, previousBlock));
        }
    }
}

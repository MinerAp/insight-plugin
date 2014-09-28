package com.amshulman.insight.event.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.StructureGrowEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;
import com.amshulman.insight.util.Util;

public class StructureGrowListener extends InternalEventHandler<StructureGrowEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(StructureGrowEvent event) {

        String name = event.getPlayer() == null ? NonPlayerLookup.NATURE : event.getPlayer().getName();
        long time = System.currentTimeMillis();
        Location base = event.getLocation().subtract(0, 1, 0);

        for (BlockState b : event.getBlocks()) {
            BlockState previousState = Util.getBlockStateOrNullIfAir(b.getBlock().getState());

            if (b.getLocation().equals(base)) {
                if (previousState.getType() != Material.DIRT || previousState.getRawData() != 0) {
                    add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_DIE, previousState, b));
                }

                continue;
            }

            add(new BlockRowEntry(time, name, EventCompat.BLOCK_GROW, b, previousState));
        }
    }
}

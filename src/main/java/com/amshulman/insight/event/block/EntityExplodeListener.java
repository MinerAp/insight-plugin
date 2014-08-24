package com.amshulman.insight.event.block;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class EntityExplodeListener extends InternalEventHandler<EntityExplodeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityExplodeEvent event) {
        String name = EntityUtil.getName(event.getEntity());
        long time = System.currentTimeMillis();

        for (Block b : event.blockList()) {
            add(new BlockRowEntry(time, name, EventCompat.BLOCK_EXPLODE, b));
        }
    }
}

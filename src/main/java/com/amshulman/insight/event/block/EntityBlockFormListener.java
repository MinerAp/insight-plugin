package com.amshulman.insight.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class EntityBlockFormListener extends InternalEventHandler<EntityBlockFormEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityBlockFormEvent event) {
        add(new BlockRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_FORM, event.getBlock()));
    }
}

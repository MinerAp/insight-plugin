package com.amshulman.insight.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;

public class EntityBlockFormListener extends InternalEventHandler<EntityBlockFormEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityBlockFormEvent event) {
        add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.BLOCK_FORM, event.getBlock()));
        System.out.println("EntityBlockFormListener");
    }
}

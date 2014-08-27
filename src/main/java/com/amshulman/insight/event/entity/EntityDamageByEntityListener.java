package com.amshulman.insight.event.entity;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class EntityDamageByEntityListener extends InternalEventHandler<EntityDamageByEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            add(new ItemRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getDamager()), EventCompat.ITEM_REMOVE, itemFrame.getLocation(), itemFrame.getItem()));
        }
    }
}

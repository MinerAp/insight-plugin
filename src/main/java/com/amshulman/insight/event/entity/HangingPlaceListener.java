package com.amshulman.insight.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class HangingPlaceListener extends InternalEventHandler<HangingPlaceEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingPlaceEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.HANGING_PLACE, event.getEntity().getLocation(), EntityUtil.getName(event.getEntity())));
    }
}

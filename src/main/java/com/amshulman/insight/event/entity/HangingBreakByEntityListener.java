package com.amshulman.insight.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class HangingBreakByEntityListener extends InternalEventHandler<HangingBreakByEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingBreakByEntityEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getRemover()), EventCompat.HANGING_BREAK, event.getEntity().getLocation(), EntityUtil.getName(event.getEntity())));
    }
}

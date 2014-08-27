package com.amshulman.insight.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.NonPlayerLookup;

public class HangingBreakListener extends InternalEventHandler<HangingBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingBreakEvent event) {
        if (RemoveCause.ENTITY.equals(event.getCause())) {
            return;
        }

        add(new EntityRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.HANGING_BREAK, event.getEntity().getLocation(), EntityUtil.getName(event.getEntity())));
    }
}

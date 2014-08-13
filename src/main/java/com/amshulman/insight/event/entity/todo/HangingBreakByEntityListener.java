package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class HangingBreakByEntityListener extends InternalEventHandler<HangingBreakByEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingBreakByEntityEvent event) {
        // TODO Auto-generated method stub
        System.out.println("HangingBreakByEntityListener");
    }
}

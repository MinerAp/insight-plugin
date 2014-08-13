package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class HangingPlaceListener extends InternalEventHandler<HangingPlaceEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingPlaceEvent event) {
        // TODO Auto-generated method stub
        System.out.println("HangingPlaceListener");
    }
}

package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.amshulman.insight.event.InternalEventHandler;

public class HangingBreakListener extends InternalEventHandler<HangingBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(HangingBreakEvent event) {
        if (RemoveCause.ENTITY.equals(event.getCause())) {
            return;
        }

        System.out.println("HangingBreakListener");
    }
}

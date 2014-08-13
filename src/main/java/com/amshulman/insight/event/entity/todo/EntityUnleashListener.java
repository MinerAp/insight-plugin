package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;

import com.amshulman.insight.event.InternalEventHandler;

public class EntityUnleashListener extends InternalEventHandler<EntityUnleashEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityUnleashEvent event) {
        if (UnleashReason.PLAYER_UNLEASH.equals(event.getReason())) {
            return;
        }

        System.out.println("EntityUnleashListener");
    }
}

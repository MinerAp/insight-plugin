package com.amshulman.insight.event.tbd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class CreatureSpawnListener extends InternalEventHandler<CreatureSpawnEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(CreatureSpawnEvent event) {
        
        // System.out.println("CreatureSpawnListener");
    }
}

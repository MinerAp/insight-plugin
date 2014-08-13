package com.amshulman.insight.event.entity.todo;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class EntityTargetListener extends InternalEventHandler<EntityTargetEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityTargetEvent event) {
        if (!EntityType.CREEPER.equals(event.getEntityType())) {
            return;
        }

        // TODO Auto-generated method stub
        System.out.println("EntityTargetListener");
    }
}

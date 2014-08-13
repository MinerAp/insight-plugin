package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerLeashEntityListener extends InternalEventHandler<PlayerLeashEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerLeashEntityEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerLeashEntityListener");
    }
}

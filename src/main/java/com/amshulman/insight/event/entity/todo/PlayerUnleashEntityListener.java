package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerUnleashEntityListener extends InternalEventHandler<PlayerUnleashEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerUnleashEntityEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerUnleashEntityListener");
    }
}

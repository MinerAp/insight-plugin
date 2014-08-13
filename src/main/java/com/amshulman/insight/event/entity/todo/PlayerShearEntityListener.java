package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerShearEntityListener extends InternalEventHandler<PlayerShearEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerShearEntityEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerShearEntityListener");
    }
}

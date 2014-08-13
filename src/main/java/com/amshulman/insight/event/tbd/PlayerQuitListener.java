package com.amshulman.insight.event.tbd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerQuitListener extends InternalEventHandler<PlayerQuitEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerQuitEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerQuitListener");
    }
}

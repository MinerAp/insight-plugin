package com.amshulman.insight.event.tbd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerJoinListener extends InternalEventHandler<PlayerJoinEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerJoinEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerJoinListener");
    }
}

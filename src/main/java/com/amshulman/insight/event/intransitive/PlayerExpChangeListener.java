package com.amshulman.insight.event.intransitive;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerExpChangeListener extends InternalEventHandler<PlayerExpChangeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerExpChangeEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PlayerExpChangeListener");
    }
}

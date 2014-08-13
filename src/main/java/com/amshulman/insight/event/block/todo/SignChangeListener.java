package com.amshulman.insight.event.block.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class SignChangeListener extends InternalEventHandler<SignChangeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(SignChangeEvent event) {
        // TODO Auto-generated method stub
        System.out.println("SignChangeListener");
    }
}

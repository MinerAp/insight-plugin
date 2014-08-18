package com.amshulman.insight.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.util.InsightConfigurationContext;

public class RegistrationHandler implements Listener {

    private final WriteBackend backend;

    public RegistrationHandler(InsightConfigurationContext configurationContext) {
        backend = configurationContext.getWriteBackend();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(AsyncPlayerPreLoginEvent event) {
        backend.registerPlayer(event.getName(), event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(WorldLoadEvent event) {
        backend.registerWorld(event.getWorld().getName());
    }
}

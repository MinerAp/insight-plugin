package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class VehicleCreateListener extends InternalEventHandler<VehicleCreateEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleCreateEvent event) {
        // TODO Auto-generated method stub
        System.out.println("VehicleCreateListener");
    }
}

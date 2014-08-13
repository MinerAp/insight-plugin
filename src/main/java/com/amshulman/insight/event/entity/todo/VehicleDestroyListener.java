package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class VehicleDestroyListener extends InternalEventHandler<VehicleDestroyEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleDestroyEvent event) {
        // TODO Auto-generated method stub
        System.out.println("VehicleDestroyListener");
    }
}

package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class VehicleExitListener extends InternalEventHandler<VehicleExitEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleExitEvent event) {
        // TODO Auto-generated method stub
        System.out.println("VehicleExitListener");
    }
}

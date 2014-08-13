package com.amshulman.insight.event.entity.todo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class VehicleEnterListener extends InternalEventHandler<VehicleEnterEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleEnterEvent event) {
        // TODO Auto-generated method stub
        System.out.println("VehicleEnterListener");
    }
}

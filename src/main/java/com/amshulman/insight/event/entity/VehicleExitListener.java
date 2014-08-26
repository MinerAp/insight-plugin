package com.amshulman.insight.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class VehicleExitListener extends InternalEventHandler<VehicleExitEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleExitEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getExited()), EventCompat.VEHICLE_EXIT, event.getVehicle().getLocation(), EntityUtil.getName(event.getVehicle())));
    }
}

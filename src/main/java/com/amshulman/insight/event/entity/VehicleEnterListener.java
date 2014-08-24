package com.amshulman.insight.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class VehicleEnterListener extends InternalEventHandler<VehicleEnterEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(VehicleEnterEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getEntered()), EventCompat.VEHICLE_ENTER, event.getVehicle().getLocation(), EntityUtil.getName(event.getVehicle())));
        System.out.println("VehicleEnterListener");
    }
}

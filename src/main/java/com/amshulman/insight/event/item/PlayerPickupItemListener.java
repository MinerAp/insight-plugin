package com.amshulman.insight.event.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;

public class PlayerPickupItemListener extends InternalEventHandler<PlayerPickupItemEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerPickupItemEvent event) {
        add(new ItemRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.ITEM_PICKUP, event.getItem()));
        System.out.println("PlayerPickupItemListener");
    }
}

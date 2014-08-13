package com.amshulman.insight.event.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;

public class PlayerDropItemListener extends InternalEventHandler<PlayerDropItemEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerDropItemEvent event) {
        add(new ItemRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.ITEM_DROP, event.getItemDrop()));
        System.out.println("PlayerDropItemListener");
    }
}

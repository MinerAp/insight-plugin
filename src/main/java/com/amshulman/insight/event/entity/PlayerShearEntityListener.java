package com.amshulman.insight.event.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class PlayerShearEntityListener extends InternalEventHandler<PlayerShearEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerShearEntityEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.SHEEP_SHEAR, event.getEntity().getLocation(), EntityUtil.getName(EntityType.SHEEP)));
    }
}

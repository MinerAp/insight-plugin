package com.amshulman.insight.event.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class PlayerExpChangeListener extends InternalEventHandler<PlayerExpChangeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerExpChangeEvent event) {
        add(new EntityRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.EXP_GAIN, event.getPlayer().getLocation(), EntityUtil.getName(EntityType.EXPERIENCE_ORB)));
    }
}

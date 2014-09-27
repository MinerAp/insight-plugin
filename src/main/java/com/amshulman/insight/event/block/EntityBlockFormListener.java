package com.amshulman.insight.event.block;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.Util;

public class EntityBlockFormListener extends InternalEventHandler<EntityBlockFormEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityBlockFormEvent event) {
        BlockState previousState = Util.getBlockStateOrNullIfAir(event.getBlock().getState());
        add(new BlockRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_FORM, event.getNewState(), previousState));
    }
}

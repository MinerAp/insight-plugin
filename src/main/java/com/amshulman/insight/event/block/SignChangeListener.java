package com.amshulman.insight.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.serialization.SignMeta;
import com.amshulman.insight.types.EventCompat;

public class SignChangeListener extends InternalEventHandler<SignChangeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(SignChangeEvent event) {
        add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.SIGN_CHANGE, event.getBlock(), new SignMeta(event.getLines())));
    }
}

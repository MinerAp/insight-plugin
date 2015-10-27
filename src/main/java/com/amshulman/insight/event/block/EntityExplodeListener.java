package com.amshulman.insight.event.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class EntityExplodeListener extends InternalEventHandler<EntityExplodeEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityExplodeEvent event) {
        String name = EntityUtil.getName(event.getEntity());
        long time = System.currentTimeMillis();

        for (Block b : event.blockList()) {
            BlockState block = b.getState();
            if (block instanceof Chest) {
                Chest chest = (Chest) block;
                Location location = chest.getLocation();
                for (ItemStack item : chest.getBlockInventory()) {
                    if (item != null) {
                        add(new ItemRowEntry(time - 1, name, EventCompat.ITEM_REMOVE, location, item));
                    }
                }
            }
            add(new BlockRowEntry(time, name, EventCompat.BLOCK_EXPLODE, block));
        }
    }
}

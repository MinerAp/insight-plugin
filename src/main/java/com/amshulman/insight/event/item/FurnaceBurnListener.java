package com.amshulman.insight.event.item;

import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.inventory.InventoryManager;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InventoryUtils;
import com.amshulman.insight.util.NonPlayerLookup;

public class FurnaceBurnListener extends InternalEventHandler<FurnaceBurnEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(FurnaceBurnEvent event) {
        if (event.getBurnTime() <= 0) {
            return;
        }

        ItemStack fuel = InventoryUtils.cloneStack(event.getFuel(), 1);
        InventoryManager.directDiff(((Furnace) event.getBlock().getState()).getInventory(), fuel, 1);
        add(new ItemRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.ITEM_BURN, event.getBlock().getLocation(), fuel));
    }
}

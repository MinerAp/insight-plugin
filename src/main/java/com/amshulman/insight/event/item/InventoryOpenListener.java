package com.amshulman.insight.event.item;

import java.util.EnumSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.inventory.InventoryManager;

public class InventoryOpenListener extends InternalEventHandler<InventoryOpenEvent> {

    private static final EnumSet<InventoryType> loggedEvents =
            EnumSet.of(InventoryType.ANVIL, InventoryType.BEACON, InventoryType.BREWING,
                       InventoryType.CHEST, InventoryType.DISPENSER, InventoryType.DROPPER,
                       InventoryType.ENCHANTING, InventoryType.ENDER_CHEST, InventoryType.FURNACE,
                       InventoryType.HOPPER, InventoryType.MERCHANT, InventoryType.WORKBENCH);

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(InventoryOpenEvent event) {
        if (loggedEvents.contains(event.getInventory().getType())) {
            InventoryManager.inventoryOpen(event.getInventory(), event.getPlayer());
        }
    }
}

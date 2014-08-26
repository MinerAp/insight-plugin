package com.amshulman.insight.event.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.inventory.InventoryManager;

public class InventoryOpenListener extends InternalEventHandler<InventoryOpenEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(InventoryOpenEvent event) {

        // All cases are listed for documentation purposes
        switch (event.getView().getType()) {
            case ANVIL:
            case BEACON:
            case BREWING:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case ENCHANTING:
            case ENDER_CHEST:
            case FURNACE:
            case HOPPER:
            case MERCHANT:
            case WORKBENCH:
                InventoryManager.inventoryOpen(event.getInventory(), event.getPlayer());
                break;

            case CRAFTING:
                // never fires

            case CREATIVE:
            case PLAYER:
                // not logged
        }
    }
}

package com.amshulman.insight.event.item;

import lombok.RequiredArgsConstructor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.action.ItemAction;
import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.inventory.Container;
import com.amshulman.insight.inventory.InventoryManager;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InventoryUtils;

@RequiredArgsConstructor
public class InventoryCloseListener extends InternalEventHandler<InventoryCloseEvent> {

    private final WriteBackend backend;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(InventoryCloseEvent event) {
        Container changes;

        InventoryType type = event.getInventory().getType();
        if (InventoryUtils.ingredientsDropOnClose(type)) {
            for (ItemStack stack : InventoryUtils.getIngredientItems(event.getInventory())) {
                if (stack != null && !Material.AIR.equals(stack.getType())) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getPlayer(), stack);
                }
            }
        }

        // All cases are listed for documentation purposes
        switch (type) {
            case ANVIL:
            case BEACON:
            case BREWING:
            case DISPENSER:
            case DROPPER:
            case ENCHANTING:
            case FURNACE:
            case HOPPER:
            case MERCHANT:
            case WORKBENCH:
                changes = InventoryManager.inventoryClose(event.getInventory(), event.getPlayer());
                process(event.getPlayer().getName(), changes.getLocation(), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                break;

            case CHEST:
                changes = InventoryManager.inventoryClose(event.getInventory(), event.getPlayer());
                if (event.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest dc = (DoubleChest) event.getInventory().getHolder();
                    backend.suggestFlush(); // ensure we have room for both sets of records
                    process2(event.getPlayer().getName(), ((Chest) dc.getLeftSide()).getLocation(), ((Chest) dc.getRightSide()).getLocation(), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                } else {
                    process(event.getPlayer().getName(), changes.getLocation(), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                }
                break;

            case ENDER_CHEST:
                changes = InventoryManager.inventoryClose(event.getInventory(), event.getPlayer());
                process(event.getPlayer().getName(), changes.getLocation(), changes, EventCompat.ENDERCHEST_INSERT, EventCompat.ENDERCHEST_REMOVE);
                break;

            case CRAFTING:
                changes = InventoryManager.inventoryCloseIfOpen(event.getInventory(), event.getPlayer());
                if (changes != null) {
                    process(event.getPlayer().getName(), event.getPlayer().getLocation(), changes, EventCompat.CRAFTING_INSERT, EventCompat.CRAFTING_REMOVE);
                }
                break;

            // not logged
            case CREATIVE:
            case PLAYER:
                InventoryManager.inventoryCloseIfOpen(event.getInventory(), event.getPlayer()); // possible memory leak otherwise
        }
    }

    private void process(String name, Location loc, Container changes, ItemAction insertEvent, ItemAction removeEvent) {
        for (ItemStack stack : changes) {
            if (stack.getAmount() > 0) {
                add(new ItemRowEntry(changes.getTimeOpened(), name, insertEvent, loc, stack));
            } else {
                add(new ItemRowEntry(changes.getTimeOpened(), name, removeEvent, loc, InventoryUtils.reverseStack(stack)));
            }
        }
    }

    private void process2(String name, Location loc1, Location loc2, Container changes, ItemAction insertEvent, ItemAction removeEvent) {
        for (ItemStack stack : changes) {
            if (stack.getAmount() > 0) {
                add(new ItemRowEntry(changes.getTimeOpened(), name, insertEvent, loc1, stack));
                add(new ItemRowEntry(changes.getTimeOpened(), name, insertEvent, loc2, stack));
            } else {
                add(new ItemRowEntry(changes.getTimeOpened(), name, removeEvent, loc1, InventoryUtils.reverseStack(stack)));
                add(new ItemRowEntry(changes.getTimeOpened(), name, removeEvent, loc2, InventoryUtils.reverseStack(stack)));
            }
        }
    }
}

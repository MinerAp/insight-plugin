package com.amshulman.insight.event.item;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import com.amshulman.insight.action.ItemAction;
import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.inventory.Container;
import com.amshulman.insight.inventory.InventoryManager;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InventoryUtils;
import com.amshulman.insight.util.craftbukkit.Anvil;
import com.amshulman.insight.util.craftbukkit.v1_7_R4.Merchant;

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
                    // System.out.println("removing " + stack);
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
                process(event.getPlayer().getName(), getLocation(event.getInventory()), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                // System.out.println("InventoryCloseListener");
                break;

            case CHEST:
                changes = InventoryManager.inventoryClose(event.getInventory(), event.getPlayer());
                if (event.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest dc = (DoubleChest) event.getInventory().getHolder();
                    backend.suggestFlush(); // ensure we have room for both sets of records
                    process2(event.getPlayer().getName(), ((Chest) dc.getLeftSide()).getLocation(), ((Chest) dc.getRightSide()).getLocation(), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                } else {
                    process(event.getPlayer().getName(), getLocation(event.getInventory()), changes, EventCompat.ITEM_INSERT, EventCompat.ITEM_REMOVE);
                }
                // System.out.println("InventoryCloseListener");
                break;
                
            case ENDER_CHEST:
                changes = InventoryManager.inventoryClose(event.getInventory(), event.getPlayer());
                process(event.getPlayer().getName(), getLocation(event.getInventory()), changes, EventCompat.ENDERCHEST_INSERT, EventCompat.ENDERCHEST_REMOVE);
                // System.out.println("InventoryCloseListener");
                break;
                
            case CRAFTING:
                changes = InventoryManager.inventoryCloseIfOpen(event.getInventory(), event.getPlayer());
                if (changes != null) {
                    process(event.getPlayer().getName(), event.getPlayer().getLocation(), changes, EventCompat.CRAFTING_INSERT, EventCompat.CRAFTING_REMOVE);
                    // System.out.println("InventoryCloseListener");
                }
                break;

            case CREATIVE:
            case PLAYER:
                // not logged
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

        // System.out.println(changes.toString());
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

        // System.out.println(changes.toString());
    }

    private static Location getLocation(Inventory inv) {
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }

        if (inv instanceof AnvilInventory) {
            return Anvil.getInstance().getLocation(inv);
        }

        if (inv instanceof MerchantInventory) {
            return Merchant.getInstance().getLocation(inv);
        }

        if (holder == null) {
            System.err.println("Holder is null");
            return null;
        }

        try {
            System.out.println(holder.getClass());
            Method m = holder.getClass().getMethod("getLocation");
            if (m == null) {
                System.err.println("Could not find getLocation for " + holder.getClass()); // DEBUG
                return null;
            }

            return (Location) m.invoke(holder);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}

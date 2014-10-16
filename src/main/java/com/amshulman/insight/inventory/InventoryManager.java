package com.amshulman.insight.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.util.InventoryUtils;

public class InventoryManager {

    private static Map<Inventory, ContainerStateTracker> foo = new HashMap<>();

    public static void inventoryOpen(Inventory inventory, HumanEntity player) {
        assert (!foo.containsKey(inventory) || !foo.get(inventory).hasOpenEditSession(player));
        getCST(inventory, player);
    }

    public static Container inventoryClose(Inventory inventory, HumanEntity player) {
        ContainerStateTracker cst = foo.get(inventory);
        assert (cst != null); // DEBUG
        return inventoryClose(cst, inventory, player);
    }

    public static Container inventoryCloseIfOpen(Inventory inventory, HumanEntity player) {
        ContainerStateTracker cst = foo.get(inventory);

        if (cst == null) {
            return null;
        }

        return inventoryClose(cst, inventory, player);
    }

    private static Container inventoryClose(ContainerStateTracker cst, Inventory inventory, HumanEntity player) {
        Container changes = cst.closePlayerEditSession(player);

        if (cst.getEditorCount() == 0) {
            foo.remove(inventory);
        }

        return changes;
    }

    public static void recordItemInsertion(Inventory inventory, HumanEntity player, ItemStack insertedStack) {
        recordItemInsertion(inventory, player, insertedStack, insertedStack.getAmount());
    }

    public static void recordItemInsertion(Inventory inventory, HumanEntity player, ItemStack insertedStack, int amount) {
        ContainerStateTracker cst = getCST(inventory, player);
        cst.recordItemInsertion(player, insertedStack, amount);
    }

    public static void recordItemRemoval(Inventory inventory, HumanEntity player, ItemStack removedStack) {
        recordItemRemoval(inventory, player, removedStack, removedStack.getAmount());
    }

    public static void recordItemRemoval(Inventory inventory, HumanEntity player, ItemStack removedStack, int amount) {
        ContainerStateTracker cst = getCST(inventory, player);
        cst.recordItemRemoval(player, removedStack, amount);
    }

    public static void recordUnknownChange(Inventory inventory, HumanEntity player) {
        ContainerStateTracker cst = getCST(inventory, player);
        cst.recordUnknownChange(player, inventory);
    }
    
    public static void directDiff(Inventory inventory, HumanEntity player, ItemStack stack, int amount) {
        ContainerStateTracker cst = getCST(inventory, player);
        cst.directDiff(player, stack, amount);
    }
    
    public static void directDiff(Inventory inventory, ItemStack stack, int amount) {
        ContainerStateTracker cst = getCST(inventory, null);
        cst.directDiff(stack, amount);
    }

    private static ContainerStateTracker getCST(Inventory inventory, HumanEntity player) {
        ContainerStateTracker cst = foo.get(inventory);
        if (cst == null) {
//            System.out.println("inventoryOpen - " + inventory.getType()); // DEBUG
            cst = new ContainerStateTracker(inventory);
            foo.put(inventory, cst);
            cst.openPlayerEditSession(player);

            if (inventory.getType() == InventoryType.CRAFTING) {
                for (ItemStack stack : InventoryUtils.getIngredientItems(inventory)) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        cst.directDiff(player, stack, stack.getAmount());
                    }
                }
            }
        } else if (!cst.hasOpenEditSession(player)) {
//            System.out.println("inventoryOpen - " + inventory.getType()); // DEBUG
            cst.openPlayerEditSession(player);
        }

        return cst;
    }

    public static Container getContainer(Inventory inv) {
        switch (inv.getType()) {
            case ANVIL:
            case MERCHANT:
                return new Container(inv.getItem(0), inv.getItem(1));
            case CRAFTING:
            case WORKBENCH:
                return new Container(((CraftingInventory) inv).getMatrix());
            case FURNACE:
                FurnaceInventory f = (FurnaceInventory) inv;
                return new Container(f.getSmelting(), f.getFuel());
            case BREWING:
            case BEACON:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case ENCHANTING:
            case ENDER_CHEST:
            case HOPPER:
                return new Container(inv);
            case CREATIVE:
            case PLAYER:
                return new Container(inv);
        }

        return null;
    }
}

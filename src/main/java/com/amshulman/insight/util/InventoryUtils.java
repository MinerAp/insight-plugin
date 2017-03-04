package com.amshulman.insight.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryUtils {

    private static final EnumSet<InventoryType> CRAFTING_SEMANTICS = EnumSet.of(InventoryType.MERCHANT, InventoryType.CRAFTING, InventoryType.WORKBENCH);
    private static final EnumSet<InventoryType> DROPS_INGREDIENTS = EnumSet.of(InventoryType.ANVIL, InventoryType.ENCHANTING, InventoryType.MERCHANT, InventoryType.CRAFTING, InventoryType.WORKBENCH);
    private static final EnumSet<InventoryType> ITEM_RESTRICTIONS = EnumSet.of(InventoryType.BREWING, InventoryType.BEACON, InventoryType.FURNACE);
    
    private static final EnumSet<InventoryAction> PICKUP_ACTIONS = EnumSet.of(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.HOTBAR_MOVE_AND_READD, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_SLOT);

    public static boolean hasCraftingSemantics(InventoryType type) {
        return CRAFTING_SEMANTICS.contains(type);
    }

    public static boolean ingredientsDropOnClose(InventoryType type) {
        return DROPS_INGREDIENTS.contains(type);
    }

    public static boolean hasItemRestrictions(InventoryType type) {
        return ITEM_RESTRICTIONS.contains(type);
    }

    public static boolean isResultsSlotNormalRemoval(InventoryAction action) {
        return PICKUP_ACTIONS.contains(action);
    }

    public static boolean isLoggedContainerType(InventoryType type) {
        return EnumSet.complementOf(EnumSet.of(InventoryType.CREATIVE, InventoryType.PLAYER)).contains(type);
    }

    public static List<ItemStack> getIngredientItems(Inventory inv) {
        if (inv instanceof CraftingInventory) {
            return Arrays.asList(((CraftingInventory) inv).getMatrix());
        } else if (inv instanceof MerchantInventory) {
            return Arrays.asList(inv.getItem(0), inv.getItem(1));
        } else if (inv instanceof EnchantingInventory) {
            return Arrays.asList(((EnchantingInventory) inv).getItem());
        } else if (inv instanceof AnvilInventory) {
            return Arrays.asList(inv.getItem(0));
        }

        return Arrays.asList();
    }

    public static ItemStack getResultItem(Inventory inv) {
        if (inv instanceof MerchantInventory) {
            return inv.getItem(2);
        } else if (inv instanceof CraftingInventory) {
            return ((CraftingInventory) inv).getResult();
        }

        return null;
    }

    public static ItemStack cloneStack(ItemStack stack, int newAmount) {
        ItemStack newStack = new ItemStack(stack);
        newStack.setAmount(newAmount);
        return newStack;
    }

    public static ItemStack reverseStack(ItemStack stack) {
        return cloneStack(stack, -stack.getAmount());
    }

    public static boolean isResultSlot(Inventory inv, int slot) {
        if (inv instanceof CraftingInventory) {
            return slot == 0;
        } else if (inv instanceof MerchantInventory) {
            return slot == 2;
        } else if (inv instanceof AnvilInventory) {
            return slot == 2;
        }

        return false;
    }
}

package com.amshulman.insight.inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.util.InventoryUtils;
import com.amshulman.insight.util.craftbukkit.InventoryUtil;

public class ContainerStateTracker {

    private Container currentState;
    private Map<HumanEntity, Container> playerChangeSets = new HashMap<>();
    private Location loc;

    ContainerStateTracker(Inventory inventory) {
        currentState = InventoryManager.getContainer(inventory);
        loc = InventoryUtil.getLocation(inventory);
    }

    void openPlayerEditSession(HumanEntity player) {
        assert (!playerChangeSets.containsKey(player)); // DEBUG

        playerChangeSets.put(player, new Container(System.currentTimeMillis(), loc));
    }

    Container closePlayerEditSession(HumanEntity player) {
        assert (playerChangeSets.containsKey(player)); // DEBUG

        return playerChangeSets.remove(player);
    }

    boolean hasOpenEditSession(HumanEntity player) {
        return player == null || playerChangeSets.containsKey(player);
    }

    public int getEditorCount() {
        return playerChangeSets.size();
    }

    void recordItemInsertion(HumanEntity player, ItemStack insertedStack, int amount) {
        recordChange(playerChangeSets.get(player), InventoryUtils.cloneStack(insertedStack, amount));
        recordChange(currentState, InventoryUtils.cloneStack(insertedStack, amount));

//        // DEBUG 
//        System.out.println("Inserting " + amount + " " + insertedStack.getType());
//        if (playerChangeSets.get(player) != null) {
//            System.out.println("Player diff: " + playerChangeSets.get(player).toString());
//        }
//        System.out.println("New state: " + currentState);
//        System.out.println("--------------------------");
    }

    void recordItemRemoval(HumanEntity player, ItemStack removedStack, int amount) {
        recordChange(playerChangeSets.get(player), InventoryUtils.cloneStack(removedStack, -amount));
        recordChange(currentState, InventoryUtils.cloneStack(removedStack, -amount));

//        // DEBUG
//        System.out.println("Removing " + amount + " " + removedStack.getType());
//        if (playerChangeSets.get(player) != null) {
//            System.out.println("Player diff: " + playerChangeSets.get(player).toString());
//        }
//        System.out.println("New state: " + currentState);
//        System.out.println("--------------------------");
    }

    void recordUnknownChange(HumanEntity player, Inventory inv) {
        Container newState = InventoryManager.getContainer(inv);
        Container changeSet = playerChangeSets.get(player);

//        boolean b = false;
        Set<ItemStack> newChanges = getChanges(currentState, newState.clone());
        for (ItemStack stack : newChanges) {
//            b = true;
            recordChange(changeSet, stack);
        }

//        if (b) {
//            System.out.println("Rebalancing");
//            System.out.println("Current state: " + currentState);
//        }

        currentState = newState;

//        // DEBUG
//        if (b) {
//            System.out.println("Player diff: " + playerChangeSets.get(player).toString());
//            System.out.println("New state: " + currentState);
//        }
    }

    private void recordChange(@Nonnull Container changeSet, ItemStack changedStack) {
        ItemStack previousChangedStack = changeSet.get(changedStack);
        if (previousChangedStack == null) {
            changeSet.add(changedStack);
            return;
        }

        int newAmount = previousChangedStack.getAmount() + changedStack.getAmount();
        if (newAmount == 0) {
            changeSet.remove(previousChangedStack);
            return;
        }

        previousChangedStack.setAmount(newAmount);
    }

    void directDiff(HumanEntity player, ItemStack removedStack, int amount) {
        recordChange(playerChangeSets.get(player), InventoryUtils.cloneStack(removedStack, amount));

//        // DEBUG
//        System.out.println("Direct diff of " + amount + " " + removedStack.getType());
//        System.out.println("Player diff: " + playerChangeSets.get(player).toString());
//        System.out.println("New state: " + currentState);
//        System.out.println("--------------------------");
    }

    void directDiff(ItemStack stack, int amount) {
        recordChange(currentState, InventoryUtils.cloneStack(stack, amount));

//        // DEBUG
//        System.out.println("Direct diff of " + amount + " " + stack.getType());
//        System.out.println("New state: " + currentState);
//        System.out.println("--------------------------");
    }

    public static Set<ItemStack> getChanges(Container before, Container after) {
        if (before == null || after == null) {
            return null;
        }

        Set<ItemStack> changes = new HashSet<>();

        for (ItemStack itemBefore : before) {
            ItemStack itemAfter = after.get(itemBefore);

            if (itemAfter != null) {
                int diff = itemAfter.getAmount() - itemBefore.getAmount();

                if (diff != 0) {
                    ItemStack changedStack = new ItemStack(itemBefore);
                    changedStack.setAmount(diff);
                    changes.add(changedStack);
                }

                after.remove(itemAfter);
            } else { // complete removal
                ItemStack changedStack = new ItemStack(itemBefore);
                changedStack.setAmount(-itemBefore.getAmount());
                changes.add(changedStack);
            }
        }

        for (ItemStack itemAfter : after) {
            changes.add(new ItemStack(itemAfter));
        }

        return changes;
    }
}

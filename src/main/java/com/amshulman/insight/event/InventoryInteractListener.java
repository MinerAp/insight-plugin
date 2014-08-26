package com.amshulman.insight.event;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.amshulman.insight.inventory.Container;
import com.amshulman.insight.inventory.ContainerStateTracker;
import com.amshulman.insight.inventory.InventoryManager;
import com.amshulman.insight.util.InventoryUtils;

public class InventoryInteractListener implements Listener {

    public static final Plugin plugin = Bukkit.getPluginManager().getPlugin("Insight");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        InventoryType clickedInventoryType = event.getClickedInventory().getType();
        InventoryType primaryInventoryType = event.getInventory().getType();

        if (InventoryUtils.isResultSlot(event.getClickedInventory(), event.getSlot())) {
            if (InventoryUtils.isResultsSlotNormalRemoval(event.getAction())) {
                foo(event);
                InventoryManager.directDiff(event.getInventory(), event.getWhoClicked(), event.getCurrentItem(), -event.getCurrentItem().getAmount());
            } else if (InventoryAction.MOVE_TO_OTHER_INVENTORY.equals(event.getAction())) {
                foo(event);
                bar(event);
            }

            return;
        }

        switch (event.getAction()) {
            /* Basic insertions */
            case PLACE_ALL:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemInsertion(event.getInventory(), event.getWhoClicked(), event.getCursor());
                }
                return;
            case PLACE_ONE:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemInsertion(event.getInventory(), event.getWhoClicked(), event.getCursor(), 1);
                }
                return;
            case PLACE_SOME:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    int capacity = event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount();
                    int available = event.getCursor().getAmount();
                    InventoryManager.recordItemInsertion(event.getInventory(), event.getWhoClicked(), event.getCursor(), Math.min(capacity, available));
                }
                return;

            /* Basic removals */
            case DROP_ALL_SLOT:
            case HOTBAR_MOVE_AND_READD:
            case PICKUP_ALL:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem());
                }
                return;
            case PICKUP_HALF:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem(), (event.getCurrentItem().getAmount() + 1) / 2);
                }
                return;
            case DROP_ONE_SLOT:
            case PICKUP_ONE:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem(), 1);
                }
                return;
            case PICKUP_SOME:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem(), event.getCurrentItem().getMaxStackSize());
                }
                return;

            /* Swap */
            case SWAP_WITH_CURSOR:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem());
                    InventoryManager.recordItemInsertion(event.getInventory(), event.getWhoClicked(), event.getCursor());
                }
                return;

            /* Move between inventories */
            case MOVE_TO_OTHER_INVENTORY:
                if (InventoryUtils.isLoggedContainerType(clickedInventoryType)) {
                    InventoryManager.recordItemRemoval(event.getInventory(), event.getWhoClicked(), event.getCurrentItem());
                } else if (InventoryUtils.hasItemRestrictions(primaryInventoryType)) {
                    pernicious(event); // brewing stands and beacons only accept transfers of certain items
                } else if (!InventoryUtils.hasCraftingSemantics(primaryInventoryType)) {
                    InventoryManager.recordItemInsertion(event.getInventory(), event.getWhoClicked(), event.getCurrentItem());
                }
                return;

            /* Gather all */
            case COLLECT_TO_CURSOR:
                pernicious(event);
                return;

            /* NOTHING! ABSOLUTELY NOTHING! STUPID! YOU SO STUPID! */
            case CLONE_STACK:
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
            case HOTBAR_SWAP:
            case NOTHING:
            case UNKNOWN:
                return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(InventoryDragEvent event) {
        pernicious(event);
    }

    // Obnoxious workaround because at the time the event is called the items have not been collected
    private static void pernicious(final InventoryInteractEvent event) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                InventoryManager.recordUnknownChange(event.getInventory(), event.getWhoClicked());
            }
        });
    }

    private static void foo(final InventoryClickEvent event) {
        final Container before = InventoryManager.getContainer(event.getView().getTopInventory());

        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                Container after = InventoryManager.getContainer(event.getView().getTopInventory());
                Set<ItemStack> diff = ContainerStateTracker.getChanges(before, after);
                for (ItemStack stack : diff) {
                    // System.out.println("consuming " + stack);
                    InventoryManager.directDiff(event.getInventory(), stack, stack.getAmount());
                }
            }
        });
    }

    private static void bar(final InventoryClickEvent event) {
        final Container before = InventoryManager.getContainer(event.getView().getBottomInventory());

        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                Container after = InventoryManager.getContainer(event.getView().getBottomInventory());
                Set<ItemStack> diff = ContainerStateTracker.getChanges(before, after);
                // System.out.println(diff);
                for (ItemStack stack : diff) {
                    // System.out.println("adding " + stack + " to diff");
                    InventoryManager.directDiff(event.getInventory(), event.getWhoClicked(), stack, -stack.getAmount());
                }
            }
        });
    }
}

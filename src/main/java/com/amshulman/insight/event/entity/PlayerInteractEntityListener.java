package com.amshulman.insight.event.entity;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.InsightConfigurationContext;

public class PlayerInteractEntityListener extends InternalEventHandler<PlayerInteractEntityEvent> {

    private final boolean loggingHangings;
    private final boolean loggingSheep;

    public PlayerInteractEntityListener(InsightConfigurationContext configurationContext) {
        loggingHangings = configurationContext.isLoggingHangings();
        loggingSheep = configurationContext.isLoggingSheep();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerInteractEntityEvent event) {
        String playerName = event.getPlayer().getName();
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (event.getRightClicked() instanceof ItemFrame && loggingHangings) {
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

            if (!Material.AIR.equals(itemFrame.getItem().getType())) {
                add(new ItemRowEntry(System.currentTimeMillis(), playerName, EventCompat.ITEM_ROTATE, itemFrame.getLocation(), itemFrame.getItem()));
            } else if (!Material.AIR.equals(itemInHand.getType())) {
                ItemStack inserted = new ItemStack(itemInHand);
                inserted.setAmount(1);
                add(new ItemRowEntry(System.currentTimeMillis(), playerName, EventCompat.ITEM_INSERT, itemFrame.getLocation(), inserted));
            }
        } else if (itemInHand.getData() instanceof Dye) {
            if (loggingSheep) {
                add(new EntityRowEntry(System.currentTimeMillis(), playerName, EventCompat.SHEEP_DYE, event.getRightClicked().getLocation(), EntityUtil.getName(EntityType.SHEEP)));
            }
        }
    }
}

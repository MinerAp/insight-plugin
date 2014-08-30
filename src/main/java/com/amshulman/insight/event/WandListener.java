package com.amshulman.insight.event;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.backend.PlayerCallbackReadBackend;
import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.util.WandUtil;

public class WandListener implements Listener {

    private final PlayerInfoManager infoManager;
    private final PlayerCallbackReadBackend readBackend;

    public WandListener(InsightConfigurationContext configurationContext) {
        readBackend = configurationContext.getReadBackend();
        infoManager = configurationContext.getInfoManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandInteract(PlayerInteractEvent event) {
        if (!WandUtil.isWand(event.getItem())) {
            return;
        }

        if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
            query(event.getPlayer().getName(), event.getClickedBlock().getLocation());
        } else if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
            query(event.getPlayer().getName(), event.getClickedBlock().getRelative(event.getBlockFace()).getLocation());
        } else {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();
            if (WandUtil.isWand(player.getItemInHand())) {
                query(player.getName(), event.getEntity().getLocation());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(PlayerInteractEntityEvent event) {
        if (WandUtil.isWand(event.getPlayer().getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (WandUtil.isWand(player.getItemInHand())) {
                if (!(event.getEntity() instanceof LivingEntity)) {
                    query(player.getName(), event.getEntity().getLocation());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDrop(PlayerDropItemEvent event) {
        if (WandUtil.isWand(event.getItemDrop().getItemStack())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop this item.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(EntityDeathEvent event) {
        for (Iterator<ItemStack> iter = event.getDrops().iterator(); iter.hasNext();) {
            if (WandUtil.isWand(iter.next())) {
                iter.remove();
            }
        }
    }

    // @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEvent(InventoryClickEvent event) {
        if (WandUtil.isWand(event.getCurrentItem()) && event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    private void query(String playerName, Location loc) {
        QueryParameters wandQueryParams = infoManager.getPlayerInfo(playerName).getWandQueryParameters();
        QueryParameterBuilder queryBuilder = QueryUtil.copyCommonParameters(wandQueryParams, readBackend.newQueryBuilder());

        queryBuilder.setArea(loc, wandQueryParams.getRadius());
        readBackend.query(playerName, queryBuilder.build(), true);
    }
}

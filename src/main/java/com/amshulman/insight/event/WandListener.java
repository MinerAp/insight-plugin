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

import com.amshulman.insight.backend.QueryReadBackend;
import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.types.InsightLocation;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.util.Util;

public class WandListener implements Listener {

    private static final String WAND_QUERY_PERMISSION = InsightCommands.WAND.getPrefix() + InsightCommands.WAND.name();

    private final PlayerInfoManager infoManager;
    private final QueryReadBackend readBackend;

    public WandListener(InsightConfigurationContext configurationContext) {
        readBackend = new QueryReadBackend(configurationContext);
        infoManager = configurationContext.getInfoManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandInteract(PlayerInteractEvent event) {
        if (!Util.isWand(event.getItem())) {
            return;
        }

        if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
            query(event.getPlayer(), event.getClickedBlock().getLocation());
        } else if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
            query(event.getPlayer(), event.getClickedBlock().getRelative(event.getBlockFace()).getLocation());
        } else {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();
            if (Util.isWand(player.getInventory().getItemInMainHand())) {
                query(player, event.getEntity().getLocation());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(PlayerInteractEntityEvent event) {
        if (Util.isWand(event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (Util.isWand(player.getInventory().getItemInMainHand())) {
                if (!(event.getEntity() instanceof LivingEntity)) {
                    query(player, event.getEntity().getLocation());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDrop(PlayerDropItemEvent event) {
        if (Util.isWand(event.getItemDrop().getItemStack()) && event.getPlayer().hasPermission(WAND_QUERY_PERMISSION)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop this item.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).hasPermission(WAND_QUERY_PERMISSION)) {
            for (Iterator<ItemStack> iter = event.getDrops().iterator(); iter.hasNext();) {
                if (Util.isWand(iter.next())) {
                    iter.remove();
                }
            }
        }
    }

    // @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEvent(InventoryClickEvent event) {
        if (Util.isWand(event.getCurrentItem()) && event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }

    private void query(Player player, Location loc) {
        if (!player.hasPermission(WAND_QUERY_PERMISSION)) {
            return;
        }

        QueryParameters wandQueryParams = infoManager.getPlayerInfo(player.getName()).getWandQueryParameters();
        QueryParameterBuilder queryBuilder = QueryUtil.copyCommonParameters(wandQueryParams, readBackend.newQueryBuilder());

        queryBuilder.setArea(new InsightLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()), wandQueryParams.getRadius());
        readBackend.query(player.getName(), queryBuilder.build(), true);
    }
}

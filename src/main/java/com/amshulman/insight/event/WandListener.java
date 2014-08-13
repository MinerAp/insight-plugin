package com.amshulman.insight.event;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

        Block block;

        if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
            block = event.getClickedBlock();
        } else if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
            block = event.getClickedBlock().getRelative(event.getBlockFace());
        } else {
            return;
        }

        event.setCancelled(true);
        String playerName = event.getPlayer().getName();

        QueryParameters wandQueryParams = infoManager.getPlayerInfo(playerName).getWandQueryParameters();
        QueryParameterBuilder queryBuilder = readBackend.newQueryBuilder();

        QueryUtil.copyActors(wandQueryParams, queryBuilder);
        QueryUtil.copyActions(wandQueryParams, queryBuilder);
        QueryUtil.copyActees(wandQueryParams, queryBuilder);
        QueryUtil.copyMaterials(wandQueryParams, queryBuilder);
        QueryUtil.copyOrder(wandQueryParams, queryBuilder);
        QueryUtil.copyTimes(wandQueryParams, queryBuilder);

        int radius = wandQueryParams.getRadius();
        queryBuilder.setArea(block.getLocation(), radius);

        readBackend.query(playerName, queryBuilder.build(), true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandDamage(BlockDamageEvent event) {
        if (WandUtil.isWand(event.getItemInHand())) {
            return;
        }
    }
}

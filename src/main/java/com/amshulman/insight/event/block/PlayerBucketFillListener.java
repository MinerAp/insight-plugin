package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;

public class PlayerBucketFillListener extends InternalEventHandler<PlayerBucketFillEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerBucketFillEvent event) {
        Block liquid = event.getBlockClicked().getRelative(event.getBlockFace());
        BlockState blockState = liquid.getState();

        if (Material.STATIONARY_WATER.equals(liquid.getType()) || Material.WATER.equals(liquid.getType())) {
            blockState.setType(Material.STATIONARY_WATER);
            add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BUCKET_REMOVE, blockState));
        } else if (Material.STATIONARY_LAVA.equals(liquid.getType()) || Material.LAVA.equals(liquid.getType())) {
            blockState.setType(Material.STATIONARY_LAVA);
            add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BUCKET_REMOVE, blockState));
        }
    }
}

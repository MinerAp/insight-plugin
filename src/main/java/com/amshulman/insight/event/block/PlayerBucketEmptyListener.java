package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;

public class PlayerBucketEmptyListener extends InternalEventHandler<PlayerBucketEmptyEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerBucketEmptyEvent event) {
        BlockState blockState = event.getBlockClicked().getRelative(event.getBlockFace()).getState();

        if (Material.WATER_BUCKET.equals(event.getBucket())) {
            blockState.setType(Material.STATIONARY_WATER);

            System.out.println("PlayerBucketEmptyListener - water");
        } else if (Material.LAVA_BUCKET.equals(event.getBucket())) {
            blockState.setType(Material.STATIONARY_LAVA);

            System.out.println("PlayerBucketEmptyListener - lava");
        }

        if (!blockState.getType().equals(event.getBlockClicked().getType())) {
            add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BUCKET_PLACE, blockState));
        }
    }
}

package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.serialization.MetadataEntry;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;

public class BlockBreakListener extends InternalEventHandler<BlockBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockBreakEvent event) {
        MetadataEntry meta = null;
        if (Material.SKULL.equals(event.getBlock().getType())) {
            meta = new SkullMeta((Skull) event.getBlock().getState());
        }

        add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_BREAK, event.getBlock(), meta));

        boolean isDoubleBlock = false;
        if (isDoubleBlock) {
            if (event.getBlock().getRelative(BlockFace.UP).getType().equals(event.getBlock().getType())) {
                add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_BREAK, event.getBlock().getRelative(BlockFace.UP), meta));
            } else if (event.getBlock().getRelative(BlockFace.DOWN).getType().equals(event.getBlock().getType())) {
                add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_BREAK, event.getBlock().getRelative(BlockFace.DOWN), meta));
            }
        }

        System.out.println("BlockBreakListener");
    }
}

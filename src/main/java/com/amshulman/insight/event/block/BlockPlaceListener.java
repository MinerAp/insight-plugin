package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.serialization.MetadataEntry;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.InsightConfigurationContext;

public class BlockPlaceListener extends InternalEventHandler<BlockPlaceEvent> {

    private final boolean loggingFarmland;

    public BlockPlaceListener(InsightConfigurationContext configurationContext) {
        loggingFarmland = configurationContext.isLoggingFarmland();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockPlaceEvent event) {
        if (Material.FIRE.equals(event.getBlock().getType()) || event instanceof BlockMultiPlaceEvent) {
            return;
        }

        if (Material.SOIL.equals(event.getBlock().getType())) {
            if (loggingFarmland) {
                add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.SOIL_TILL, event.getBlockReplacedState()));
            }
            return;
        }

        MetadataEntry meta = null;
        if (Material.SKULL.equals(event.getBlock().getType())) {
            meta = new SkullMeta((Skull) event.getBlock().getState(), event.getPlayer().getItemInHand().getItemMeta());
        }

        BlockState previousBlock = null;
        if (event.getBlockReplacedState().getType() != Material.AIR) {
            previousBlock = event.getBlockReplacedState();
        }

        add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, event.getBlock(), previousBlock, meta));
    }
}

package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.NonPlayerLookup;
import com.amshulman.insight.util.craftbukkit.Block;

public class BlockIgniteListener extends InternalEventHandler<BlockIgniteEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockIgniteEvent event) {

        if (!Block.getInstance().willIgnite(event.getBlock().getRelative(BlockFace.DOWN))) {
            return;
        }

        BlockState fire = event.getBlock().getState();
        fire.setType(Material.FIRE);

        switch (event.getCause()) {
            case SPREAD:
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.FIRE_SPREAD, fire));
                System.out.println("BlockIgniteListener -- spread");
                return;
            case FLINT_AND_STEEL:
                add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_IGNITE, fire));
                System.out.println("BlockIgniteListener -- flint");
                break;
            case LAVA:
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.LAVA, EventCompat.BLOCK_IGNITE, fire));
                System.out.println("BlockIgniteListener - lava");
                break;
            case LIGHTNING:
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.LIGHTNING, EventCompat.BLOCK_IGNITE, fire));
                System.out.println("BlockIgniteListener - lightning");
                break;
            case EXPLOSION:
                System.out.println("BlockIgniteListener - explosion");
                add(new BlockRowEntry(System.currentTimeMillis(), NonPlayerLookup.NATURE, EventCompat.BLOCK_IGNITE, fire));
                break;
            case FIREBALL:
                System.out.println("BlockIgniteListener - fireball");
                break;
            case ENDER_CRYSTAL:
            default:
                System.out.println("BlockIgniteListener - ???");
                break;
        }
    }
}

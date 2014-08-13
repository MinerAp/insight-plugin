package com.amshulman.insight.event.block;

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.serialization.MetadataEntry;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;

public class BlockPlaceListener extends InternalEventHandler<BlockPlaceEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(BlockPlaceEvent event) {
        if (Material.FIRE.equals(event.getBlock().getType())) {
            return;
        }

        MetadataEntry meta = null;
        if (Material.SKULL.equals(event.getBlock().getType())) {
            meta = new SkullMeta((Skull) event.getBlock().getState(), event.getPlayer().getItemInHand().getItemMeta());
        }

        add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, event.getBlock(), meta));

//            if(isDoubleBlock(event.getBlock().getType())) {
//                if (event.getBlock().getRelative(BlockFace.UP).getType().equals(event.getBlock().getType())) {
//                    add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, event.getBlock().getRelative(BlockFace.UP)));
//                } else if (event.getBlock().getRelative(BlockFace.DOWN).getType().equals(event.getBlock().getType())) {
//                    add(new BlockRowEntry(System.currentTimeMillis(), event.getPlayer().getName(), EventCompat.BLOCK_PLACE, event.getBlock().getRelative(BlockFace.DOWN)));
//                }
//            }
            
//            Bed b;
//            if (b.isHeadOfBed()) {
//                (((Block) b).getRelative(b.getFacing())).getLocation();
//            } else {
//                (((Block) b).getRelative(b.getFacing().getOppositeFace())).getLocation();
//            }

        System.out.println("BlockPlaceListener");
    }

    private static boolean isDoubleBlock(Material mat) {
        return EnumSet.of(Material.IRON_DOOR_BLOCK, Material.WOOD_DOOR, Material.DOUBLE_PLANT, Material.BED).contains(mat);
    }
    
    private static Location getOtherBlock() {
        return null;
    }
}

package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;

public class EntityChangeBlockListener extends InternalEventHandler<EntityChangeBlockEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityChangeBlockEvent event) {
        switch (event.getEntity().getType()) {
            case SHEEP:
                add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.ENTITY_EAT, event.getBlock()));
                System.out.println("EntityChangeBlockListener - sheep eat");
                break;
            case FALLING_BLOCK:
                add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.BLOCK_DROP, event.getBlock()));
                System.out.println("EntityChangeBlockListener - falling block");
                break;
            case ZOMBIE:
                add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.BLOCK_BREAK, event.getBlock()));
                if (Material.WOODEN_DOOR.equals(event.getBlock().getRelative(BlockFace.UP).getType())) {
                    add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.BLOCK_BREAK, event.getBlock().getRelative(BlockFace.DOWN)));
                } else if (Material.WOODEN_DOOR.equals(event.getBlock().getRelative(BlockFace.DOWN).getType())) {
                    add(new BlockRowEntry(System.currentTimeMillis(), event.getEntity().getType().name(), EventCompat.BLOCK_BREAK, event.getBlock().getRelative(BlockFace.UP)));
                }
                System.out.println("EntityBreakDoorListener");
                break;
            /*
             * case ARROW:
             * case BAT:
             * case BLAZE:
             * case BOAT:
             * case CAVE_SPIDER:
             * case CHICKEN:
             * case COMPLEX_PART:
             * case COW:
             * case CREEPER:
             * case DROPPED_ITEM:
             * case EGG:
             * case ENDERMAN:
             * case ENDER_CRYSTAL:
             * case ENDER_DRAGON:
             * case ENDER_PEARL:
             * case ENDER_SIGNAL:
             * case EXPERIENCE_ORB:
             * case FIREBALL:
             * case FIREWORK:
             * case FISHING_HOOK:
             * case GHAST:
             * case GIANT:
             * case HORSE:
             * case IRON_GOLEM:
             * case ITEM_FRAME:
             * case LEASH_HITCH:
             * case LIGHTNING:
             * case MAGMA_CUBE:
             * case MINECART:
             * case MINECART_CHEST:
             * case MINECART_COMMAND:
             * case MINECART_FURNACE:
             * case MINECART_HOPPER:
             * case MINECART_MOB_SPAWNER:
             * case MINECART_TNT:
             * case MUSHROOM_COW:
             * case OCELOT:
             * case PAINTING:
             * case PIG:
             * case PIG_ZOMBIE:
             * case PLAYER:
             * case PRIMED_TNT:
             * case SILVERFISH:
             * case SKELETON:
             * case SLIME:
             * case SMALL_FIREBALL:
             * case SNOWBALL:
             * case SNOWMAN:
             * case SPIDER:
             * case SPLASH_POTION:
             * case SQUID:
             * case THROWN_EXP_BOTTLE:
             * case UNKNOWN:
             * case VILLAGER:
             * case WEATHER:
             * case WITCH:
             * case WITHER:
             * case WITHER_SKULL:
             * case WOLF:
             */
            default:
                System.out.println("EntityChangeBlockListener - ???");
                break;
        }
    }
}

package com.amshulman.insight.event.block;

import org.bukkit.block.Block;
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
        long when = System.currentTimeMillis();
        switch (event.getEntity().getType()) {
            case SHEEP:
                add(new BlockRowEntry(when, event.getEntity().getType().name(), EventCompat.ENTITY_EAT, event.getBlock()));
                break;
            case FALLING_BLOCK:
                add(new BlockRowEntry(when, event.getEntity().getType().name(), EventCompat.BLOCK_DROP, event.getBlock()));
                break;
            case ZOMBIE:
                add(new BlockRowEntry(when, event.getEntity().getType().name(), EventCompat.BLOCK_BREAK, event.getBlock()));

                Block other;
                if (event.getBlock().getData() < 8) {
                    other = event.getBlock().getRelative(BlockFace.UP);
                } else {
                    other = event.getBlock().getRelative(BlockFace.DOWN);
                }

                add(new BlockRowEntry(when, event.getEntity().getType().name(), EventCompat.BLOCK_BREAK, other));
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

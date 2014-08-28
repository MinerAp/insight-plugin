package com.amshulman.insight.event.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.InsightConfigurationContext;

public class EntityChangeBlockListener extends InternalEventHandler<EntityChangeBlockEvent> {

    private final boolean loggingSheep;

    public EntityChangeBlockListener(InsightConfigurationContext configurationContext) {
        loggingSheep = configurationContext.isLoggingSheep();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityChangeBlockEvent event) {
        long when = System.currentTimeMillis();
        switch (event.getEntity().getType()) {
            case SHEEP:
                if (loggingSheep) {
                    add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.SHEEP_EAT, event.getBlock()));
                }
                break;
            case FALLING_BLOCK:
                add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_DROP, event.getBlock()));
                break;
            case ZOMBIE:
                add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_BREAK, event.getBlock()));

                Block other;
                if (event.getBlock().getData() < 8) {
                    other = event.getBlock().getRelative(BlockFace.UP);
                } else {
                    other = event.getBlock().getRelative(BlockFace.DOWN);
                }

                add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_BREAK, other));
                break;
            default:
                System.out.println("EntityChangeBlockListener - ??? " + event.getEntityType() + " " + event.getBlock().getType());
                break;
        }
    }
}

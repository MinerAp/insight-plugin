package com.amshulman.insight.event.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.MaterialData;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.BlockRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.InsightConfigurationContext;

public class EntityChangeBlockListener extends InternalEventHandler<EntityChangeBlockEvent> {

    private final boolean loggingSheep;
    private final boolean loggingFarmland;

    public EntityChangeBlockListener(InsightConfigurationContext configurationContext) {
        loggingSheep = configurationContext.isLoggingSheep();
        loggingFarmland = configurationContext.isLoggingFarmland();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityChangeBlockEvent event) {
        long when = System.currentTimeMillis();
        switch (event.getEntity().getType()) {
            case SHEEP:
                if (loggingSheep) {
                    BlockState newState = event.getBlock().getState();
                    newState.setType(event.getTo());
                    newState.setRawData((byte) 0);

                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.SHEEP), EventCompat.SHEEP_EAT, event.getBlock(), newState));
                }
                break;
            case FALLING_BLOCK:
                add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.BLOCK_DROP, event.getBlock()));
                break;
            case ZOMBIE:
                add(new BlockRowEntry(when, EntityUtil.getName(EntityType.ZOMBIE), EventCompat.BLOCK_BREAK, event.getBlock()));

                Block other;
                if (event.getBlock().getData() < 8) {
                    other = event.getBlock().getRelative(BlockFace.UP);
                } else {
                    other = event.getBlock().getRelative(BlockFace.DOWN);
                }

                add(new BlockRowEntry(when, EntityUtil.getName(EntityType.ZOMBIE), EventCompat.BLOCK_BREAK, other));
                break;
            case ENDERMAN:
                MaterialData carried = ((Enderman) event.getEntity()).getCarriedMaterial();

                if (Material.AIR == carried.getItemType()) {
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.ENDERMAN), EventCompat.ENDERMAN_REMOVE, event.getBlock()));
                } else {
                    BlockState state = event.getBlock().getState();
                    state.setType(carried.getItemType());
                    state.setRawData(carried.getData());

                    add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.ENDERMAN_PLACE, state));
                }
                break;
            case SILVERFISH: // silverfish entering blocks is not logged
                if (Material.AIR == event.getTo()) {
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.SILVERFISH), EventCompat.BLOCK_BREAK, event.getBlock()));
                }
                break;
            case BOAT:
                Entity passenger = event.getEntity().getPassenger();
                if (passenger == null) {
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.BOAT), EventCompat.BLOCK_BREAK, event.getBlock()));
                } else {
                    add(new BlockRowEntry(when, EntityUtil.getName(passenger), EventCompat.BLOCK_BREAK, event.getBlock()));
                }
                break;
            case VILLAGER:
                if (event.getBlock().getType() == Material.AIR) {
                    BlockState block = event.getBlock().getState();
                    block.setType(event.getTo());
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.VILLAGER), EventCompat.BLOCK_PLACE, block));
                } else if (event.getBlock().getType() == Material.SOIL) {
                    logFarmlandTrample(when, event);
                } else {
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.VILLAGER), EventCompat.BLOCK_BREAK, event.getBlock()));
                }
                break;
            case RABBIT:
                if (event.getBlock().getType() == Material.CARROT) {
                    add(new BlockRowEntry(when, EntityUtil.getName(EntityType.RABBIT), EventCompat.BLOCK_BREAK, event.getBlock()));
                }
                break;
            case ARROW:
                if (event.getBlock().getType() == Material.TNT) {
                    // TODO: Record TNT activating.
                }
                break;
            default:
                if (event.getBlock().getType() == Material.REDSTONE_ORE) {
                    return; // Not logged
                } else if (event.getBlock().getType() == Material.SOIL) {
                    logFarmlandTrample(when, event);
                    return;
                }

                System.out.println("EntityChangeBlockListener - ??? " + event.getEntityType() + " " + event.getBlock().getType());
                break;
        }
    }

    void logFarmlandTrample(long when, EntityChangeBlockEvent event) {
        if (loggingFarmland) {
            BlockState newState = event.getBlock().getState();
            newState.setType(event.getTo());
            newState.setRawData((byte) 0);

            add(new BlockRowEntry(when, EntityUtil.getName(event.getEntity()), EventCompat.SOIL_TRAMPLE, event.getBlock(), newState));
        }
    }
}

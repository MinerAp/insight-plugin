package com.amshulman.insight.event.entity;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;

public class EntityDeathListener extends InternalEventHandler<EntityDeathEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityDeathEvent event) {
        EntityDamageEvent previousEvent = event.getEntity().getLastDamageCause();
        Location loc = event.getEntity().getLocation();
        String acteeName = EntityUtil.getName(event.getEntity());

        if (previousEvent instanceof EntityDamageByEntityEvent) { // player killed
            EntityDamageByEntityEvent entityKillEvent = (EntityDamageByEntityEvent) previousEvent;
            add(new EntityRowEntry(System.currentTimeMillis(), EntityUtil.getName(entityKillEvent.getDamager()), EventCompat.ENTITY_KILL, loc, acteeName));
            System.out.println("EntityDeathListener -- entity kill");
        } else if (previousEvent instanceof EntityDamageByBlockEvent) { // environment killed
            EntityDamageByBlockEvent natureKillEvent = (EntityDamageByBlockEvent) previousEvent;
            if (natureKillEvent.getDamager() != null) {
                add(new EntityRowEntry(System.currentTimeMillis(), natureKillEvent.getDamager().getType().name(), EventCompat.ENTITY_KILL, loc, acteeName));
                System.out.println("EntityDeathListener -- block kill");
            } else {
                add(new EntityRowEntry(System.currentTimeMillis(), acteeName, EventCompat.ENTITY_DEATH, loc, acteeName));
                System.out.println("EntityDeathListener -- death #1");
            }
            System.out.println(EntityUtil.getName(event.getEntity()));
        } else { // died
            add(new EntityRowEntry(System.currentTimeMillis(), acteeName, EventCompat.ENTITY_DEATH, loc, acteeName));
            System.out.println("EntityDeathListener -- death #2");
        }

        for (ItemStack stack : event.getDrops()) {
            add(new ItemRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getEntity()), EventCompat.ITEM_DROP, loc, stack));
        }
    }
}

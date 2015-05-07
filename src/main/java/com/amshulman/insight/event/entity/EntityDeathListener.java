package com.amshulman.insight.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.row.EntityRowEntry;
import com.amshulman.insight.row.ItemRowEntry;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.util.EntityUtil;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.NonPlayerLookup;

public class EntityDeathListener extends InternalEventHandler<EntityDeathEvent> {

    private final boolean loggingDrops;
    private final boolean loggingDeaths;

    public EntityDeathListener(InsightConfigurationContext configurationContext) {
        loggingDrops = configurationContext.isLoggingDrops();
        loggingDeaths = configurationContext.isLoggingDeaths();
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(EntityDeathEvent event) {
        EntityDamageEvent previousEvent = event.getEntity().getLastDamageCause();
        Location loc = event.getEntity().getLocation();
        String acteeName = EntityUtil.getName(event.getEntity());

        if (loggingDeaths) {
            if (previousEvent instanceof EntityDamageByEntityEvent) { // player killed
                Entity killingEntity = ((EntityDamageByEntityEvent) previousEvent).getDamager();
                String killerName;

                if (killingEntity instanceof Projectile) {
                    ProjectileSource shooter = ((Projectile) killingEntity).getShooter();
                    if (shooter instanceof LivingEntity) {
                        killerName = EntityUtil.getName((LivingEntity) shooter);
                    } else {
                        killerName = NonPlayerLookup.NATURE;
                    }
                } else if (killingEntity instanceof LightningStrike) {
                    killerName = NonPlayerLookup.NATURE;
                } else {
                    killerName = EntityUtil.getName(killingEntity);
                }

                if (killerName == null) {
                    System.out.println("[???] Bad killer name via path #1, killer is a " + killingEntity.getClass());
                }

                add(new EntityRowEntry(System.currentTimeMillis(), killerName, EventCompat.ENTITY_KILL, loc, acteeName));
            } else if (previousEvent instanceof EntityDamageByBlockEvent) { // environment killed
                EntityDamageByBlockEvent natureKillEvent = (EntityDamageByBlockEvent) previousEvent;
                if (natureKillEvent.getDamager() != null) {
                    if (natureKillEvent.getDamager().getType().name() == null) {
                        System.out.println("[???] Bad killer name via path #2, killer is a " + natureKillEvent.getDamager().getClass());
                    }

                    add(new EntityRowEntry(System.currentTimeMillis(), natureKillEvent.getDamager().getType().name(), EventCompat.ENTITY_KILL, loc, acteeName));
                } else { // death by lava or void
                    add(new EntityRowEntry(System.currentTimeMillis(), acteeName, EventCompat.ENTITY_DEATH, loc, acteeName));
                }
            } else { // died
                add(new EntityRowEntry(System.currentTimeMillis(), acteeName, EventCompat.ENTITY_DEATH, loc, acteeName));
            }
        }

        if (loggingDrops) {
            for (ItemStack stack : event.getDrops()) {
                add(new ItemRowEntry(System.currentTimeMillis(), EntityUtil.getName(event.getEntity()), EventCompat.ITEM_DROP, loc, stack));
            }
        }
    }
}

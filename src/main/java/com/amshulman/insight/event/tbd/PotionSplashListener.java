package com.amshulman.insight.event.tbd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;

import com.amshulman.insight.event.InternalEventHandler;

public class PotionSplashListener extends InternalEventHandler<PotionSplashEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PotionSplashEvent event) {
        // TODO Auto-generated method stub
        System.out.println("PotionSplashListener");
    }
}

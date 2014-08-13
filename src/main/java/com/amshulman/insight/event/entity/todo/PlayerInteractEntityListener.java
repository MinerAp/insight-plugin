package com.amshulman.insight.event.entity.todo;

import org.bukkit.Material;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.material.Dye;

import com.amshulman.insight.event.InternalEventHandler;

public class PlayerInteractEntityListener extends InternalEventHandler<PlayerInteractEntityEvent> {

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void listen(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof PoweredMinecart && Material.COAL.equals(event.getPlayer().getItemInHand().getType())) {
            System.out.println("PlayerInteractEntityListener - powered minecart");
        } else if (event.getPlayer().getItemInHand().getData() instanceof Dye) {
            System.out.println("PlayerInteractEntityListener - dye");
        }
    }
}

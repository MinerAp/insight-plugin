package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityUtil {

    public static String getName(Entity actor) {
        if (actor instanceof Player) {
            return ((Player) actor).getName();
        } else if (actor == null) {
            return NonPlayerLookup.NATURE;
        } else {
            return getName(actor.getType());
        }
    }

    public static String getName(EntityType type) {
        switch (type) {
          case PIG_ZOMBIE:
              return "Zombie Pigman";
          case MUSHROOM_COW:
              return "Mooshroom";
          case PRIMED_TNT:
          case MINECART_TNT:
              return "TNT";
          case EXPERIENCE_ORB:
              return "XP Orb";
          case MINECART_COMMAND:
              return "Command Block Minecart";
          case MINECART_CHEST:
              return "Chest Minecart";
          case MINECART_FURNACE:
              return "Furnace Minecart";
          case MINECART_HOPPER:
              return "Hopper Minecart";
          case MINECART_MOB_SPAWNER:
              return "Mob Spawner Minecart";
          default:
            return WordUtils.capitalizeFully(type.name().replace('_', ' '));
        }
    }
}

package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
            case PRIMED_TNT:
            case MINECART_TNT:
                return "TNT";
            case EXPERIENCE_ORB:
                return "XP Orb";
            default:
                return type.getName();
        }
    }
}

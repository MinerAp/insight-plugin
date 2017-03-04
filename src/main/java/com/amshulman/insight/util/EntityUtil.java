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
            case CREEPER:
                return "Creeper";
            case SKELETON:
                return "Skeleton";
            case SPIDER:
                return "Spider";
            case GIANT:
                return "Giant";
            case ZOMBIE:
                return "Zombie";
            case SLIME:
                return "Slime";
            case GHAST:
                return "Ghast";
            case PIG_ZOMBIE:
                return "Zombie Pigman";
            case ENDERMAN:
                return "Enderman";
            case CAVE_SPIDER:
                return "Cave Spider";
            case SILVERFISH:
                return "Silverfish";
            case BLAZE:
                return "Blaze";
            case MAGMA_CUBE:
                return "Magma Cube";
            case ENDER_DRAGON:
                return "Ender Dragon";
            case WITHER:
                return "Wither";
            case BAT:
                return "Bat";
            case WITCH:
                return "Witch";
            case ENDERMITE:
                return "Endermite";
            case GUARDIAN:
                return "Guardian";
            case SHULKER:
                return "Shulker";
            case PIG:
                return "Pig";
            case SHEEP:
                return "Sheep";
            case COW:
                return "Cow";
            case CHICKEN:
                return "Chicken";
            case SQUID:
                return "Squid";
            case WOLF:
                return "Wolf";
            case MUSHROOM_COW:
                return "Mooshroom";
            case SNOWMAN:
                return "Snowman";
            case OCELOT:
                return "Ocelot";
            case IRON_GOLEM:
                return "Iron Golem";
            case HORSE:
                return "Horse";
            case RABBIT:
                return "Rabbit";
            case POLAR_BEAR:
                return "Polar Bear";
            case VILLAGER:
                return "Villager";
            case ENDER_CRYSTAL:
                return "Ender Crystal";
            case PRIMED_TNT:
            case MINECART_TNT:
                return "TNT";
            case EXPERIENCE_ORB:
                return "XP Orb";
            case FALLING_BLOCK:
                return "Falling Block";
            default:
                System.out.println("EntityUtil - ??? " + type.name());
                return type.getName();
        }
    }
}

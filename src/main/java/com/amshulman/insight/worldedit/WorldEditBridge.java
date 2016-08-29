package com.amshulman.insight.worldedit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.amshulman.insight.query.QueryParameterBuilder;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class WorldEditBridge {

    private static final WorldEditPlugin worldEditPlugin;

    static {
        WorldEditPlugin plugin = JavaPlugin.getPlugin(WorldEditPlugin.class);
        if (plugin != null && plugin.isEnabled()) {
            worldEditPlugin = plugin;
        } else {
            worldEditPlugin = null;
        }
    }

    public static boolean isWorldEditEnabled() {
        return worldEditPlugin != null;
    }

    public static boolean getSelectedArea(Player player, QueryParameterBuilder params) {
        Region region = null;

        try {
            LocalSession ls = worldEditPlugin.getSession(player);
            if (!ls.isSelectionDefined(ls.getSelectionWorld())) {
                return false;
            }
            region = ls.getSelection(ls.getSelectionWorld());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
            return false;
        }

        params.setArea(region.getWorld().getName(),
                       region.getMinimumPoint().getBlockX(), region.getMaximumPoint().getBlockX(),
                       region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY(),
                       region.getMinimumPoint().getBlockZ(), region.getMaximumPoint().getBlockZ());

        return true;
    }
}

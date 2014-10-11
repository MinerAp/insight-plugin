package com.amshulman.insight.command.insight;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.insight.backend.RollbackReadBackend;
import com.amshulman.insight.parser.QueryParser;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.types.InsightLocation;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.worldedit.WorldEditBridge;
import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightRollback extends PlayerOnlyCommand {

    private final RollbackReadBackend readBackend;
    private final boolean worldEditEnabled;

    public CommandInsightRollback(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.ROLLBACK, 1, Integer.MAX_VALUE);
        readBackend = new RollbackReadBackend(configurationContext);
        worldEditEnabled = configurationContext.isWorldEditEnabled();
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        QueryParameters queryParams = QueryUtil.parseArgs(player, args);
        if (queryParams == null) {
            return true;
        }

        QueryParameterBuilder queryBuilder = QueryUtil.copyCommonParameters(queryParams, readBackend.newQueryBuilder());

        if (queryParams.isLocationSet()) {
            if (queryParams.getRadius() == QueryParameters.WORLDEDIT) {
                if (!worldEditEnabled) {
                    player.sendMessage(ChatColor.RED + "WorldEdit is not loaded");
                    return true;
                }

                boolean success = WorldEditBridge.getSelectedArea(player, queryBuilder);
                if (!success) {
                    player.sendMessage(ChatColor.RED + "Error getting selection");
                    return true;
                }
            } else {
                Location loc = player.getLocation();
                queryBuilder.setArea(new InsightLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()), queryParams.getRadius());
            }
        } else {
            if (queryParams.getWorlds().isEmpty()) {
                for (String world : QueryParser.getWorlds()) {
                    queryBuilder.addWorld(world);
                }
            } else {
                QueryUtil.copyWorlds(queryParams, queryBuilder);
            }
        }

        readBackend.rollback(player.getName(), queryBuilder.build(), true);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}

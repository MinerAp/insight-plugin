package com.amshulman.insight.command.insight;

import org.antlr.v4.runtime.RecognitionException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.amshulman.insight.backend.PlayerCallbackReadBackend;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.worldedit.WorldEditBridge;
import com.amshulman.mbapi.commands.ConsoleOrPlayerCommand;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightLookup extends ConsoleOrPlayerCommand {

    private final PlayerCallbackReadBackend readBackend;
    private final boolean worldEditEnabled;

    public CommandInsightLookup(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.LOOKUP, 1, Integer.MAX_VALUE);
        readBackend = configurationContext.getReadBackend();
        worldEditEnabled = configurationContext.isWorldEditEnabled();
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) throws RecognitionException {
        QueryParameters lookupQueryParams = QueryUtil.parseArgs(player, args);
        if (lookupQueryParams == null) {
            return true;
        }

        QueryParameterBuilder queryBuilder = readBackend.newQueryBuilder();
        QueryUtil.copyActors(lookupQueryParams, queryBuilder);
        QueryUtil.copyActions(lookupQueryParams, queryBuilder);
        QueryUtil.copyActees(lookupQueryParams, queryBuilder);
        QueryUtil.copyMaterials(lookupQueryParams, queryBuilder);
        QueryUtil.copyOrder(lookupQueryParams, queryBuilder);
        QueryUtil.copyTimes(lookupQueryParams, queryBuilder);

        if (lookupQueryParams.isLocationSet()) {
            if (lookupQueryParams.getRadius() == QueryParameters.WORLDEDIT) {
                if (!worldEditEnabled) {
                    player.sendMessage(ChatColor.RED + "WorldEdit is not loaded");
                    return true;
                }

                boolean success = WorldEditBridge.getSelectedArea(player, queryBuilder);
                if (!success) {
                    player.sendMessage(ChatColor.RED + "Error getting selection.");
                    return true;
                }
            } else {
                queryBuilder.setArea(player.getLocation(), lookupQueryParams.getRadius());
            }
        } else {
            if (lookupQueryParams.getWorlds().isEmpty()) {
                queryBuilder.addWorld(player.getWorld().getName());
            } else {
                QueryUtil.copyWorlds(lookupQueryParams, queryBuilder);
            }
        }

        readBackend.query(player.getName(), queryBuilder.build(), true);
        return true;
    }

    @Override
    protected boolean executeForConsole(ConsoleCommandSender console, TypeSafeList<String> args) throws RecognitionException {
        QueryParameters params = QueryUtil.parseArgs(console, args);
        if (params == null) {
            return true;
        }

        if (params.isLocationSet()) {
            console.sendMessage(ChatColor.RED + "You may not specify a location from the console");
            return true;
        }

        if (params.getWorlds().isEmpty()) {
            console.sendMessage(ChatColor.RED + "You must specify at least one world to query");
            return true;
        }

        QueryParameterBuilder queryBuilder = readBackend.newQueryBuilder();
        QueryUtil.copyActors(params, queryBuilder);
        QueryUtil.copyActions(params, queryBuilder);
        QueryUtil.copyActees(params, queryBuilder);
        QueryUtil.copyMaterials(params, queryBuilder);
        QueryUtil.copyOrder(params, queryBuilder);
        QueryUtil.copyTimes(params, queryBuilder);
        QueryUtil.copyWorlds(params, queryBuilder); // Different from above

        readBackend.query(console.getName(), queryBuilder.build(), true);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return QueryUtil.tabComplete(args);
    }
}

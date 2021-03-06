package com.amshulman.insight.command.insight;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.amshulman.insight.backend.QueryReadBackend;
import com.amshulman.insight.parser.QueryParser;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.types.InsightLocation;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.worldedit.WorldEditBridge;
import com.amshulman.mbapi.commands.ConsoleOrPlayerCommand;
import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;

public class CommandInsightLookup extends ConsoleOrPlayerCommand {

    private final QueryReadBackend readBackend;
    private final boolean worldEditEnabled;

    public CommandInsightLookup(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.LOOKUP, 1, Integer.MAX_VALUE);
        readBackend = new QueryReadBackend(configurationContext);
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

        readBackend.query(player.getName(), queryBuilder.build(), true);
        return true;
    }

    @Override
    protected boolean executeForConsole(ConsoleCommandSender console, TypeSafeList<String> args) {
        QueryParameters queryParams = QueryUtil.parseArgs(console, args);
        if (queryParams == null) {
            return true;
        }

        if (queryParams.isLocationSet()) {
            console.sendMessage(ChatColor.RED + "You may not specify a location from the console");
            return true;
        }

        QueryParameterBuilder queryBuilder = QueryUtil.copyCommonParameters(queryParams, readBackend.newQueryBuilder());

        if (queryParams.getWorlds().isEmpty()) {
            for (String world : QueryParser.getWorlds()) {
                queryBuilder.addWorld(world);
            }
        } else {
            QueryUtil.copyWorlds(queryParams, queryBuilder);
        }

        readBackend.query(console.getName(), queryBuilder.build(), false);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        TypeSafeList<String> temp = TypeSafeCollections.emptyList(); // TODO

        for (int i = args.size() - 2; i >= 0; --i) {
            String arg = args.get(i).toLowerCase();
            switch (arg) {
                case "actor":
                case "actee":
                    return tabCompleteFromList(args.get(i + 1), temp);
                case "action":
                    return tabCompleteFromList(args.get(i + 1), temp);
                case "material":
                    return tabCompleteFromList(args.get(i + 1), temp);
                case "world":
                    return tabCompleteFromList(args.get(i + 1), new TypeSafeSetImpl<String>(QueryParser.getWorlds(), CoreTypes.STRING));
                case "radius":
                case "before":
                case "after":
                    return TypeSafeCollections.emptyList();
                default:
                    continue;
            }
        }

        return TypeSafeCollections.emptyList();
    }
}

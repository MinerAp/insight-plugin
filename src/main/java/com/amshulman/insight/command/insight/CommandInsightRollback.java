package com.amshulman.insight.command.insight;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.insight.backend.RollbackReadBackend;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
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

        // TODO

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}

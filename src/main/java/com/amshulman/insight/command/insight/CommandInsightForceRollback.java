package com.amshulman.insight.command.insight;

import org.bukkit.entity.Player;

import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.typesafety.TypeSafeList;

public final class CommandInsightForceRollback extends CommandInsightRollback {

    public CommandInsightForceRollback(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.FORCEROLLBACK);
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        return rollback(player, args, true);
    }
}

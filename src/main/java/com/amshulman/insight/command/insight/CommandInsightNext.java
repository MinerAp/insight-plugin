package com.amshulman.insight.command.insight;

import org.bukkit.command.CommandSender;

import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.results.ResultSetFormatter;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.PlayerInfo;
import com.amshulman.mbapi.commands.ConsoleAndPlayerCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightNext extends ConsoleAndPlayerCommand {

    private final PlayerInfoManager infoManager;

    public CommandInsightNext(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.NEXT, 0, 0);

        infoManager = configurationContext.getInfoManager();
    }

    @Override
    protected boolean execute(CommandSender sender, TypeSafeList<String> args) {
        PlayerInfo playerInfo = infoManager.getPlayerInfo(sender.getName());
        if (playerInfo.getLastResults() == null) {
            sender.sendMessage("no cached query");
            return true;
        }

        playerInfo.setLastRequestedPage(playerInfo.getLastRequestedPage() + 1);
        ResultSetFormatter resultFormatter = new ResultSetFormatter(playerInfo.getLastResults(), playerInfo.getLastRequestedPage());
        resultFormatter.sendResults(sender);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}

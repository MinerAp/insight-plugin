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

public class CommandInsightPage extends ConsoleAndPlayerCommand {

    private final PlayerInfoManager infoManager;

    public CommandInsightPage(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.PAGE, 1, 1);

        infoManager = configurationContext.getInfoManager();
    }

    @Override
    protected boolean execute(CommandSender sender, TypeSafeList<String> args) {
        PlayerInfo playerInfo = infoManager.getPlayerInfo(sender.getName());
        if (playerInfo.getLastResults() == null) {
            sender.sendMessage("no cached query");
            return true;
        }

        int pageNum;
        try {
            pageNum = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            sender.sendMessage("not a number");
            return true;
        }

        playerInfo.setLastRequestedPage(pageNum);
        ResultSetFormatter resultFormatter = new ResultSetFormatter(playerInfo.getLastResults(), pageNum);
        resultFormatter.sendResults(sender);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}

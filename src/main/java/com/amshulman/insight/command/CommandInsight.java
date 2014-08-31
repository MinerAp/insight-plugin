package com.amshulman.insight.command;

import com.amshulman.insight.command.insight.CommandInsightLookup;
import com.amshulman.insight.command.insight.CommandInsightNext;
import com.amshulman.insight.command.insight.CommandInsightPage;
import com.amshulman.insight.command.insight.CommandInsightPrev;
import com.amshulman.insight.command.insight.CommandInsightTp;
import com.amshulman.insight.command.insight.CommandInsightWand;
import com.amshulman.insight.util.Commands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.mbapi.commands.DelegatingCommand;

public class CommandInsight extends DelegatingCommand {

    public CommandInsight(InsightConfigurationContext configurationContext) {
        super(configurationContext, Commands.INSIGHT, 0, Integer.MAX_VALUE);

        registerSubcommand(new CommandInsightLookup(configurationContext), "l");
        registerSubcommand(new CommandInsightNext(configurationContext));
        registerSubcommand(new CommandInsightPage(configurationContext), "pg");
        registerSubcommand(new CommandInsightPrev(configurationContext));
        registerSubcommand(new CommandInsightTp(configurationContext));
        registerSubcommand(new CommandInsightWand(configurationContext), "w");
    }
}

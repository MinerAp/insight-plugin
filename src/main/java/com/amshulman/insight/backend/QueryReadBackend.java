package com.amshulman.insight.backend;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.results.InsightResultSet;
import com.amshulman.insight.results.ResultSetFormatter;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.PlayerInfo;
import com.amshulman.mbapi.MbapiPlugin;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QueryReadBackend extends AbstractCallbackReadBackend {

    static String UNKNOWN_ERROR = "An unknown error occured. Please report this.";

    MbapiPlugin plugin;
    PlayerInfoManager infoManager;

    public QueryReadBackend(InsightConfigurationContext configurationContext) {
        super(configurationContext.getReadBackend());
        plugin = configurationContext.plugin;
        infoManager = configurationContext.getInfoManager();
    }

    public void query(final String playerName, final QueryParameters params, final boolean fancyResults) {
        try {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    InsightResultSet rs = null;
                    try {
                        rs = readBackend.submit(params);
                    } catch (IllegalArgumentException e) {
                        String msg;
                        if (e == null || e.getMessage() == null) {
                            msg = UNKNOWN_ERROR;
                        } else {
                            msg = e.getMessage();
                        }

                        reportError(playerName, msg);
                        return;
                    }

                    if (rs == null) {
                        reportError(playerName, UNKNOWN_ERROR);
                        return;
                    }

                    reportResults(playerName, new ResultSetFormatter(rs, 1));
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
            reportError(playerName, UNKNOWN_ERROR);
        }
    }

    private void reportError(final String playerName, String error) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                CommandSender sender = getSender(playerName);
                if (sender == null) {
                    return;
                }
                sender.sendMessage(ChatColor.RED + error);
            }
        });
    }

    private void reportResults(final String playerName, ResultSetFormatter resultFormatter) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                CommandSender sender = getSender(playerName);
                if (sender == null) {
                    return;
                }

                PlayerInfo playerInfo = infoManager.getPlayerInfo(playerName);
                playerInfo.setLastResults(resultFormatter.getResultSet());
                playerInfo.setLastRequestedPage(1);

                resultFormatter.sendResults(sender);
            }
        });
    }

    private static CommandSender getSender(String playerName) {
        CommandSender sender = Bukkit.getPlayer(playerName);
        if (sender == null && playerName.equals(Bukkit.getConsoleSender().getName())) {
            sender = Bukkit.getConsoleSender();
        }
        return sender;
    }
}

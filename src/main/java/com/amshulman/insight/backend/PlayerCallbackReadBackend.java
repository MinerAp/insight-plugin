package com.amshulman.insight.backend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.amshulman.insight.InsightPlugin;
import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.results.InsightResultSet;
import com.amshulman.insight.results.ResultSetFormatter;
import com.amshulman.insight.util.PlayerInfo;
import com.amshulman.insight.util.craftbukkit.PlayerUtil;
import com.amshulman.mbapi.MbapiPlugin;

@RequiredArgsConstructor
public class PlayerCallbackReadBackend implements AutoCloseable {

    private final MbapiPlugin plugin = JavaPlugin.getPlugin(InsightPlugin.class);
    private final ExecutorService threadPool = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));

    private final ReadBackend readBackend;
    private final PlayerInfoManager infoManager;

    public void query(final String playerName, final QueryParameters params, final boolean fancyResults) {
        try {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    InsightResultSet rs = null;
                    try {
                        rs = readBackend.submit(params);
                    } catch (IllegalArgumentException e) {
                        reportResults(playerName, null, ResultSetFormatter.formatError(e, fancyResults), fancyResults);
                        return;
                    }

                    if (rs == null) {
                        reportResults(playerName, null, ResultSetFormatter.formatError(null, fancyResults), fancyResults);
                        return;
                    }

                    reportResults(playerName, rs, ResultSetFormatter.formatResults(rs, 1, fancyResults), fancyResults);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();

            Bukkit.getScheduler().runTask(plugin, new Runnable() {

                @Override
                public void run() {
                    CommandSender sender = Bukkit.getPlayer(playerName);

                    if (sender == null) {
                        if (playerName.equals(Bukkit.getConsoleSender().getName())) {
                            sender = Bukkit.getConsoleSender();
                        } else {
                            return;
                        }
                    }

                    sender.sendMessage(ChatColor.RED + "Error running query, please check console");
                }
            });
        }
    }

    private void reportResults(final String playerName, final InsightResultSet rs, final String[] messages, final boolean fancyResults) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                CommandSender sender = Bukkit.getPlayer(playerName);

                if (sender == null) {
                    if (playerName.equals(Bukkit.getConsoleSender().getName())) {
                        sender = Bukkit.getConsoleSender();
                    } else {
                        return;
                    }
                }

                if (rs != null) {
                    PlayerInfo playerInfo = infoManager.getPlayerInfo(playerName);
                    playerInfo.setLastResults(rs);
                    playerInfo.setLastRequestedPage(1);
                }

                if (fancyResults) {
                    PlayerUtil.getInstance().sendRawMessages((Player) sender, messages);
                } else {
                    sender.sendMessage(messages);
                }
            }
        });
    }

    public QueryParameterBuilder newQueryBuilder() {
        return readBackend.newQueryBuilder();
    }

    @Override
    public void close() {
        readBackend.close();
    }
}

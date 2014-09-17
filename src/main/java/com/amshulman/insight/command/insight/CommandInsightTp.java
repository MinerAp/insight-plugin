package com.amshulman.insight.command.insight;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.insight.action.BlockAction;
import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.results.InsightRecord;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.PlayerInfo;
import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.mbapi.util.LocationUtil;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightTp extends PlayerOnlyCommand {

    private final PlayerInfoManager infoManager;

    public CommandInsightTp(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.TP, 1, 1);

        infoManager = configurationContext.getInfoManager();
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        PlayerInfo playerInfo = infoManager.getPlayerInfo(player.getName());
        if (playerInfo.getLastResults() == null) {
            player.sendMessage("no cached query");
            return true;
        }

        int recordNum;
        try {
            recordNum = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            player.sendMessage("not a number");
            return true;
        }

        if (recordNum <= 0 || playerInfo.getLastResults().getSize() < recordNum) {
            player.sendMessage("record number out of bounds");
            return true;
        }

        InsightRecord record = playerInfo.getLastResults().getRecord(recordNum - 1);

        int heightAdjustment;
        float yaw, pitch;
        if (record.getAction() instanceof BlockAction) {
            heightAdjustment = 1;
            yaw = 180;
            pitch = 90;
        } else {
            heightAdjustment = 0;
            yaw = 180;
            pitch = 0;
        }

        player.teleport(LocationUtil.center(Bukkit.getWorld(record.getWorld()), record.getX(), record.getY() + heightAdjustment, record.getZ(), yaw, pitch));
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}

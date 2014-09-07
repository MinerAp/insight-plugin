package com.amshulman.insight.command.insight;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.query.QueryParameterBuilder;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.PlayerInfo;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.util.WandUtil;
import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightWand extends PlayerOnlyCommand {

    private final PlayerInfoManager infoManager;

    public CommandInsightWand(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.WAND, 0, Integer.MAX_VALUE);
        infoManager = configurationContext.getInfoManager();
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        PlayerInfo playerInfo = infoManager.getPlayerInfo(player.getName());
        PlayerInventory inv = player.getInventory();
        ItemStack inHand = inv.getItemInHand();

        boolean hasWands = false;
        ItemStack[] contents = inv.getContents();
        for (int i = contents.length - 1; i >= 0; --i) {
            if (WandUtil.isWand(contents[i])) {
                inv.setItem(i, null);
                hasWands = true;
            }
        }

        if (hasWands) {
            playerInfo.setWandQueryParameters(new QueryParameterBuilder().build());
            return true;
        }

        if (args.size() > 0) {
            QueryParameters params = QueryUtil.parseArgs(player, args);
            if (params == null) {
                return true;
            }

            if (!params.getWorlds().isEmpty()) {
                player.sendMessage(ChatColor.RED + "You cannot specify the world as a wand parameter");
                return true;
            }

            playerInfo.setWandQueryParameters(params);
        }

        if (!Material.AIR.equals(inHand.getType())) {
            int emptySlot = inv.firstEmpty();
            if (emptySlot == -1) {
                player.sendMessage(ChatColor.GRAY + "Your inventory is full. Please make room for the wand.");
                return true;
            }
            inv.setItem(emptySlot, inHand);
        }

        inv.setItemInHand(WandUtil.getWand());
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

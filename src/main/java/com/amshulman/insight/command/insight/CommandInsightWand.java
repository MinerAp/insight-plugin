package com.amshulman.insight.command.insight;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.amshulman.insight.util.Commands.InsightCommands;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.insight.util.QueryUtil;
import com.amshulman.insight.util.WandUtil;
import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeList;

public class CommandInsightWand extends PlayerOnlyCommand {

    public CommandInsightWand(InsightConfigurationContext configurationContext) {
        super(configurationContext, InsightCommands.WAND, 0, 0);
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
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
            return true;
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
        return QueryUtil.tabComplete(args);
    }
}

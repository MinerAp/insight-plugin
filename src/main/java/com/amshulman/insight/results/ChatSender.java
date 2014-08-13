package com.amshulman.insight.results;

public class ChatSender {

    public static void sendRawMessage(org.bukkit.entity.Player player, String[] messages) {
        for (String message : messages) {
            com.amshulman.insight.util.craftbukkit.Player.getInstance().sendRawMessage(player, message);
        }
    }
}

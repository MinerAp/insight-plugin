package com.amshulman.insight.results;

import org.bukkit.ChatColor;

@SuppressWarnings("unused") class ChatMessage extends ChatRootMessage {

    private String color = null;
    private ChatHover hoverEvent = null;

    ChatMessage(String msg) {
        super(msg);
    }

    ChatMessage setHoverEvent(ChatHover hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }
    
    ChatMessage setColor(ChatColor color) {
        this.color = color.name().toLowerCase();
        return this;
    }
}

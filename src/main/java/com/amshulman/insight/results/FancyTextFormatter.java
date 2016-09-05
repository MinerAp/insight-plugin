package com.amshulman.insight.results;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.amshulman.insight.results.UnformattedResults.Message;
import com.amshulman.insight.results.UnformattedResults.ResultRow;
import com.amshulman.insight.util.craftbukkit.PlayerUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

final class FancyTextFormatter {

    private static final Gson GSON = ChatItemHover.registerTypeAdapter(new GsonBuilder()).create();

    static void send(Player player, UnformattedResults results) {
        String[] messages = new String[results.getResultRows().size() + 1];

        // Format the header message. If there is an error, use that instead.
        ChatRootMessage header = new ChatRootMessage();
        if (results.getHeaderRow().getError() == null) {
            header.addMessage(convert(results.getHeaderRow().getExamineMessage(), null));
            header.addMessage(convert(results.getHeaderRow().getNumWorldsMessage(), results.getHeaderRow().getWorldsHoverMessage()));
            header.addMessage(convert(results.getHeaderRow().getPagesMessage(), null));
        } else {
            header.addMessage(convert(results.getHeaderRow().getError(), null));
        }
        messages[0] = GSON.toJson(header);

        // Format each result row.
        int currentRow = 1;
        for (ResultRow row : results.getResultRows()) {
            ChatRootMessage resultRow = new ChatRootMessage();

            // Append the row number and location, if present.
            resultRow.addMessage(convert(row.getRowNumber(), getLocationHoverFromMessage(row.getLocation())));

            // Append the date and time.
            resultRow.addMessage(new ChatMessage(" "));
            resultRow.addMessage(convert(row.getDatetime(), null));

            // Append the actor.
            resultRow.addMessage(new ChatMessage(" "));
            resultRow.addMessage(convert(row.getActor(), null));

            // Append the action.
            resultRow.addMessage(new ChatMessage(" "));
            resultRow.addMessage(convert(row.getAction(), null));

            // If present, append the actee/material and the hover text.
            if (row.getActeeOrMaterial() != null) {
                resultRow.addMessage(new ChatMessage(" "));
                resultRow.addMessage(convert(row.getActeeOrMaterial(), row.getActeeOrMaterialHover()));
            }

            messages[currentRow] = GSON.toJson(resultRow);
            ++currentRow;
        }

        // Send the raw JSON to the player.
        PlayerUtil.getInstance().sendRawMessages(player, messages);
    }

    private static ChatHover getLocationHoverFromMessage(Message message) {
        if (message == null) {
            return null;
        }
        return new ChatTextHover(message.getMessage());
    }

    private static ChatMessage convert(String message, String hover) {
        ChatMessage serializableMessage = new ChatMessage(message);
        if (hover != null) {
            serializableMessage.setHoverEvent(new ChatTextHover(hover));
        }
        return serializableMessage;
    }

    private static ChatMessage convert(Message message, ChatHover hover) {
        ChatMessage serializableMessage = new ChatMessage(message.getMessage());
        if (message.getColor() != null) {
            serializableMessage.setColor(message.getColor());
        }
        if (hover != null) {
            serializableMessage.setHoverEvent(hover);
        }
        return serializableMessage;
    }

    @SuppressWarnings("unused")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class ChatRootMessage {

        private final String text = "";
        private List<ChatMessage> extra = null;

        void addMessage(ChatMessage message) {
            if (extra == null) {
                extra = new LinkedList<ChatMessage>();
            }
            extra.add(message);
        }
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    private static final class ChatMessage {
        final String text;
        private String color = null;
        private ChatHover hoverEvent = null;

        void setHoverEvent(ChatHover hoverEvent) {
            this.hoverEvent = hoverEvent;
        }

        void setColor(ChatColor color) {
            this.color = color.name().toLowerCase();
        }
    }
}

package com.amshulman.insight.results;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.amshulman.insight.results.UnformattedResults.Message;
import com.amshulman.insight.results.UnformattedResults.ResultRow;

final class PlainTextFormatter {
    static void send(CommandSender commandSender, UnformattedResults results) {
        String[] messages = new String[results.getResultRows().size() + 1];

        StringBuilder headerRowBuilder = new StringBuilder(200);
        if (results.getHeaderRow().getError() == null) {
            headerRowBuilder.append(results.getHeaderRow().getExamineMessage());

            // If there is hover text, use that rather than the number of worlds.
            if (results.getHeaderRow().getWorldsHoverMessage() != null) {
                headerRowBuilder.append(results.getHeaderRow().getWorldsHoverMessage());
            } else {
                headerRowBuilder.append(results.getHeaderRow().getNumWorldsMessage());
            }

            headerRowBuilder.append(results.getHeaderRow().getPagesMessage());
        } else {
            append(headerRowBuilder, results.getHeaderRow().getError());
        }
        messages[0] = headerRowBuilder.toString();

        int currentRow = 1;
        for (ResultRow row : results.getResultRows()) {
            StringBuilder resultRowBuilder = new StringBuilder(200);

            // Append the row number.
            append(resultRowBuilder, row.getRowNumber());

            // Append the date and time.
            resultRowBuilder.append(' ');
            append(resultRowBuilder, row.getDatetime());

            // Append the actor.
            resultRowBuilder.append(' ');
            append(resultRowBuilder, row.getActor());

            // Append the action.
            resultRowBuilder.append(' ');
            append(resultRowBuilder, row.getAction());

            // If present, append the actee/material.
            if (row.getActeeOrMaterial() != null) {
                resultRowBuilder.append(' ');
                append(resultRowBuilder, row.getActeeOrMaterial());
            }

            // Append the location.
            if (row.getLocation() != null) {
                resultRowBuilder.append(' ');
                append(resultRowBuilder, row.getLocation());
            }

            messages[currentRow] = resultRowBuilder.toString();
            ++currentRow;
        }

        // Send the colorized text messages to the user.
        commandSender.sendMessage(messages);
    }

    private static ChatColor getColor(Message message) {
        return message.getColor() == null ? ChatColor.WHITE : message.getColor();
    }

    private static void append(StringBuilder sb, Message message) {
        sb.append(getColor(message));
        sb.append(message.getMessage());
    }
}

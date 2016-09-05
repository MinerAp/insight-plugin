package com.amshulman.insight.results;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class UnformattedResults {
    HeaderRow headerRow = new HeaderRow();
    List<ResultRow> resultRows = new ArrayList<ResultRow>();

    ResultRow addResultRow() {
        ResultRow row = new ResultRow();
        resultRows.add(row);
        return row;
    }

    @Getter(value = AccessLevel.PACKAGE)
    @Setter(value = AccessLevel.PACKAGE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    static final class Message {
        final String message;
        ChatColor color = null;
    }

    @Getter(value = AccessLevel.PACKAGE)
    @Setter(value = AccessLevel.PACKAGE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static final class HeaderRow {
        String examineMessage;
        String numWorldsMessage;
        String worldsHoverMessage;
        String pagesMessage;

        Message error;
        Message setError(String errorMessage) {
            return this.error = new Message(errorMessage);
        }
    }

    @Getter(value = AccessLevel.PACKAGE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static final class ResultRow {
        Message rowNumber;
        Message location;
        Message datetime;
        Message actor;
        Message action;
        Message acteeOrMaterial;
        ChatHover acteeOrMaterialHover;

        Message setRowNumber(String rowNumber) {
            return this.rowNumber = new Message(rowNumber);
        }

        Message setLocation(String location) {
            return this.location = new Message(location);
        }

        Message setDatetime(String datetime) {
            return this.datetime = new Message(datetime);
        }

        Message setActor(String actor) {
            return this.actor = new Message(actor);
        }

        Message setAction(String action) {
            return this.action = new Message(action);
        }

        Message setActeeOrMaterial(String acteeOrMaterial) {
            return this.acteeOrMaterial = new Message(acteeOrMaterial);
        }

        void setActeeOrMaterialHover(ChatHover acteeOrMaterialHover) {
            this.acteeOrMaterialHover = acteeOrMaterialHover;
        }
    }
}

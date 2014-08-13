package com.amshulman.insight.results;

import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("unused")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) class ChatRootMessage {

    final String text;
    List<ChatMessage> extra = null;

    public ChatRootMessage() {
        text = "";
    }
    
    ChatRootMessage addMessage(ChatMessage message) {
        if (extra == null) {
            extra = new LinkedList<ChatMessage>();
        }

        extra.add(message);
        return this;
    }
}

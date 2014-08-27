package com.amshulman.insight.results;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE) class ChatTextHover extends ChatHover {

    ChatTextHover(ChatRootMessage chatRootMessage) {
        super(HoverEventType.show_text, chatRootMessage);
    }
}

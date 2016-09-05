package com.amshulman.insight.results;

final class ChatTextHover extends ChatHover {

    ChatTextHover(String message) {
        super(HoverEventType.show_text, message);
    }
}

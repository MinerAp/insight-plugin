package com.amshulman.insight.results;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true)
abstract class ChatHover {

    private HoverEventType action;
    protected Object value;

    protected enum HoverEventType {
        show_item,
        show_text
    }
}

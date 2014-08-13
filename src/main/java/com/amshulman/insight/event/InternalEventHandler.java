package com.amshulman.insight.event;

import lombok.Setter;

import org.bukkit.event.Event;

import com.amshulman.insight.InsightPlugin;
import com.amshulman.insight.event.BaseEventHandler;

public abstract class InternalEventHandler<T extends Event> extends BaseEventHandler<T> {

    @Setter private static InsightPlugin plugin;

    public InternalEventHandler() {
        super(plugin);
    }
}

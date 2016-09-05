package com.amshulman.insight.results;

import java.lang.reflect.Type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.amshulman.insight.util.craftbukkit.ItemStackUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

final class ChatItemHover extends ChatHover {

    ChatItemHover(Material mat) {
        this(mat, (short) 0);
    }

    ChatItemHover(Material mat, short damage) {
        super(HoverEventType.show_item, new ItemStack(mat, 1, damage));
    }

    void setMetadata(ItemMeta meta) {
        ((ItemStack) value).setItemMeta(meta);
    }

    private static final class ItemHolderTypeAdapter implements JsonSerializer<ItemStack> {

        @Override
        public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(ItemStackUtil.getInstance().serializeItemAsJson(src));
        }
    }

    static GsonBuilder registerTypeAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<ItemStack>() {}.getType(), new ItemHolderTypeAdapter());
        return builder;
    }
}

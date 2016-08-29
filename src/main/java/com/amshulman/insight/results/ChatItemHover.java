package com.amshulman.insight.results;

import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
        super(HoverEventType.show_item, new ItemHolder(mat));
    }

    public void setDamage(short damage) {
        ((ItemHolder) value).damage = damage;
    }

    public void setMetadata(ItemMeta meta) {
        ((ItemHolder) value).itemMeta = meta;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    private static class ItemHolder {

        final Material material;
        short damage = 0;
        ItemMeta itemMeta = null;
    }

    private static final class ItemHolderTypeAdapter implements JsonSerializer<ItemHolder> {

        @Override
        public JsonElement serialize(ItemHolder src, Type typeOfSrc, JsonSerializationContext context) {
            ItemStack itemStack = new ItemStack(src.material, 1, src.damage);
            itemStack.setItemMeta(src.itemMeta);
            return new JsonPrimitive(ItemStackUtil.getInstance().serializeItemAsJson(itemStack));
        }
    }

    static GsonBuilder registerTypeAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<ItemHolder>() {}.getType(), new ItemHolderTypeAdapter());
        return builder;
    }
}

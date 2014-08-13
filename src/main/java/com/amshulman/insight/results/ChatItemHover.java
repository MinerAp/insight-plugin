package com.amshulman.insight.results;

import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.amshulman.insight.util.craftbukkit.NMSItemStack;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

final class ChatItemHover extends ChatHover {

    ChatItemHover(Material mat) {
        super(HoverEventType.show_item, new ItemHolder(mat.getId()));
    }

    public void setDamage(short damage) {
        ((ItemHolder) value).Damage = damage;
    }

    public void setMetadata(ItemMeta meta) {
        ((ItemHolder) value).tag = meta;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    private static class ItemHolder {

        final int id;
        Short Damage = null;
        ItemMeta tag = null;
    }

    private static final class ItemHolderTypeAdapter implements JsonSerializer<ItemHolder> {

        @Override
        public JsonElement serialize(ItemHolder src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", src.id);

            if (src.Damage != null) {
                obj.addProperty("Damage", src.Damage);
            }

            if (src.tag != null) {
                ItemStack stack = new ItemStack(src.id, 1, src.Damage == null ? 0 : src.Damage);
                stack.setItemMeta(src.tag);
                String tag = NMSItemStack.getInstance().getTag(stack);
                if (tag != null) {
                    obj.addProperty("tag", tag);
                }
            }

            return new JsonPrimitive(forceTagAsObject(thisAbusesTheJsonSpec(obj.toString())));
        }

        private static String forceTagAsObject(String obj) {
            return obj.replaceAll("tag:\"(\\{.*?\\})(?<!\\\\)\"", "tag:$1").replaceAll("\\\\\"", "\"");
        }

        private static String thisAbusesTheJsonSpec(String str) {
            return str.replaceAll("\"([\\w]+)\":", "$1:");
        }
    }

    static GsonBuilder registerTypeAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(new TypeToken<ItemHolder>() {}.getType(), new ItemHolderTypeAdapter());
        return builder;
    }
}

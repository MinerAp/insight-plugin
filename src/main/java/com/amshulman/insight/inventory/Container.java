package com.amshulman.insight.inventory;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class Container implements Iterable<ItemStack>, Cloneable {

    Map<ItemStack, ItemStack> items;
    @Getter long timeOpened;
    @Getter Location location;

    Container(Inventory inv) {
        this((Iterable<ItemStack>) inv);
    }

    Container(ItemStack... inv) {
        this(Arrays.asList(inv));
    }

    private Container(Iterable<ItemStack> inv) {
        this(0L, null);
        Map<Material, Integer> foo = new EnumMap<>(Material.class);

        for (ItemStack stack : inv) {
            if (stack == null || Material.AIR.equals(stack.getType())) {
                continue;
            }

            if (stack.hasItemMeta() || stack.getDurability() != 0) {
                add(stack.clone());
                continue;
            }

            Material type = stack.getType();
            int count = foo.containsKey(type) ? foo.get(type) : 0;
            foo.put(type, count + stack.getAmount());
        }

        for (Entry<Material, Integer> e : foo.entrySet()) {
            add(new ItemStack(e.getKey(), e.getValue()));
        }
    }

    Container(long timeOpened, Location location) {
        this.items = new TCustomHashMap<>(ITEM_STACK_SIMILARITY);
        this.timeOpened = timeOpened;
        this.location = location;
    }

    private Container(Map<ItemStack, ItemStack> items, long timeOpened, Location loc) {
        this.items = new TCustomHashMap<>(ITEM_STACK_SIMILARITY, items);
        this.timeOpened = timeOpened;
        this.location = loc;
    }

    void add(ItemStack stack) {
        items.put(stack, stack);
    }

    ItemStack remove(ItemStack stack) {
        return items.remove(stack);
    }

    ItemStack get(ItemStack stack) {
        return items.get(stack);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return items.keySet().iterator();
    }

    @Override
    public Container clone() {
        return new Container(items, timeOpened, location);
    }
    
    @Override
    public String toString() {
        return items.keySet().toString();
    }

    private static HashingStrategy<ItemStack> ITEM_STACK_SIMILARITY = new HashingStrategy<ItemStack>() {

        private static final long serialVersionUID = -1650877666265419401L;

        @Override
        public int computeHashCode(ItemStack object) {
            int hash = 1;

            hash = hash * 31 + object.getTypeId();
            hash = hash * 31 + (object.getDurability() & 0xffff);
            hash = hash * 31 + (object.hasItemMeta() ? object.getItemMeta().hashCode() : 0);

            return hash;
        }

        @Override
        public boolean equals(ItemStack o1, ItemStack o2) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }

            return o1.isSimilar(o2);
        }
    };
}

package com.amshulman.insight.results;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;

import com.amshulman.insight.action.BlockAction;
import com.amshulman.insight.action.EntityAction;
import com.amshulman.insight.action.ItemAction;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.serialization.BlockMetadata;
import com.amshulman.insight.serialization.ItemMetadata;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.types.MaterialCompat;
import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.mbapi.util.StringUtil;
import com.amshulman.typesafety.TypeSafeList;
import com.amshulman.typesafety.impl.TypeSafeListImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResultSetFormatter {

    private static final DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");

    @Setter private static int pageSize = 9;

    private static final ChatColor HEADER_ACCENT = ChatColor.GOLD;
    private static final ChatColor HEADER_BASE = ChatColor.GRAY;
    private static final ChatColor BODY_ACCENT = ChatColor.RED;
    private static final ChatColor BODY_BASE = ChatColor.GRAY;

    public static String[] formatError(Throwable e, boolean fancyResults) {
        String msg;
        if (e == null || e.getMessage() == null) {
            msg = "An unknown error occured. Please check the logs";
        } else {
            msg = e.getMessage();
        }

        if (fancyResults) {
            msg = new Gson().toJson(new ChatMessage(msg).setColor(ChatColor.RED));
        } else {
            msg = ChatColor.RED + msg;
        }

        return new String[] { msg };
    }

    public static String[] formatResults(InsightResultSet resultSet, int pageNumber, boolean fancyResults) {
        if (resultSet == null) {
            return null;
        }

        Gson gson = fancyResults ? ChatItemHover.registerTypeAdapter(new GsonBuilder()).create() : null;
        TypeSafeList<String> messages = new TypeSafeListImpl<String>(new ArrayList<String>(pageSize + 1), CoreTypes.STRING);

        if (resultSet.getSize() == 0) {
            if (fancyResults) {
                messages.add(gson.toJson(new ChatMessage("No results found.")));
            } else {
                messages.add("No results found.");
            }

            return messages.toArray();
        }

        if (fancyResults) {
            messages.add(gson.toJson(prepareFancyHeader(resultSet, pageNumber)));
        } else {
            messages.add(buildHeader(resultSet, pageNumber));
        }

        int start = (pageNumber - 1) * pageSize;
        int end = pageNumber * pageSize;

        if (start > resultSet.getSize() || pageNumber <= 0) {
            if (fancyResults) {
                messages.add(gson.toJson(String.format("There is no page %d.", pageNumber)));
            } else {
                messages.add(String.format("There is no page %d.", pageNumber));
            }

            return messages.toArray();
        }

        if (end > resultSet.getSize()) {
            end = resultSet.getSize();
        }

        if (fancyResults) {
            buildFancyResults(gson, resultSet.getResultSubset(start, end), messages, start);
        } else {
            buildResults(resultSet.getResultSubset(start, end), messages, start);
        }

        return messages.toArray();
    }

    private static String buildHeader(InsightResultSet resultSet, int pageNumber) {
        QueryParameters params = resultSet.getQueryParameters();
        String location;

        if (params.isLocationSet() && params.getRadius() == 0) {
            location = String.format("at %s in %s", formatLocation(params.getPoint()), params.getPoint().getWorld().getName());
        } else {
            TypeSafeList<String> worlds = new TypeSafeListImpl<String>(new ArrayList<String>(params.getWorlds()), CoreTypes.STRING);
            Collections.sort(worlds.getCollection(), String.CASE_INSENSITIVE_ORDER);

            if (worlds.size() == 1) {
                location = String.format("in %s", worlds.get(0));
            } else {
                String first, second;

                if (worlds.size() == 2) {
                    first = worlds.get(0);
                    second = worlds.get(1);
                } else {
                    String worldsList = StringUtil.createList(worlds);
                    int lastComma = worldsList.lastIndexOf(',');

                    first = worldsList.substring(0, lastComma + 1);
                    second = worldsList.substring(lastComma + 2, worldsList.length());
                }

                location = String.format("across %s and %s", first, second);
            }
        }

        int totalPages = (resultSet.getSize() + pageSize - 1) / pageSize;
        return String.format("Examining changes %s, page %d of %d:", location, pageNumber, totalPages);
    }

    private static ChatRootMessage prepareFancyHeader(InsightResultSet resultSet, int pageNumber) {
        ChatRootMessage rootMessage = new ChatRootMessage();
        QueryParameters params = resultSet.getQueryParameters();

        if (params.isLocationSet() && params.getRadius() == 0) {
            rootMessage.addMessage(new ChatMessage("Examining changes at ").setColor(HEADER_BASE));
            rootMessage.addMessage(new ChatMessage(formatLocation(params.getPoint())).setColor(HEADER_ACCENT));
            rootMessage.addMessage(new ChatMessage(" in ").setColor(HEADER_BASE));
            rootMessage.addMessage(new ChatMessage(params.getPoint().getWorld().getName()).setColor(HEADER_ACCENT));
        } else {
            TypeSafeList<String> worlds = new TypeSafeListImpl<String>(new ArrayList<String>(params.getWorlds()), CoreTypes.STRING);
            Collections.sort(worlds.getCollection(), String.CASE_INSENSITIVE_ORDER);

            if (worlds.size() == 1) {
                rootMessage.addMessage(new ChatMessage("Examining changes in ").setColor(HEADER_BASE));
                rootMessage.addMessage(new ChatMessage(worlds.get(0)).setColor(HEADER_ACCENT));
            } else if (worlds.size() == 2) {
                rootMessage.addMessage(new ChatMessage("Examining changes across ").setColor(HEADER_BASE));
                rootMessage.addMessage(new ChatMessage(worlds.get(0)).setColor(HEADER_ACCENT));
                rootMessage.addMessage(new ChatMessage(" and ").setColor(HEADER_BASE));
                rootMessage.addMessage(new ChatMessage(worlds.get(1)).setColor(HEADER_ACCENT));
            } else {
                rootMessage.addMessage(new ChatMessage("Examining changes across ").setColor(HEADER_BASE));

                rootMessage.addMessage(new ChatMessage(worlds.get(0)).setColor(HEADER_ACCENT));
                for (int i = 1; i < worlds.size() - 1; ++i) {
                    rootMessage.addMessage(new ChatMessage(", ").setColor(HEADER_BASE));
                    rootMessage.addMessage(new ChatMessage(worlds.get(i)).setColor(HEADER_ACCENT));
                }
                rootMessage.addMessage(new ChatMessage(", and ").setColor(HEADER_BASE));
                rootMessage.addMessage(new ChatMessage(worlds.get(worlds.size() - 1)).setColor(HEADER_ACCENT));
            }
        }

        rootMessage.addMessage(new ChatMessage(", page ").setColor(HEADER_BASE));
        rootMessage.addMessage(new ChatMessage(Integer.toString(pageNumber)).setColor(HEADER_ACCENT));
        rootMessage.addMessage(new ChatMessage(" of ").setColor(HEADER_BASE));
        rootMessage.addMessage(new ChatMessage(Integer.toString((resultSet.getSize() + pageSize - 1) / pageSize)).setColor(HEADER_ACCENT));
        rootMessage.addMessage(new ChatMessage(":").setColor(HEADER_BASE));
        return rootMessage;
    }

    private static void buildResults(InsightResultSet resultSet, TypeSafeList<String> messages, int current) {
        QueryParameters params = resultSet.getQueryParameters();
        boolean multipleBlocks = !(params.isLocationSet() && params.getRadius() == 0);
        boolean multipleWorlds = params.getWorlds().size() > 1;

        for (InsightRecord r : resultSet) {
            StringBuilder msg = new StringBuilder();
            ++current;

            // record id
            msg.append("[").append(current).append("] ");

            msg.append(df.format(r.getDatetime())).append(' '); // date and time
            msg.append(r.getActor()).append(' '); // actor
            msg.append(r.getAction().getFriendlyDescription()); // action

            if (r.getAction() instanceof BlockAction) {
                msg.append(' ').append(MaterialCompat.getFriendlyName(r.getMaterial()));
            } else if (r.getAction() instanceof EntityAction) {
                msg.append(' ').append(r.getActee());
            } else if (r.getAction() instanceof ItemAction) {
                int quantity = 1;

                if (r.getMetadata() != null) {
                    quantity = ((ItemMetadata) r.getMetadata()).getQuantity();
                }

                msg.append(' ').append(quantity).append(' ').append(MaterialCompat.getFriendlyName(r.getMaterial()));
            }

            if (multipleBlocks) {
                msg.append(' ').append(formatLocation(r.getX(), r.getY(), r.getZ()));
            }

            if (multipleWorlds) {
                msg.append(" in " + r.getWorld());
            }

            messages.add(msg.toString());
        }
    }

    private static void buildFancyResults(Gson gson, InsightResultSet resultSet, TypeSafeList<String> messages, int current) {
        QueryParameters params = resultSet.getQueryParameters();
        boolean multipleBlocks = !(params.isLocationSet() && params.getRadius() == 0);

        for (InsightRecord r : resultSet) {
            ChatRootMessage rootMessage = new ChatRootMessage();
            ++current;

            // record id
            if (multipleBlocks) {
                String location = formatLocation(r.getX(), r.getY(), r.getZ());
                rootMessage.addMessage(new ChatMessage("[" + current + "]").setHoverEvent(new ChatTextHover(new ChatMessage(location))).setColor(BODY_ACCENT));
                rootMessage.addMessage(new ChatMessage(" "));
            } else {
                rootMessage.addMessage(new ChatMessage("[" + current + "] ").setColor(BODY_ACCENT));
            }

            // date and time
            rootMessage.addMessage(new ChatMessage(df.format(r.getDatetime()) + " ").setColor(BODY_BASE));

            // actor
            rootMessage.addMessage(new ChatMessage(r.getActor() + " "));

            // action
            rootMessage.addMessage(new ChatMessage(r.getAction().getFriendlyDescription()));

            if (r.getAction() instanceof BlockAction) {
                ChatMessage msg = new ChatMessage(" " + MaterialCompat.getFriendlyName(r.getMaterial()));
                Pair<Material, Short> mat = MaterialCompat.getBukkitMaterial(r.getMaterial().getName(), r.getMaterial().getSubtype());
                ChatItemHover hover;

                switch (mat.getValue0()) {
                    case REDSTONE_WIRE:
                        hover = new ChatItemHover(Material.REDSTONE);
                        break;
                    case SIGN_POST:
                        hover = new ChatItemHover(Material.SIGN);
                        break;
                    case WOODEN_DOOR:
                        hover = new ChatItemHover(Material.WOOD_DOOR);
                        break;
                    case IRON_DOOR_BLOCK:
                        hover = new ChatItemHover(Material.IRON_DOOR);
                        break;
                    case SUGAR_CANE_BLOCK:
                        hover = new ChatItemHover(Material.SUGAR_CANE);
                        break;
                    case CAKE_BLOCK:
                        hover = new ChatItemHover(Material.CAKE);
                        break;
                    case NETHER_WARTS:
                        hover = new ChatItemHover(Material.NETHER_STALK);
                        break;
                    case BREWING_STAND:
                        hover = new ChatItemHover(Material.BREWING_STAND_ITEM);
                        break;
                    case CAULDRON:
                        hover = new ChatItemHover(Material.CAULDRON_ITEM);
                        break;
                    case COCOA:
                        hover = new ChatItemHover(Material.INK_SACK);
                        hover.setDamage((short) 3);
                        break;
                    case TRIPWIRE:
                        hover = new ChatItemHover(Material.STRING);
                        break;
                    case FLOWER_POT:
                        hover = new ChatItemHover(Material.FLOWER_POT_ITEM);
                        break;
                    case CARROT:
                        hover = new ChatItemHover(Material.CARROT_ITEM);
                        break;
                    case POTATO:
                        hover = new ChatItemHover(Material.POTATO_ITEM);
                        break;
                    case SKULL:
                        ItemStack skull = ((SkullMeta) ((BlockMetadata) r.getMetadata()).getMeta()).getItemStack();
                        hover = new ChatItemHover(Material.SKULL_ITEM);
                        hover.setDamage(skull.getDurability());
                        hover.setMetadata(skull.getItemMeta());
                        break;
                    case REDSTONE_COMPARATOR_OFF:
                    case REDSTONE_COMPARATOR_ON:
                        hover = new ChatItemHover(Material.REDSTONE_COMPARATOR);
                        break;
                    default:
                        hover = new ChatItemHover(mat.getValue0());
                        if (mat.getValue1() != 0) {
                            hover.setDamage(mat.getValue1());
                        }
                        break;
                }

                rootMessage.addMessage(msg.setHoverEvent(hover));
            } else if (r.getAction() instanceof EntityAction) {
                if (r.getAction() != EventCompat.ENTITY_DEATH) {
                    rootMessage.addMessage(new ChatMessage(" " + r.getActee()));
                }
            } else if (r.getAction() instanceof ItemAction) {
                Pair<Material, Short> mat = MaterialCompat.getBukkitMaterial(r.getMaterial().getName(), r.getMaterial().getSubtype());
                ChatItemHover hover = new ChatItemHover(mat.getValue0());
                int quantity = 1;

                if (mat.getValue1() != 0) {
                    hover.setDamage(mat.getValue1());
                }

                if (r.getMetadata() != null) {
                    ItemMetadata itemMeta = (ItemMetadata) r.getMetadata();

                    quantity = itemMeta.getQuantity();
                    hover.setMetadata(itemMeta.getMeta());
                }

                rootMessage.addMessage(new ChatMessage(" " + quantity + " "));
                rootMessage.addMessage(new ChatMessage(MaterialCompat.getFriendlyName(r.getMaterial())).setHoverEvent(hover));
            }

            messages.add(gson.toJson(rootMessage));
        }
    }

    private static String formatLocation(Location l) {
        return formatLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    private static String formatLocation(int x, int y, int z) {
        return String.format("<%d, %d, %d>", x, y, z);
    }
}

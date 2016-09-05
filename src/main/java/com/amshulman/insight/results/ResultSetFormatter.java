package com.amshulman.insight.results;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;

import com.amshulman.insight.action.BlockAction;
import com.amshulman.insight.action.EntityAction;
import com.amshulman.insight.action.ItemAction;
import com.amshulman.insight.query.QueryParameters;
import com.amshulman.insight.results.UnformattedResults.ResultRow;
import com.amshulman.insight.serialization.BlockMetadata;
import com.amshulman.insight.serialization.ItemMetadata;
import com.amshulman.insight.serialization.SignMeta;
import com.amshulman.insight.serialization.SkullMeta;
import com.amshulman.insight.types.EventCompat;
import com.amshulman.insight.types.InsightLocation;
import com.amshulman.insight.types.MaterialCompat;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ResultSetFormatter {

    static DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss");

    @NonFinal @Setter static int pageSize = 9;

    static ChatColor HEADER_ACCENT = ChatColor.GOLD;
    static ChatColor HEADER_BASE = ChatColor.GRAY;
    static ChatColor BODY_ACCENT = ChatColor.RED;
    static ChatColor BODY_BASE = ChatColor.GRAY;

    @Getter InsightResultSet resultSet;
    int pageNumber;
    boolean multipleBlocks;
    boolean multipleWorlds;
    UnformattedResults results = new UnformattedResults();

    public ResultSetFormatter(InsightResultSet rs, int pageNumber) {
        resultSet = rs;
        this.pageNumber = pageNumber;

        QueryParameters params = rs.getQueryParameters();
        multipleBlocks = !(params.isLocationSet() && params.getRadius() == 0);
        multipleWorlds = params.getWorlds().size() != 1;

        prepareResults();
    }

    public void sendResults(CommandSender sender) {
        if (sender == null) {
            return;
        } else if (sender instanceof Player) {
            FancyTextFormatter.send((Player) sender, results);
        } else {
            PlainTextFormatter.send(sender, results);
        }
    }

    private void prepareResults() {
        if (resultSet == null) {
            throw new NullPointerException();
        }

        if (resultSet.getSize() == 0) {
            results.getHeaderRow().setError("No results found.").setColor(HEADER_BASE);
            return;
        }

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(pageNumber * pageSize, resultSet.getSize());

        if (start > resultSet.getSize() || pageNumber <= 0) {
            results.getHeaderRow().setError(String.format("There is no page %d.", pageNumber)).setColor(HEADER_BASE);
            return;
        }

        buildHeader();

        int rowNumber = 1;
        for (InsightRecord<?> r : resultSet.getResultSubset(start, end)) {
            buildResultRow(r, rowNumber);
            ++rowNumber;
        }
    }

    private void buildHeader() {
        QueryParameters params = resultSet.getQueryParameters();

        if (params.isLocationSet() && params.getRadius() == 0) {
            StringBuilder sb = new StringBuilder(100);
            sb.append(HEADER_BASE).append("Examining changes at ");
            sb.append(HEADER_ACCENT).append(formatLocation(params.getPoint()));
            sb.append(HEADER_BASE).append(" in ");
            results.getHeaderRow().setExamineMessage(sb.toString());
            results.getHeaderRow().setNumWorldsMessage(HEADER_ACCENT + params.getPoint().getWorld());
        } else {
            String[] worlds = params.getWorlds().toArray(new String[0]);
            Arrays.sort(worlds, String.CASE_INSENSITIVE_ORDER);

            if (worlds.length == 1) {
                results.getHeaderRow().setExamineMessage(HEADER_BASE + "Examining changes in ");
                results.getHeaderRow().setNumWorldsMessage(HEADER_ACCENT + worlds[0]);
            } else {
                results.getHeaderRow().setExamineMessage(HEADER_BASE + "Examining changes across ");
                results.getHeaderRow().setNumWorldsMessage(HEADER_ACCENT + Integer.toString(worlds.length) + " worlds");
                results.getHeaderRow().setWorldsHoverMessage(listWorlds(worlds));
            }
        }

        StringBuilder sb = new StringBuilder(100);
        sb.append(HEADER_BASE).append(", page ");
        sb.append(HEADER_ACCENT).append(pageNumber);
        sb.append(HEADER_BASE).append(" of ");
        sb.append(HEADER_ACCENT).append((resultSet.getSize() + pageSize - 1) / pageSize);
        sb.append(HEADER_BASE).append(":");
        results.getHeaderRow().setPagesMessage(sb.toString());
    }

    private String listWorlds(String[] worlds) {
        StringBuilder sb = new StringBuilder(100);
        if (worlds.length == 1) {
            sb.append(HEADER_ACCENT).append(worlds[0]);
        } else if (worlds.length == 2) {
            sb.append(HEADER_ACCENT).append(worlds[0]);
            sb.append(HEADER_BASE).append(" and ");
            sb.append(HEADER_ACCENT).append(worlds[1]);
        } else {
            sb.append(HEADER_ACCENT).append(worlds[0]);
            for (int i = 1; i < worlds.length - 1; ++i) {
                sb.append(HEADER_BASE).append(", ");
                sb.append(HEADER_ACCENT).append(worlds[1]);
            }
            sb.append(HEADER_BASE).append(", and ");
            sb.append(HEADER_ACCENT).append(worlds[worlds.length - 1]);
        }
        return sb.toString();
    }

    private void buildResultRow(InsightRecord<?> r, int rowNumber) {
        ResultRow resultRow = results.addResultRow();
        resultRow.setRowNumber("[" + rowNumber + "]").setColor(BODY_ACCENT);

        if (multipleBlocks) {
            String location = formatLocation(r.getLocation());
            if (multipleWorlds) {
                location += " in " + r.getLocation().getWorld();
            }
            resultRow.setLocation(location).setColor(BODY_BASE);
        }

        // date and time
        resultRow.setDatetime(DATE_FORMAT.format(r.getDatetime())).setColor(BODY_BASE);

        // actor
        resultRow.setActor(r.getActor());

        // action
        resultRow.setAction(r.getAction().getFriendlyDescription());

        // actee/material and hover
        if (r.getAction() instanceof BlockAction) {
            resultRow.setActeeOrMaterial(MaterialCompat.getFriendlyName(r.getMaterial()));
            resultRow.setActeeOrMaterialHover(getBlockHover(r));
        } else if (r.getAction() instanceof EntityAction) {
            if (r.getAction() != EventCompat.ENTITY_DEATH) {
                resultRow.setActeeOrMaterial(r.getActee());
            }
        } else if (r.getAction() instanceof ItemAction) {
            String item = MaterialCompat.getFriendlyName(r.getMaterial());
            if (r.getAction() != EventCompat.ITEM_ROTATE) {
                int quantity = r.getMetadata() == null ? 1 : ((ItemMetadata) r.getMetadata()).getQuantity();
                item = quantity + " " + item;
            }

            resultRow.setActeeOrMaterial(item);
            resultRow.setActeeOrMaterialHover(getItemHover(r));
        }
    }

    private static ChatHover getBlockHover(InsightRecord<?> r) {
        Pair<Material, Short> mat = MaterialCompat.getBukkitMaterial(r.getMaterial().getName(), r.getMaterial().getSubtype());

        try {
            if (r.getAction() == EventCompat.SIGN_CHANGE) {
                SignMeta meta = (SignMeta) ((BlockMetadata) r.getMetadata()).getMeta();
                return new ChatTextHover(StringUtils.join(meta.getText(), '\n'));
            } else {
                switch (mat.getValue0()) {
                case BED_BLOCK:
                    return new ChatItemHover(Material.BED);
                case REDSTONE_WIRE:
                    return new ChatItemHover(Material.REDSTONE);
                case SIGN_POST:
                case WALL_SIGN:
                    return new ChatItemHover(Material.SIGN);
                case WOODEN_DOOR:
                    return new ChatItemHover(Material.WOOD_DOOR);
                case IRON_DOOR_BLOCK:
                    return new ChatItemHover(Material.IRON_DOOR);
                case SUGAR_CANE_BLOCK:
                    return new ChatItemHover(Material.SUGAR_CANE);
                case CAKE_BLOCK:
                    return new ChatItemHover(Material.CAKE);
                case NETHER_WARTS:
                    return new ChatItemHover(Material.NETHER_STALK);
                case BREWING_STAND:
                    return new ChatItemHover(Material.BREWING_STAND_ITEM);
                case CAULDRON:
                    return new ChatItemHover(Material.CAULDRON_ITEM);
                case COCOA:
                    return new ChatItemHover(Material.INK_SACK, (short) 3);
                case TRIPWIRE:
                    return new ChatItemHover(Material.STRING);
                case FLOWER_POT:
                    return new ChatItemHover(Material.FLOWER_POT_ITEM);
                case CARROT:
                    return new ChatItemHover(Material.CARROT_ITEM);
                case POTATO:
                    return new ChatItemHover(Material.POTATO_ITEM);
                case SKULL:
                    ItemStack skull = ((SkullMeta) ((BlockMetadata) r.getMetadata()).getMeta()).getItemStack();
                    ChatItemHover skullHover = new ChatItemHover(Material.SKULL_ITEM, skull.getDurability());
                    skullHover.setMetadata(skull.getItemMeta());
                    return skullHover;
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                    return new ChatItemHover(Material.REDSTONE_COMPARATOR);
                case AIR:
                    return null;
                default:
                    return new ChatItemHover(mat.getValue0(), mat.getValue1());
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Unreadable or missing metadata at " + r);
            return null;
        }
    }

    private static ChatHover getItemHover(InsightRecord<?> r) {
        Pair<Material, Short> mat = MaterialCompat.getBukkitMaterial(r.getMaterial().getName(), r.getMaterial().getSubtype());
        ChatItemHover hover = new ChatItemHover(mat.getValue0(), mat.getValue1());
        if (r.getMetadata() != null) {
            ItemMetadata itemMeta = (ItemMetadata) r.getMetadata();
            hover.setMetadata(itemMeta.getMeta());
        }
        return hover;
    }

    private static String formatLocation(InsightLocation l) {
        return formatLocation(l.getX(), l.getY(), l.getZ());
    }

    private static String formatLocation(int x, int y, int z) {
        return String.format("<%d, %d, %d>", x, y, z);
    }
}

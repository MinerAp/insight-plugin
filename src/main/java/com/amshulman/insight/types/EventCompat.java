package com.amshulman.insight.types;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import com.amshulman.insight.action.BlockAction;
import com.amshulman.insight.action.BlockAction.BlockRollbackAction;
import com.amshulman.insight.action.EntityAction;
import com.amshulman.insight.action.EntityAction.EntityRollbackAction;
import com.amshulman.insight.action.ItemAction;
import com.amshulman.insight.action.ItemAction.ItemRollbackAction;
import com.amshulman.insight.action.impl.BlockActionImpl;
import com.amshulman.insight.action.impl.EntityActionImpl;
import com.amshulman.insight.action.impl.ItemActionImpl;
import com.amshulman.insight.types.EventRegistry;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class EventCompat {

    /* Block Actions */
    static BlockAction BLOCK_PLACE = createBlockAction("BLOCK_PLACE", "placed", BlockActionImpl.REMOVE);
    static BlockAction BUCKET_PLACE = createBlockAction("BUCKET_PLACE", "placed", BlockActionImpl.REMOVE);
    static BlockAction ENDERMAN_PLACE = createBlockAction("ENDERMAN_PLACE", "placed", BlockActionImpl.REMOVE);

    static BlockAction BLOCK_BREAK = createBlockAction("BLOCK_BREAK", "broke", BlockActionImpl.PLACE);
    static BlockAction BUCKET_REMOVE = createBlockAction("BUCKET_REMOVE", "removed", BlockActionImpl.PLACE);
    static BlockAction BLOCK_BURN = createBlockAction("BLOCK_BURN", "burned", BlockActionImpl.PLACE);
    static BlockAction BLOCK_EXPLODE = createBlockAction("BLOCK_EXPLODE", "blew up", BlockActionImpl.PLACE);
    static BlockAction ENDERMAN_REMOVE = createBlockAction("ENDERMAN_REMOVE", "removed", BlockActionImpl.PLACE);

    static BlockAction BLOCK_MELT = createBlockAction("BLOCK_MELT", "melted", BlockActionImpl.PLACE);
    static BlockAction BLOCK_FORM = createBlockAction("BLOCK_FORM", "formed", BlockActionImpl.REMOVE);
    static BlockAction BLOCK_GROW = createBlockAction("BLOCK_GROW", "grew", BlockActionImpl.REMOVE);
    static BlockAction BLOCK_DIE = createBlockAction("BLOCK_DIE", "killed", BlockActionImpl.PLACE);
    static BlockAction BLOCK_DROP = createBlockAction("BLOCK_DROP", "dropped", null); // TODO
    static BlockAction SHEEP_EAT = createBlockAction("SHEEP_EAT", "ate", BlockActionImpl.PLACE);
    static BlockAction SOIL_TILL = createBlockAction("SOIL_TILL", "tilled", BlockActionImpl.PLACE);
    static BlockAction SOIL_TRAMPLE = createBlockAction("SOIL_TRAMPLE", "trampled", BlockActionImpl.PLACE);
    static BlockAction SOIL_REVERT = createBlockAction("SOIL_REVERT", "deteriorated", BlockActionImpl.PLACE);

    static BlockAction BLOCK_IGNITE = createBlockAction("BLOCK_IGNITE", "created", BlockActionImpl.REMOVE);
    static BlockAction FIRE_SPREAD = createBlockAction("FIRE_SPREAD", "spread", BlockActionImpl.REMOVE);

    static BlockAction BLOCK_FLOW = createBlockAction("BLOCK_FLOW", "flowed into", BlockActionImpl.PLACE); // Opposite of normal because water "removes" other blocks
    static BlockAction BLOCK_TELEPORT = createBlockAction("BLOCK_TELEPORT", "teleported", null); // TODO

    static BlockAction SIGN_CHANGE = createBlockAction("SIGN_CHANGE", "wrote", BlockActionImpl.NOTHING);

    /* Entity Actions */
    static EntityAction ENTITY_DEATH = createEntityAction("ENTITY_DEATH", "died", null); // TODO
    static EntityAction ENTITY_KILL = createEntityAction("ENTITY_KILL", "killed", null); // TODO

    static EntityAction HANGING_PLACE = createEntityAction("HANGING_PLACE", "placed", null); // TODO
    static EntityAction HANGING_BREAK = createEntityAction("HANGING_BREAK", "broke", null); // TODO

    static EntityAction EXP_GAIN = createEntityAction("EXP_GAIN", "picked up", EntityActionImpl.NOTHING);
    static EntityAction SHEEP_DYE = createEntityAction("SHEEP_DYE", "dyed", EntityActionImpl.NOTHING);
    static EntityAction SHEEP_SHEAR = createEntityAction("SHEEP_SHEAR", "sheared", EntityActionImpl.NOTHING);

    static EntityAction VEHICLE_ENTER = createEntityAction("VEHICLE_ENTER", "entered", EntityActionImpl.NOTHING);
    static EntityAction VEHICLE_EXIT = createEntityAction("VEHICLE_EXIT", "exited", EntityActionImpl.NOTHING);

    /* Item Actions */
    static ItemAction ITEM_INSERT = createItemAction("ITEM_INSERT", "inserted", ItemActionImpl.WITHDRAW);
    static ItemAction CRAFTING_INSERT = createItemAction("CRAFT_INSERT", "inserted", ItemActionImpl.NOTHING);
    static ItemAction ENDERCHEST_INSERT = createItemAction("EC_INSERT", "inserted", ItemActionImpl.NOTHING);

    static ItemAction ITEM_REMOVE = createItemAction("ITEM_REMOVE", "removed", ItemActionImpl.INSERT);
    static ItemAction CRAFTING_REMOVE = createItemAction("CRAFT_REMOVE", "removed", ItemActionImpl.NOTHING);
    static ItemAction ENDERCHEST_REMOVE = createItemAction("EC_REMOVE", "removed", ItemActionImpl.NOTHING);

    static ItemAction ITEM_DROP = createItemAction("ITEM_DROP", "dropped", ItemActionImpl.NOTHING);
    static ItemAction ITEM_PICKUP = createItemAction("ITEM_PICKUP", "picked up", ItemActionImpl.NOTHING);
    static ItemAction ITEM_BURN = createItemAction("ITEM_BURN", "burned", ItemActionImpl.NOTHING);

    static ItemAction ITEM_ROTATE = createItemAction("ITEM_ROTATE", "rotated", ItemActionImpl.NOTHING);

    /* Intransitive Actions */
    //

    static {
        EventRegistry.addActionsToAlias("PLACE", BLOCK_PLACE, BUCKET_PLACE, ENDERMAN_PLACE);
        EventRegistry.addActionsToAlias("BREAK", BLOCK_BREAK, BUCKET_REMOVE, BLOCK_BURN, BLOCK_EXPLODE, ENDERMAN_REMOVE);
        EventRegistry.addActionsToAlias("CHANGE", BLOCK_MELT, BLOCK_FORM, BLOCK_GROW, BLOCK_DIE, BLOCK_DROP, SHEEP_EAT);
        EventRegistry.addActionsToAlias("SPREAD", FIRE_SPREAD, BLOCK_IGNITE);

        EventRegistry.addActionsToAlias("INSERT", ITEM_INSERT);
        EventRegistry.addActionsToAlias("REMOVE", ITEM_REMOVE);
        EventRegistry.addActionsToAlias("DROP", ITEM_DROP);
        EventRegistry.addActionsToAlias("PICKUP", ITEM_PICKUP);
    }

    private static BlockAction createBlockAction(String name, String friendlyDescription, BlockRollbackAction rollbackAction) {
        BlockAction action = new BlockActionImpl(name, friendlyDescription, rollbackAction);
        EventRegistry.addAction(action);
        return action;
    }

    private static EntityAction createEntityAction(String name, String friendlyDescription, EntityRollbackAction rollbackAction) {
        EntityAction action = new EntityActionImpl(name, friendlyDescription, rollbackAction);
        EventRegistry.addAction(action);
        return action;
    }

    private static ItemAction createItemAction(String name, String friendlyDescription, ItemRollbackAction rollbackAction) {
        ItemAction action = new ItemActionImpl(name, friendlyDescription, rollbackAction);
        EventRegistry.addAction(action);
        return action;
    }
}
